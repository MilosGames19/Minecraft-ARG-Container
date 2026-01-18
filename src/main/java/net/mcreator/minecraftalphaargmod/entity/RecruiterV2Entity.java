package net.mcreator.minecraftalphaargmod.entity;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

import net.mcreator.minecraftalphaargmod.init.TheArgContainerModEntities;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class RecruiterV2Entity extends PathfinderMob {
    public int stareTimer = 0;
    public boolean getTheFrickOut = false;
    private boolean initializedSpawnPos = false;
    private int ticksSinceSpawn = 0;
    private static final int GRACE_TICKS = 20;
    private boolean controllerMode = false;
    private boolean spawnedThisCycle = false;
    
    // Building mode variables
    private boolean buildingMode = false;
    private boolean autoBuildEnabled = false; // Auto-build after delay
    private int autoBuildTimer = 0;
    private static final int AUTO_BUILD_DELAY = 1200; // 10 seconds (20 ticks/sec * 10)
    private BuildingPhase currentPhase = BuildingPhase.IDLE;
    private BlockPos buildLocation = null;
    private int buildingTimer = 0;
    private List<BlockPlacement> blocksToPlace = new ArrayList<>();
    private int currentBlockIndex = 0;
    private StructureType structureType = StructureType.SMALL_T;
    private static final double MAX_REACH = 10; // Player-like reach distance

    private enum BuildingPhase {
        IDLE,
        MOVING_TO_BUILD,
        BUILDING,
        COMPLETE
    }

    private enum StructureType {
        SMALL_T,  // 3x3x1 T shape
        BIG_T     // 6x6x2 T shape
    }

    private static class BlockPlacement {
        BlockPos pos;
        boolean isAir; // true for air blocks, false for cobblestone
        
        BlockPlacement(BlockPos pos, boolean isAir) {
            this.pos = pos;
            this.isAir = isAir;
        }
    }

    public RecruiterV2Entity(PlayMessages.SpawnEntity packet, Level world) {
        this(TheArgContainerModEntities.RECRUITER_V_2.get(), world);
    }

    public RecruiterV2Entity(EntityType<RecruiterV2Entity> type, Level world) {
        super(type, world);
        setMaxUpStep(0.6f);
        xpReward = 0;
        setNoAi(false);
        setCustomName(Component.literal(" "));
        setCustomNameVisible(true);
        
        // Enable auto-build with random delay on spawn
        this.autoBuildEnabled = true;
        // Random delay between 10-30 seconds
        this.autoBuildTimer = (200 + this.random.nextInt(400));
        // 50% chance for big T, 50% for small T
        this.structureType = this.random.nextBoolean() ? StructureType.BIG_T : StructureType.SMALL_T;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new BuildingGoal(this));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 100.0F));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) return;

        ticksSinceSpawn++;

        if (!initializedSpawnPos) {
            initializedSpawnPos = true;
            if (this.level() instanceof ServerLevel sl) {
                Player p = sl.getNearestPlayer(this, 256.0D);
                if (p != null) {
                    double angle = this.getRandom().nextDouble() * (Math.PI * 2.0);
                    double radius = 24.0 + this.getRandom().nextDouble() * 24.0;
                    double tx = p.getX() + Math.cos(angle) * radius;
                    double tz = p.getZ() + Math.sin(angle) * radius;
                    int ty = sl.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(tx), Mth.floor(tz));
                    this.moveTo(tx + 0.5D, ty + 1.0D, tz + 0.5D, 0.0F, 0.0F);
                } else {
                    var spawn = sl.getSharedSpawnPos();
                    int y = sl.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawn.getX(), spawn.getZ());
                    this.moveTo(spawn.getX() + 0.5D, y + 1.0D, spawn.getZ() + 0.5D, 0.0F, 0.0F);
                }
            }
        }

        // Auto-build timer countdown
        if (autoBuildEnabled && !buildingMode) {
            autoBuildTimer--;
            if (autoBuildTimer <= 0) {
                // Start building automatically
                enableBuildingMode(structureType);
                autoBuildEnabled = false; // Only build once
            }
        }

        // Check if player is staring while building
        if (buildingMode && currentPhase == BuildingPhase.BUILDING) {
            Player player = this.level().getNearestPlayer(this, 32.0D);
            if (player != null && isPlayerStaringAtMe(player)) {
                // Player caught us building - disappear immediately
                quickGlanceAndVanish(player);
                return;
            }
        }

        if (buildingMode) {
            // Building mode overrides normal behavior
            return;
        }

        if (controllerMode) {
            if (!this.spawnedThisCycle) {
                gone();
                return;
            }
            this.spawnedThisCycle = false;
        }

        Player player = this.level().getNearestPlayer(this, 256.0D);
        if (player == null) return;

        double yaw = Math.toDegrees(Math.atan2(this.getX() - player.getX(), -(this.getZ() - player.getZ())));
        float yawF = (float) yaw;
        this.setYRot(yawF);
        this.setYHeadRot(yawF);
        this.setYBodyRot(yawF);

        float dist = this.distanceTo(player);
        if (ticksSinceSpawn > GRACE_TICKS && dist < 19.0F) {
            gone();
            return;
        }

        boolean hasLoS = this.hasLineOfSight(player);
        float diff = Mth.wrapDegrees(player.getYRot() - this.getYRot());
        float closenessTo180 = Math.abs(Math.abs(diff) - 180.0F);
        if (closenessTo180 < 10.0F && hasLoS) {
            this.stareTimer++;
            if (this.stareTimer > 70) {
                this.getTheFrickOut = true;
                gone();
            }
        } else {
            if (this.stareTimer > 0) this.stareTimer = Math.max(0, this.stareTimer - 1);
        }
    }

    private boolean isPlayerStaringAtMe(Player player) {
        if (!this.hasLineOfSight(player)) return false;
        
        // Calculate if player is looking at the entity
        double dx = this.getX() - player.getX();
        double dy = this.getY() + this.getEyeHeight() - (player.getY() + player.getEyeHeight());
        double dz = this.getZ() - player.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        
        // Calculate angle to entity from player's perspective
        double angleToEntity = Math.toDegrees(Math.atan2(-dx, dz));
        float diff = Mth.wrapDegrees((float)angleToEntity - player.getYRot());
        
        // Player is staring if looking within 20 degrees
        return Math.abs(diff) < 20.0F;
    }

    private void quickGlanceAndVanish(Player player) {
        // Quick look at player
        this.getLookControl().setLookAt(player, 180.0F, 180.0F);
        
        // Play vanish effect
        this.level().broadcastEntityEvent(this, (byte) 60); // Death particles
        
        // Immediate disappearance
        this.discard();
    }

    public void enableControllerMode(boolean enabled) {
        this.controllerMode = enabled;
    }

    public void markSpawnedThisCycle() {
        this.spawnedThisCycle = true;
    }

    public void enableBuildingMode() {
        enableBuildingMode(StructureType.SMALL_T);
    }

    public void enableBuildingMode(StructureType type) {
        this.buildingMode = true;
        this.structureType = type;
        this.currentPhase = BuildingPhase.MOVING_TO_BUILD;
        this.blocksToPlace.clear();
        this.currentBlockIndex = 0;
        
        // Disable auto-build if manually triggered
        this.autoBuildEnabled = false;
        
        // Hold cobblestone in hand
        this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.COBBLESTONE));
        
        // Set build location near the entity
        if (this.level() instanceof ServerLevel sl) {
            int bx = Mth.floor(this.getX()) + this.random.nextInt(10) - 5;
            int bz = Mth.floor(this.getZ()) + this.random.nextInt(10) - 5;
            int by = sl.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, bx, bz);
            this.buildLocation = new BlockPos(bx, by, bz);
            
            // Plan the structure
            planStructure();
        }
    }

    private void planStructure() {
        if (buildLocation == null) return;
        
        if (structureType == StructureType.SMALL_T) {
            planSmallT();
        } else {
            planBigT();
        }
    }

    private void planSmallT() {
        // Small T structure (3x3x1)
        // Bottom row (3 cobblestone)
        blocksToPlace.add(new BlockPlacement(buildLocation.offset(0, 0, 0), false)); // cobblestone
        blocksToPlace.add(new BlockPlacement(buildLocation.offset(1, 0, 0), false)); // cobblestone
        blocksToPlace.add(new BlockPlacement(buildLocation.offset(2, 0, 0), false)); // cobblestone
        
        // Middle row (air, cobblestone, air)
        blocksToPlace.add(new BlockPlacement(buildLocation.offset(0, 1, 0), true));  // air
        blocksToPlace.add(new BlockPlacement(buildLocation.offset(1, 1, 0), false)); // cobblestone
        blocksToPlace.add(new BlockPlacement(buildLocation.offset(2, 1, 0), true));  // air
        
        // Top row (air, cobblestone, air)
        blocksToPlace.add(new BlockPlacement(buildLocation.offset(0, 2, 0), true));  // air
        blocksToPlace.add(new BlockPlacement(buildLocation.offset(1, 2, 0), false)); // cobblestone
        blocksToPlace.add(new BlockPlacement(buildLocation.offset(2, 2, 0), true));  // air
    }

    private void planBigT() {
        // Big T structure (6x6x2)
        // Build from the structure definition
        
        // Y=0 layer (all cobblestone, 6x2)
        for (int x = 0; x < 6; x++) {
            for (int z = 0; z < 2; z++) {
                blocksToPlace.add(new BlockPlacement(buildLocation.offset(x, 0, z), false));
            }
        }
        
        // Y=1 layer (all cobblestone, 6x2)
        for (int x = 0; x < 6; x++) {
            for (int z = 0; z < 2; z++) {
                blocksToPlace.add(new BlockPlacement(buildLocation.offset(x, 1, z), false));
            }
        }
        
        // Y=2 layer (T shape)
        // Sides (air)
        for (int x = 0; x < 2; x++) {
            for (int z = 0; z < 2; z++) {
                blocksToPlace.add(new BlockPlacement(buildLocation.offset(x, 2, z), true));
            }
        }
        // Center (cobblestone)
        for (int x = 2; x < 4; x++) {
            for (int z = 0; z < 2; z++) {
                blocksToPlace.add(new BlockPlacement(buildLocation.offset(x, 2, z), false));
            }
        }
        // Sides (air)
        for (int x = 4; x < 6; x++) {
            for (int z = 0; z < 2; z++) {
                blocksToPlace.add(new BlockPlacement(buildLocation.offset(x, 2, z), true));
            }
        }
        
        // Y=3,4,5 layers (vertical part of T)
        for (int y = 3; y <= 5; y++) {
            // Sides (air)
            for (int x = 0; x < 2; x++) {
                for (int z = 0; z < 2; z++) {
                    blocksToPlace.add(new BlockPlacement(buildLocation.offset(x, y, z), true));
                }
            }
            // Center (cobblestone)
            for (int x = 2; x < 4; x++) {
                for (int z = 0; z < 2; z++) {
                    blocksToPlace.add(new BlockPlacement(buildLocation.offset(x, y, z), false));
                }
            }
            // Sides (air)
            for (int x = 4; x < 6; x++) {
                for (int z = 0; z < 2; z++) {
                    blocksToPlace.add(new BlockPlacement(buildLocation.offset(x, y, z), true));
                }
            }
        }
    }

    private void gone() {
        this.discard();
    }

    private void teleportRandomly() {
        if (!(this.level() instanceof ServerLevel sl)) return;
        
        // Teleport 30-50 blocks away in random direction
        double angle = this.random.nextDouble() * Math.PI * 2.0;
        double distance = 30.0 + this.random.nextDouble() * 20.0;
        
        double newX = this.getX() + Math.cos(angle) * distance;
        double newZ = this.getZ() + Math.sin(angle) * distance;
        int newY = sl.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(newX), Mth.floor(newZ));
        
        this.teleportTo(newX, newY + 1.0, newZ);
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEFINED;
    }

    @Override
    public double getMyRidingOffset() {
        return -0.35D;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.hurt"));
    }

    @Override
    public SoundEvent getDeathSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.death"));
    }

    @Override
    public boolean hurt(DamageSource damagesource, float amount) {
        if (damagesource.is(DamageTypes.IN_FIRE)) return false;
        if (damagesource.getDirectEntity() instanceof AbstractArrow) return false;
        if (damagesource.getDirectEntity() instanceof Player) return false;
        if (damagesource.getDirectEntity() instanceof ThrownPotion || damagesource.getDirectEntity() instanceof AreaEffectCloud) return false;
        if (damagesource.is(DamageTypes.FALL)) return false;
        if (damagesource.is(DamageTypes.CACTUS)) return false;
        if (damagesource.is(DamageTypes.DROWN)) return false;
        if (damagesource.is(DamageTypes.LIGHTNING_BOLT)) return false;
        if (damagesource.is(DamageTypes.EXPLOSION) || damagesource.is(DamageTypes.PLAYER_EXPLOSION)) return false;
        if (damagesource.is(DamageTypes.TRIDENT)) return false;
        if (damagesource.is(DamageTypes.FALLING_ANVIL)) return false;
        if (damagesource.is(DamageTypes.DRAGON_BREATH)) return false;
        if (damagesource.is(DamageTypes.WITHER) || damagesource.is(DamageTypes.WITHER_SKULL)) return false;
        return super.hurt(damagesource, amount);
    }

    @Override
    public boolean ignoreExplosion() {
        return true;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    public static void init() {
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder = builder.add(Attributes.MAX_HEALTH, 20);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
        builder = builder.add(Attributes.FOLLOW_RANGE, 32);
        return builder;
    }

    // AI Goal for building
    private class BuildingGoal extends Goal {
        private final RecruiterV2Entity entity;

        public BuildingGoal(RecruiterV2Entity entity) {
            this.entity = entity;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return entity.buildingMode;
        }

        @Override
        public void tick() {
            switch (entity.currentPhase) {
                case MOVING_TO_BUILD:
                    moveToLocation();
                    break;
                case BUILDING:
                    buildStructure();
                    break;
            }
        }

        private void moveToLocation() {
            if (entity.buildLocation == null) {
                entity.currentPhase = BuildingPhase.COMPLETE;
                return;
            }

            entity.getNavigation().moveTo(entity.buildLocation.getX(), entity.buildLocation.getY(), entity.buildLocation.getZ(), 1.0);

            if (entity.distanceToSqr(entity.buildLocation.getX(), entity.buildLocation.getY(), entity.buildLocation.getZ()) < 25.0) {
                entity.currentPhase = BuildingPhase.BUILDING;
                entity.currentBlockIndex = 0;
            }
        }

        private void buildStructure() {
            if (entity.currentBlockIndex >= entity.blocksToPlace.size()) {
                // Building complete
                entity.currentPhase = BuildingPhase.COMPLETE;
                entity.buildingMode = false;
                
                // Remove cobblestone from hand
                entity.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                
                // Teleport to random location
                entity.teleportRandomly();
                return;
            }

            BlockPlacement placement = entity.blocksToPlace.get(entity.currentBlockIndex);
            
            // Check if block is within reach
            double distance = entity.distanceToSqr(placement.pos.getX() + 0.5, placement.pos.getY() + 0.5, placement.pos.getZ() + 0.5);
            if (distance > MAX_REACH * MAX_REACH) {
                // Move closer to the block
                entity.getNavigation().moveTo(placement.pos.getX(), placement.pos.getY(), placement.pos.getZ(), 1.0);
                return;
            }
            
            // Look at placement location
            entity.getLookControl().setLookAt(placement.pos.getX() + 0.5, placement.pos.getY() + 0.5, placement.pos.getZ() + 0.5);
            
            entity.buildingTimer++;
            
            // Place block every 15 ticks (0.75 seconds)
            if (entity.buildingTimer >= 4) {
                BlockState currentState = entity.level().getBlockState(placement.pos);
                
                if (!placement.isAir) {
                    // Place cobblestone
                    if (currentState.isAir() || currentState.canBeReplaced()) {
                        // Play placing animation
                        entity.swing(InteractionHand.MAIN_HAND);
                        
                        // Place the block
                        entity.level().setBlock(placement.pos, Blocks.COBBLESTONE.defaultBlockState(), 3);
                        
                        // Play block place sound
                        entity.level().playSound(null, placement.pos, SoundEvents.STONE_PLACE, 
                            entity.getSoundSource(), 1.0F, 1.0F);
                    }
                } else {
                    // Clear block (place air) if needed
                    if (!currentState.isAir()) {
                        entity.level().setBlock(placement.pos, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
                
                entity.currentBlockIndex++;
                entity.buildingTimer = 0;
            }
        }
    }
}