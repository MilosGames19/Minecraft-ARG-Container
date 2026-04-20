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
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.tags.FluidTags;

import net.mcreator.minecraftalphaargmod.init.TheArgContainerModEntities;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecruiterV2Entity extends PathfinderMob {

	public int stareTimer = 0;
	public boolean getTheFrickOut = false;
	private boolean initializedSpawnPos = false;
	private int ticksSinceSpawn = 0;
	private static final int GRACE_TICKS = 20;
	private boolean controllerMode = false;
	private boolean spawnedThisCycle = false;

	private boolean buildingMode = false;
	private boolean autoBuildEnabled = false;
	private int autoBuildTimer = 0;
	private static final int AUTO_BUILD_DELAY_MIN = 1200;
	private static final int AUTO_BUILD_DELAY_MAX = 3600;
	private static final double MAX_REACH = 5.0;
	private static final int PLACE_INTERVAL = 4;
	private static final int STUCK_THRESHOLD = 100;

	private BuildingPhase currentPhase = BuildingPhase.IDLE;
	private BlockPos buildLocation = null;
	private int buildingTimer = 0;
	private int stuckTimer = 0;
	private final List<BlockPos> blocksToPlace = new ArrayList<>();
	private final Set<BlockPos> selfPlacedBlocks = new HashSet<>();
	private int currentBlockIndex = 0;
	private StructureType structureType = StructureType.SMALL_T;

	private enum BuildingPhase {
		IDLE, MOVING_TO_BUILD, BUILDING, COMPLETE
	}

	private enum StructureType {
		SMALL_T, BIG_T
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
		this.autoBuildEnabled = this.random.nextFloat() < 0.25f;
		this.autoBuildTimer = AUTO_BUILD_DELAY_MIN + this.random.nextInt(AUTO_BUILD_DELAY_MAX - AUTO_BUILD_DELAY_MIN);
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
		if (this.level().isClientSide)
			return;

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
					this.moveTo(tx + 0.5, ty + 1.0, tz + 0.5, 0.0F, 0.0F);
				} else {
					var spawn = sl.getSharedSpawnPos();
					int y = sl.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawn.getX(), spawn.getZ());
					this.moveTo(spawn.getX() + 0.5, y + 1.0, spawn.getZ() + 0.5, 0.0F, 0.0F);
				}
			}
		}

		if (autoBuildEnabled && !buildingMode) {
			autoBuildTimer--;
			if (autoBuildTimer <= 0) {
				enableBuildingMode(structureType);
				autoBuildEnabled = false;
			}
		}

		if (buildingMode) {
			if (currentPhase == BuildingPhase.BUILDING) {
				Player player = this.level().getNearestPlayer(this, 32.0D);
				if (player != null && isPlayerStaringAtMe(player)) {
					silentVanish();
					return;
				}
			}
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
		if (player == null)
			return;

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
			if (this.stareTimer > 0)
				this.stareTimer = Math.max(0, this.stareTimer - 1);
		}
	}

	private boolean isPlayerStaringAtMe(Player player) {
		if (!this.hasLineOfSight(player))
			return false;
		double dx = this.getX() - player.getX();
		double dz = this.getZ() - player.getZ();
		double angleToEntity = Math.toDegrees(Math.atan2(dz, dx)) - 90.0;
		float diff = Mth.wrapDegrees((float) angleToEntity - player.getYRot());
		return Math.abs(diff) < 20.0F;
	}

	private void silentVanish() {
		cleanupBuild();
		this.discard();
	}

	private void cleanupBuild() {
		for (BlockPos pos : selfPlacedBlocks) {
			if (this.level().getBlockState(pos).is(Blocks.COBBLESTONE)) {
				this.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
			}
		}
		selfPlacedBlocks.clear();
		this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
		buildingMode = false;
		currentPhase = BuildingPhase.IDLE;
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
		this.selfPlacedBlocks.clear();
		this.currentBlockIndex = 0;
		this.stuckTimer = 0;
		this.autoBuildEnabled = false;
		this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.COBBLESTONE));

		if (this.level() instanceof ServerLevel sl) {
			BlockPos found = findValidBuildLocation(sl);
			if (found == null) {
				this.buildingMode = false;
				this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
				return;
			}
			this.buildLocation = found;
			planStructure();
		}
	}

	private BlockPos findValidBuildLocation(ServerLevel sl) {
		for (int attempt = 0; attempt < 20; attempt++) {
			int bx = Mth.floor(this.getX()) + this.random.nextInt(20) - 10;
			int bz = Mth.floor(this.getZ()) + this.random.nextInt(20) - 10;
			int by = sl.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, bx, bz);

			BlockPos ground = new BlockPos(bx, by - 1, bz);
			BlockState groundState = sl.getBlockState(ground);

			if (!groundState.isAir() && !groundState.liquid() && groundState.isSolid() && !groundState.is(Blocks.ICE) && !groundState.is(Blocks.PACKED_ICE) && !sl.isWaterAt(new BlockPos(bx, by, bz)) && !sl.isWaterAt(new BlockPos(bx, by + 1, bz))
					&& isStructureAreaClear(sl, new BlockPos(bx, by, bz))) {
				return new BlockPos(bx, by, bz);
			}
		}
		return null;
	}

	private boolean isStructureAreaClear(ServerLevel sl, BlockPos origin) {
		int width = structureType == StructureType.SMALL_T ? 3 : 6;
		int height = structureType == StructureType.SMALL_T ? 3 : 6;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				BlockPos check = origin.offset(x, y, 0);
				BlockState state = sl.getBlockState(check);
				if (state.liquid())
					return false;
			}
		}
		return true;
	}

	private void planStructure() {
		if (buildLocation == null)
			return;
		if (structureType == StructureType.SMALL_T) {
			planSmallT();
		} else {
			planBigT();
		}
	}

	private void planSmallT() {
		int x0 = buildLocation.getX();
		int y0 = buildLocation.getY();
		int z0 = buildLocation.getZ();

		blocksToPlace.add(new BlockPos(x0, y0, z0));
		blocksToPlace.add(new BlockPos(x0 + 1, y0, z0));
		blocksToPlace.add(new BlockPos(x0 + 2, y0, z0));

		blocksToPlace.add(new BlockPos(x0 + 1, y0 + 1, z0));

		blocksToPlace.add(new BlockPos(x0 + 1, y0 + 2, z0));
	}

	private void planBigT() {
		int x0 = buildLocation.getX();
		int y0 = buildLocation.getY();
		int z0 = buildLocation.getZ();

		for (int x = 0; x < 6; x++) {
			blocksToPlace.add(new BlockPos(x0 + x, y0, z0));
			blocksToPlace.add(new BlockPos(x0 + x, y0, z0 + 1));
		}

		for (int x = 2; x < 4; x++) {
			blocksToPlace.add(new BlockPos(x0 + x, y0 + 1, z0));
			blocksToPlace.add(new BlockPos(x0 + x, y0 + 1, z0 + 1));
			blocksToPlace.add(new BlockPos(x0 + x, y0 + 2, z0));
			blocksToPlace.add(new BlockPos(x0 + x, y0 + 2, z0 + 1));
			blocksToPlace.add(new BlockPos(x0 + x, y0 + 3, z0));
			blocksToPlace.add(new BlockPos(x0 + x, y0 + 3, z0 + 1));
			blocksToPlace.add(new BlockPos(x0 + x, y0 + 4, z0));
			blocksToPlace.add(new BlockPos(x0 + x, y0 + 4, z0 + 1));
		}
	}

	private BlockPos getApproachPos(BlockPos target) {
		Direction[] dirs = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
		BlockPos best = null;
		double bestDist = Double.MAX_VALUE;
		for (Direction dir : dirs) {
			BlockPos candidate = target.relative(dir, 2);
			if (!isInStructureFootprint(candidate)) {
				double d = this.distanceToSqr(candidate.getX() + 0.5, candidate.getY() + 0.5, candidate.getZ() + 0.5);
				if (d < bestDist) {
					bestDist = d;
					best = candidate;
				}
			}
		}
		return best != null ? best : target.relative(Direction.NORTH, 3);
	}

	private boolean isInStructureFootprint(BlockPos pos) {
		if (buildLocation == null)
			return false;
		int width = structureType == StructureType.SMALL_T ? 3 : 6;
		int depth = structureType == StructureType.SMALL_T ? 1 : 2;
		int height = structureType == StructureType.SMALL_T ? 3 : 5;
		return pos.getX() >= buildLocation.getX() && pos.getX() < buildLocation.getX() + width && pos.getY() >= buildLocation.getY() && pos.getY() < buildLocation.getY() + height && pos.getZ() >= buildLocation.getZ()
				&& pos.getZ() < buildLocation.getZ() + depth;
	}

	private void gone() {
		this.discard();
	}

	private void teleportRandomly() {
		if (!(this.level() instanceof ServerLevel sl))
			return;
		for (int attempt = 0; attempt < 10; attempt++) {
			double angle = this.random.nextDouble() * Math.PI * 2.0;
			double distance = 30.0 + this.random.nextDouble() * 20.0;
			double newX = this.getX() + Math.cos(angle) * distance;
			double newZ = this.getZ() + Math.sin(angle) * distance;
			int bx = Mth.floor(newX);
			int bz = Mth.floor(newZ);

			int solidY = sl.getHeight(Heightmap.Types.WORLD_SURFACE, bx, bz);
			BlockPos surface = new BlockPos(bx, solidY, bz);
			BlockPos groundBp = surface.below();
			BlockState groundState = sl.getBlockState(groundBp);
			BlockState surfaceState = sl.getBlockState(surface);

			if (!groundState.isAir() && !groundState.liquid() && groundState.isSolid() && !groundState.is(Blocks.ICE) && !groundState.is(Blocks.PACKED_ICE) && !surfaceState.liquid() && !sl.isWaterAt(surface) && !sl.isWaterAt(groundBp)
					&& !sl.getFluidState(surface).is(FluidTags.LAVA) && !sl.getFluidState(groundBp).is(FluidTags.LAVA)) {
				this.teleportTo(newX, solidY, newZ);
				return;
			}
		}
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
		if (buildingMode)
			return false;
		if (damagesource.is(DamageTypes.IN_FIRE))
			return false;
		if (damagesource.getDirectEntity() instanceof AbstractArrow)
			return false;
		if (damagesource.getDirectEntity() instanceof Player)
			return false;
		if (damagesource.getDirectEntity() instanceof ThrownPotion)
			return false;
		if (damagesource.getDirectEntity() instanceof AreaEffectCloud)
			return false;
		if (damagesource.is(DamageTypes.FALL))
			return false;
		if (damagesource.is(DamageTypes.CACTUS))
			return false;
		if (damagesource.is(DamageTypes.DROWN))
			return false;
		if (damagesource.is(DamageTypes.LIGHTNING_BOLT))
			return false;
		if (damagesource.is(DamageTypes.EXPLOSION))
			return false;
		if (damagesource.is(DamageTypes.PLAYER_EXPLOSION))
			return false;
		if (damagesource.is(DamageTypes.TRIDENT))
			return false;
		if (damagesource.is(DamageTypes.FALLING_ANVIL))
			return false;
		if (damagesource.is(DamageTypes.DRAGON_BREATH))
			return false;
		if (damagesource.is(DamageTypes.WITHER))
			return false;
		if (damagesource.is(DamageTypes.WITHER_SKULL))
			return false;
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
		return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.MAX_HEALTH, 20).add(Attributes.ARMOR, 0).add(Attributes.ATTACK_DAMAGE, 3).add(Attributes.FOLLOW_RANGE, 32);
	}

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
		public boolean canContinueToUse() {
			return entity.buildingMode;
		}

		@Override
		public void tick() {
			switch (entity.currentPhase) {
				case MOVING_TO_BUILD -> moveToLocation();
				case BUILDING -> buildStructure();
				case COMPLETE -> finishBuilding();
			}
		}

		private void moveToLocation() {
			if (entity.buildLocation == null) {
				entity.currentPhase = BuildingPhase.COMPLETE;
				return;
			}

			BlockPos approach = entity.getApproachPos(entity.buildLocation);
			entity.getNavigation().moveTo(approach.getX() + 0.5, approach.getY(), approach.getZ() + 0.5, 1.0);

			double distSq = entity.distanceToSqr(entity.buildLocation.getX() + 0.5, entity.buildLocation.getY(), entity.buildLocation.getZ() + 0.5);

			if (distSq < 36.0) {
				entity.currentPhase = BuildingPhase.BUILDING;
				entity.currentBlockIndex = 0;
				entity.buildingTimer = 0;
				entity.stuckTimer = 0;
			}
		}

		private void buildStructure() {
			if (entity.currentBlockIndex >= entity.blocksToPlace.size()) {
				entity.currentPhase = BuildingPhase.COMPLETE;
				return;
			}

			BlockPos target = entity.blocksToPlace.get(entity.currentBlockIndex);

			double distSq = entity.distanceToSqr(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5);

			if (distSq > MAX_REACH * MAX_REACH) {
				BlockPos approachPos = entity.getApproachPos(target);
				entity.getNavigation().moveTo(approachPos.getX() + 0.5, approachPos.getY(), approachPos.getZ() + 0.5, 1.0);

				entity.stuckTimer++;
				if (entity.stuckTimer > STUCK_THRESHOLD) {
					entity.currentBlockIndex++;
					entity.stuckTimer = 0;
					entity.buildingTimer = 0;
				}
				return;
			}

			entity.stuckTimer = 0;
			entity.getNavigation().stop();

			entity.getLookControl().setLookAt(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, 30.0F, 30.0F);

			entity.buildingTimer++;

			if (entity.buildingTimer >= PLACE_INTERVAL) {
				BlockState existing = entity.level().getBlockState(target);

				if (!existing.isAir() && !existing.canBeReplaced() && !entity.selfPlacedBlocks.contains(target)) {
					entity.currentBlockIndex++;
					entity.buildingTimer = 0;
					return;
				}

				if (!existing.liquid()) {
					entity.swing(InteractionHand.MAIN_HAND);
					entity.level().setBlock(target, Blocks.COBBLESTONE.defaultBlockState(), 3);
					entity.selfPlacedBlocks.add(target);
					entity.level().playSound(null, target, SoundEvents.STONE_PLACE, entity.getSoundSource(), 0.8F, 0.9F + entity.random.nextFloat() * 0.2F);
				}

				entity.currentBlockIndex++;
				entity.buildingTimer = 0;
			}
		}

		private void finishBuilding() {
			entity.selfPlacedBlocks.clear();
			entity.buildingMode = false;
			entity.currentPhase = BuildingPhase.IDLE;
			entity.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
			entity.teleportRandomly();
		}
	}
}