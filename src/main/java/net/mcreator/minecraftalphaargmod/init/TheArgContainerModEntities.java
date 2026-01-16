
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.minecraftalphaargmod.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;

import net.mcreator.minecraftalphaargmod.entity.VoidEntityGeckolibEntity;
import net.mcreator.minecraftalphaargmod.entity.User0CloneEntity;
import net.mcreator.minecraftalphaargmod.entity.TheDirectorEntity;
import net.mcreator.minecraftalphaargmod.entity.StevenEntity;
import net.mcreator.minecraftalphaargmod.entity.SpearProjectileEntity;
import net.mcreator.minecraftalphaargmod.entity.SoulEntityEntity;
import net.mcreator.minecraftalphaargmod.entity.RecruiterV2Entity;
import net.mcreator.minecraftalphaargmod.entity.RecruiterEntity;
import net.mcreator.minecraftalphaargmod.entity.RangerBulletProjectileEntity;
import net.mcreator.minecraftalphaargmod.entity.PigEntity;
import net.mcreator.minecraftalphaargmod.entity.PartiBulletEntity;
import net.mcreator.minecraftalphaargmod.entity.ObserverMobEntity;
import net.mcreator.minecraftalphaargmod.entity.LongLegsEntity;
import net.mcreator.minecraftalphaargmod.entity.GiantEntity;
import net.mcreator.minecraftalphaargmod.entity.ExplosiveEssenceEntity;
import net.mcreator.minecraftalphaargmod.entity.EvilUser0Entity;
import net.mcreator.minecraftalphaargmod.entity.EssenceProjectileEntity;
import net.mcreator.minecraftalphaargmod.entity.EntityEntity;
import net.mcreator.minecraftalphaargmod.entity.DarknessEntityEntity;
import net.mcreator.minecraftalphaargmod.entity.DBGEntity;
import net.mcreator.minecraftalphaargmod.entity.COREEntity;
import net.mcreator.minecraftalphaargmod.entity.BrixgoaEntity;
import net.mcreator.minecraftalphaargmod.entity.BlueGiantEntity;
import net.mcreator.minecraftalphaargmod.TheArgContainerMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class TheArgContainerModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TheArgContainerMod.MODID);
	public static final RegistryObject<EntityType<ObserverMobEntity>> OBSERVER_MOB = register("observer_mob",
			EntityType.Builder.<ObserverMobEntity>of(ObserverMobEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(ObserverMobEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<RecruiterEntity>> RECRUITER = register("recruiter",
			EntityType.Builder.<RecruiterEntity>of(RecruiterEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(RecruiterEntity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<GiantEntity>> GIANT = register("giant",
			EntityType.Builder.<GiantEntity>of(GiantEntity::new, MobCategory.AMBIENT).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(GiantEntity::new)

					.sized(3f, 6f));
	public static final RegistryObject<EntityType<SoulEntityEntity>> SOUL_ENTITY = register("soul_entity",
			EntityType.Builder.<SoulEntityEntity>of(SoulEntityEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(SoulEntityEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<EntityEntity>> ENTITY = register("entity",
			EntityType.Builder.<EntityEntity>of(EntityEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(EntityEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<SpearProjectileEntity>> SPEAR_PROJECTILE = register("spear_projectile", EntityType.Builder.<SpearProjectileEntity>of(SpearProjectileEntity::new, MobCategory.MISC)
			.setCustomClientFactory(SpearProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<EssenceProjectileEntity>> ESSENCE_PROJECTILE = register("essence_projectile", EntityType.Builder.<EssenceProjectileEntity>of(EssenceProjectileEntity::new, MobCategory.MISC)
			.setCustomClientFactory(EssenceProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<ExplosiveEssenceEntity>> EXPLOSIVE_ESSENCE = register("explosive_essence", EntityType.Builder.<ExplosiveEssenceEntity>of(ExplosiveEssenceEntity::new, MobCategory.MISC)
			.setCustomClientFactory(ExplosiveEssenceEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<DBGEntity>> DBG = register("dbg",
			EntityType.Builder.<DBGEntity>of(DBGEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(DBGEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<BlueGiantEntity>> BLUE_GIANT = register("blue_giant",
			EntityType.Builder.<BlueGiantEntity>of(BlueGiantEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(BlueGiantEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<BrixgoaEntity>> BRIXGOA = register("brixgoa",
			EntityType.Builder.<BrixgoaEntity>of(BrixgoaEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(BrixgoaEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<StevenEntity>> STEVEN = register("steven",
			EntityType.Builder.<StevenEntity>of(StevenEntity::new, MobCategory.AMBIENT).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(StevenEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<User0CloneEntity>> USER_0_CLONE = register("user_0_clone",
			EntityType.Builder.<User0CloneEntity>of(User0CloneEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(User0CloneEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<EvilUser0Entity>> EVIL_USER_0 = register("evil_user_0", EntityType.Builder.<EvilUser0Entity>of(EvilUser0Entity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
			.setUpdateInterval(3).setCustomClientFactory(EvilUser0Entity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<RangerBulletProjectileEntity>> RANGER_BULLET_PROJECTILE = register("ranger_bullet_projectile", EntityType.Builder.<RangerBulletProjectileEntity>of(RangerBulletProjectileEntity::new, MobCategory.MISC)
			.setCustomClientFactory(RangerBulletProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<PigEntity>> PIG = register("pig",
			EntityType.Builder.<PigEntity>of(PigEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(PigEntity::new)

					.sized(0.9f, 0.9f));
	public static final RegistryObject<EntityType<PartiBulletEntity>> PARTI_BULLET = register("parti_bullet",
			EntityType.Builder.<PartiBulletEntity>of(PartiBulletEntity::new, MobCategory.MISC).setCustomClientFactory(PartiBulletEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<LongLegsEntity>> LONG_LEGS = register("long_legs",
			EntityType.Builder.<LongLegsEntity>of(LongLegsEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(500).setUpdateInterval(3).setCustomClientFactory(LongLegsEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<VoidEntityGeckolibEntity>> VOID_ENTITY_GECKOLIB = register("void_entity_geckolib",
			EntityType.Builder.<VoidEntityGeckolibEntity>of(VoidEntityGeckolibEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(VoidEntityGeckolibEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<COREEntity>> CORE = register("core",
			EntityType.Builder.<COREEntity>of(COREEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(COREEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<DarknessEntityEntity>> DARKNESS_ENTITY = register("darkness_entity",
			EntityType.Builder.<DarknessEntityEntity>of(DarknessEntityEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(DarknessEntityEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<RecruiterV2Entity>> RECRUITER_V_2 = register("recruiter_v_2", EntityType.Builder.<RecruiterV2Entity>of(RecruiterV2Entity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(RecruiterV2Entity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<TheDirectorEntity>> THE_DIRECTOR = register("the_director",
			EntityType.Builder.<TheDirectorEntity>of(TheDirectorEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(TheDirectorEntity::new)

					.sized(0.6f, 1.8f));

	// Start of user code block custom entities
	// End of user code block custom entities
	private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			ObserverMobEntity.init();
			RecruiterEntity.init();
			GiantEntity.init();
			SoulEntityEntity.init();
			EntityEntity.init();
			DBGEntity.init();
			BlueGiantEntity.init();
			BrixgoaEntity.init();
			StevenEntity.init();
			User0CloneEntity.init();
			EvilUser0Entity.init();
			PigEntity.init();
			LongLegsEntity.init();
			VoidEntityGeckolibEntity.init();
			COREEntity.init();
			DarknessEntityEntity.init();
			RecruiterV2Entity.init();
			TheDirectorEntity.init();
		});
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(OBSERVER_MOB.get(), ObserverMobEntity.createAttributes().build());
		event.put(RECRUITER.get(), RecruiterEntity.createAttributes().build());
		event.put(GIANT.get(), GiantEntity.createAttributes().build());
		event.put(SOUL_ENTITY.get(), SoulEntityEntity.createAttributes().build());
		event.put(ENTITY.get(), EntityEntity.createAttributes().build());
		event.put(DBG.get(), DBGEntity.createAttributes().build());
		event.put(BLUE_GIANT.get(), BlueGiantEntity.createAttributes().build());
		event.put(BRIXGOA.get(), BrixgoaEntity.createAttributes().build());
		event.put(STEVEN.get(), StevenEntity.createAttributes().build());
		event.put(USER_0_CLONE.get(), User0CloneEntity.createAttributes().build());
		event.put(EVIL_USER_0.get(), EvilUser0Entity.createAttributes().build());
		event.put(PIG.get(), PigEntity.createAttributes().build());
		event.put(LONG_LEGS.get(), LongLegsEntity.createAttributes().build());
		event.put(VOID_ENTITY_GECKOLIB.get(), VoidEntityGeckolibEntity.createAttributes().build());
		event.put(CORE.get(), COREEntity.createAttributes().build());
		event.put(DARKNESS_ENTITY.get(), DarknessEntityEntity.createAttributes().build());
		event.put(RECRUITER_V_2.get(), RecruiterV2Entity.createAttributes().build());
		event.put(THE_DIRECTOR.get(), TheDirectorEntity.createAttributes().build());
	}
}
