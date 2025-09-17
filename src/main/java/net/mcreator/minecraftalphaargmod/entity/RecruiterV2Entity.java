package net.mcreator.minecraftalphaargmod.entity;

// Alphaver code

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import net.mcreator.minecraftalphaargmod.init.TheArgContainerModEntities;

public class RecruiterV2Entity extends PathfinderMob {
    // Classic-like state
    public int stareTimer = 0;
    public boolean getTheFrickOut = false;

    // Behavior controls
    private boolean initializedSpawnPos = false;
    private int ticksSinceSpawn = 0;
    private static final int GRACE_TICKS = 20; // 1 second grace to prevent instant despawn
    private boolean controllerMode = false;    // if true, requires markSpawnedThisCycle() each tick
    private boolean spawnedThisCycle = false;

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
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 100.0F));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) return;

        ticksSinceSpawn++;

        // One-time spawn placement: away from the nearest player (24–48 blocks), fallback to shared spawn
        if (!initializedSpawnPos) {
            initializedSpawnPos = true;
            if (this.level() instanceof ServerLevel sl) {
                Player p = sl.getNearestPlayer(this, 256.0D);
                if (p != null) {
                    double angle = this.getRandom().nextDouble() * (Math.PI * 2.0);
                    double radius = 24.0 + this.getRandom().nextDouble() * 24.0; // 24..48
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

        // Controller-driven lifecycle (optional, off by default)
        if (controllerMode) {
            if (!this.spawnedThisCycle) {
                gone();
                return;
            }
            this.spawnedThisCycle = false; // reset for next tick
        }

        Player player = this.level().getNearestPlayer(this, 256.0D);
        if (player == null) return;

        // Face the player (classic atan2 with -(dz))
        double yaw = Math.toDegrees(Math.atan2(this.getX() - player.getX(), -(this.getZ() - player.getZ())));
        float yawF = (float) yaw;
        this.setYRot(yawF);
        this.setYHeadRot(yawF);
        this.setYBodyRot(yawF);

        // Proximity despawn: only after grace ticks
        float dist = this.distanceTo(player);
        if (ticksSinceSpawn > GRACE_TICKS && dist < 19.0F) {
            gone();
            return;
        }

        // Stare logic: within ~10° of face-to-face and line-of-sight for 70 ticks
        boolean hasLoS = this.hasLineOfSight(player);
        float diff = Mth.wrapDegrees(player.getYRot() - this.getYRot());
        // When facing each other, yaw difference ~180; bring it to closeness-to-180
        float closenessTo180 = Math.abs(Math.abs(diff) - 180.0F);
        if (closenessTo180 < 10.0F && hasLoS) {
            this.stareTimer++;
            if (this.stareTimer > 70) {
                this.getTheFrickOut = true;
                gone();
            }
        } else {
            // Break the stare if the condition isn’t met
            if (this.stareTimer > 0) this.stareTimer = Math.max(0, this.stareTimer - 1);
        }
    }

    // Enable if you want an external controller to keep it alive each tick
    public void enableControllerMode(boolean enabled) {
        this.controllerMode = enabled;
    }
    // Call per tick when controllerMode is true
    public void markSpawnedThisCycle() {
        this.spawnedThisCycle = true;
    }

    private void gone() {
        this.discard();
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
        builder = builder.add(Attributes.FOLLOW_RANGE, 32); // give it reasonable player detection range
        return builder;
    }
}
