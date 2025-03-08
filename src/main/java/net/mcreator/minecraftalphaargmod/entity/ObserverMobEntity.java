package net.mcreator.minecraftalphaargmod.entity;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;

import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.Difficulty;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;

import java.util.Random;
import java.util.UUID;

import net.mcreator.minecraftalphaargmod.procedures.ObserverMobEntityIsHurtProcedure;
import net.mcreator.minecraftalphaargmod.procedures.EssencedropProcedure;
import net.mcreator.minecraftalphaargmod.init.TheArgContainerModEntities;

public class ObserverMobEntity extends Monster {
    private static final EntityDataAccessor<Boolean> COMMENCED_ATTACK = SynchedEntityData.defineId(ObserverMobEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> OBSERVATION_TIMER = SynchedEntityData.defineId(ObserverMobEntity.class, EntityDataSerializers.INT);
    private BlockPos lastKnownPosition = BlockPos.ZERO;

    public ObserverMobEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(TheArgContainerModEntities.OBSERVER_MOB.get(), world);
    }

    public ObserverMobEntity(EntityType<ObserverMobEntity> type, Level world) {
        super(type, world);
        setMaxUpStep(0.6f);
        xpReward = 0;
        setNoAi(false);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(COMMENCED_ATTACK, true);
        this.entityData.define(OBSERVATION_TIMER, 0);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
            }
            
            @Override
            public boolean canUse() {
                return entityData.get(OBSERVATION_TIMER) > 550 && super.canUse();
            }
        });
        
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new FloatGoal(this));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Player.class, false, false) {
            @Override
            public boolean canUse() {
                return entityData.get(OBSERVATION_TIMER) <= 550 && super.canUse();
            }
        });
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            LivingEntity target = this.getTarget();
            int timer = this.entityData.get(OBSERVATION_TIMER);

            if (target != null) {
                if (timer > 550) {
                    this.getNavigation().moveTo(target, 1.0);
                } else {
                    timer++;
                    this.entityData.set(OBSERVATION_TIMER, timer);

                    if (this.distanceTo(target) < 4.0F) {
                        timer += 55;
                        this.entityData.set(OBSERVATION_TIMER, timer);
                    }

                    if (timer > 550) {
                        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                                ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("the_arg_container:obvr_attack")),
                                SoundSource.HOSTILE, 1.0F, 1.0F);
                    }

                    if (timer % 55 == 0) {
                        BlockPos newPos = calculateNewPosition(target.blockPosition());
                        this.getNavigation().moveTo(newPos.getX(), newPos.getY(), newPos.getZ(), 1.0);
                    }
                }
                
                double yRot = Math.atan2(this.getX() - target.getX(), -(this.getZ() - target.getZ())) 
                             * (180.0 / Math.PI);
                this.setYRot((float) yRot);
            } else {
                this.entityData.set(OBSERVATION_TIMER, 0);
                this.entityData.set(COMMENCED_ATTACK, true);
            }
        }
    }

    private BlockPos calculateNewPosition(BlockPos targetPos) {
        Random rand = new Random();
        int x = targetPos.getX() + (rand.nextInt(3) - 1) * 8;
        int z = targetPos.getZ() + (rand.nextInt(3) - 1) * 8;
        int y = findValidY(x, z, this.level());
        return new BlockPos(x, y, z);
    }

    private int findValidY(int x, int z, Level level) {
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        while (y > 0 && !level.getBlockState(new BlockPos(x, y, z)).isSolid()) {
            y--;
        }
        return y + 1;
    }

    @Override
    public boolean hurt(DamageSource damagesource, float amount) {
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity entity = this;
        Entity sourceentity = damagesource.getEntity();

        if (sourceentity != null && sourceentity != this.getTarget() && sourceentity instanceof LivingEntity) {
            this.setTarget((LivingEntity) sourceentity);
            this.entityData.set(OBSERVATION_TIMER, 0);
            this.entityData.set(COMMENCED_ATTACK, true);
        }

        ObserverMobEntityIsHurtProcedure.execute(x, y, z, entity);
        return super.hurt(damagesource, amount);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("ObservationTimer")) {
            this.entityData.set(OBSERVATION_TIMER, compound.getInt("ObservationTimer"));
        }
        if (compound.contains("CommencedAttack")) {
            this.entityData.set(COMMENCED_ATTACK, compound.getBoolean("CommencedAttack"));
        }
        if (compound.contains("llocx")) {
            this.lastKnownPosition = new BlockPos(
                compound.getInt("llocx"),
                compound.getInt("llocy"),
                compound.getInt("llocz")
            );
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("ObservationTimer", this.entityData.get(OBSERVATION_TIMER));
        compound.putBoolean("CommencedAttack", this.entityData.get(COMMENCED_ATTACK));
        compound.putInt("llocx", this.lastKnownPosition.getX());
        compound.putInt("llocy", this.lastKnownPosition.getY());
        compound.putInt("llocz", this.lastKnownPosition.getZ());
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEFINED;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.skeleton.hurt"));
    }

    @Override
    public SoundEvent getDeathSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.skeleton.death"));
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        EssencedropProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ());
    }

    public static void init() {
        SpawnPlacements.register(TheArgContainerModEntities.OBSERVER_MOB.get(), 
            SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            (entityType, world, reason, pos, random) -> 
                world.getDifficulty() != Difficulty.PEACEFUL 
                && Monster.isDarkEnoughToSpawn(world, pos, random) 
                && Mob.checkMobSpawnRules(entityType, world, reason, pos, random));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder = builder.add(Attributes.MAX_HEALTH, 10);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 2);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        return builder;
    }
}