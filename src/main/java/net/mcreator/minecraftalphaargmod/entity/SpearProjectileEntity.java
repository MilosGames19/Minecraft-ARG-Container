package net.mcreator.minecraftalphaargmod.entity;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.RandomSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;

import net.mcreator.minecraftalphaargmod.init.TheArgContainerModItems;
import net.mcreator.minecraftalphaargmod.init.TheArgContainerModEntities;

@OnlyIn(value = Dist.CLIENT, _interface = ItemSupplier.class)
public class SpearProjectileEntity extends AbstractArrow implements ItemSupplier {
	public static final ItemStack PROJECTILE_ITEM = new ItemStack(TheArgContainerModItems.SPEAR.get());

	private static final float BOUNCE_FACTOR = 0.6f;

	public SpearProjectileEntity(PlayMessages.SpawnEntity packet, Level world) {
		super(TheArgContainerModEntities.SPEAR_PROJECTILE.get(), world);
	}

	public SpearProjectileEntity(EntityType<? extends SpearProjectileEntity> type, Level world) {
		super(type, world);
	}

	public SpearProjectileEntity(EntityType<? extends SpearProjectileEntity> type, double x, double y, double z, Level world) {
		super(type, x, y, z, world);
	}

	public SpearProjectileEntity(EntityType<? extends SpearProjectileEntity> type, LivingEntity entity, Level world) {
		super(type, entity, world);
		this.pickup = AbstractArrow.Pickup.ALLOWED;
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack getItem() {
		return PROJECTILE_ITEM;
	}

	@Override
	protected ItemStack getPickupItem() {
		return PROJECTILE_ITEM.copy();
	}

	@Override
	protected void doPostHurtEffects(LivingEntity entity) {
		super.doPostHurtEffects(entity);
		entity.setArrowCount(entity.getArrowCount() - 1);
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		Entity target = result.getEntity();
		Entity owner  = this.getOwner();

		DamageSource source = this.damageSources().arrow(this, owner != null ? owner : this);
		target.hurt(source, (float) this.getBaseDamage());

		if (target instanceof LivingEntity living) {
			doPostHurtEffects(living);
		}

		this.playSound(SoundEvents.TRIDENT_HIT, 1.0f, 1.0f);
		Vec3 vel = this.getDeltaMovement();
		this.setDeltaMovement(
			vel.x * BOUNCE_FACTOR,
			vel.y * BOUNCE_FACTOR,
			vel.z * BOUNCE_FACTOR
		);
	}

	@Override
	public void tick() {
		super.tick();
	}

	public boolean isInGround() {
		return this.inGround;
	}

	public static SpearProjectileEntity shoot(Level world, LivingEntity entity, RandomSource source) {
		return shoot(world, entity, source, 2f, 4, 4);
	}

	public static SpearProjectileEntity shoot(Level world, LivingEntity entity, RandomSource random, float power, double damage, int knockback) {
		SpearProjectileEntity arrow = new SpearProjectileEntity(TheArgContainerModEntities.SPEAR_PROJECTILE.get(), entity, world);
		arrow.shoot(entity.getViewVector(1).x, entity.getViewVector(1).y, entity.getViewVector(1).z, power * 2, 0);
		arrow.setSilent(true);
		arrow.setCritArrow(false);
		arrow.setBaseDamage(damage);
		arrow.setKnockback(knockback);
		world.addFreshEntity(arrow);
		world.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
				ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.arrow.shoot")),
				SoundSource.PLAYERS, 1, 1f / (random.nextFloat() * 0.5f + 1) + (power / 2));
		return arrow;
	}

	public static SpearProjectileEntity shoot(LivingEntity entity, LivingEntity target) {
		SpearProjectileEntity arrow = new SpearProjectileEntity(TheArgContainerModEntities.SPEAR_PROJECTILE.get(), entity, entity.level());
		double dx = target.getX() - entity.getX();
		double dy = target.getY() + target.getEyeHeight() - 1.1;
		double dz = target.getZ() - entity.getZ();
		arrow.shoot(dx, dy - arrow.getY() + Math.hypot(dx, dz) * 0.2F, dz, 2f * 2, 12.0F);
		arrow.setSilent(true);
		arrow.setBaseDamage(4);
		arrow.setKnockback(4);
		arrow.setCritArrow(false);
		entity.level().addFreshEntity(arrow);
		entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
				ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.arrow.shoot")),
				SoundSource.PLAYERS, 1, 1f / (RandomSource.create().nextFloat() * 0.5f + 1));
		return arrow;
	}
}