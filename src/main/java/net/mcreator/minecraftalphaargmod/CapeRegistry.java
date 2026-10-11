package net.mcreator.minecraftalphaargmod.capes;

import net.mcreator.minecraftalphaargmod.ContributorRegistry;
import net.mcreator.minecraftalphaargmod.DevRegistry;
import net.mcreator.minecraftalphaargmod.ReviewerRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CapeRegistry {

	public static final UUID LEAD_DEV_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
	private static final String LEAD_DEV_NAME = "Dev";

	private static final List<CapeDefinition> CAPES = new ArrayList<>();
	private static final Map<String, CapeDefinition> BY_ID = new HashMap<>();

	static {

		CAPES.add(new CapeDefinition("dev_default", new ResourceLocation("the_arg_container", "textures/capes/arg_container_cape_dev_default.png"), "cape.the_arg_container.dev_default", CapeCategory.DEV_DEFAULT, null));
		CAPES.add(new CapeDefinition("contributor_default", new ResourceLocation("the_arg_container", "textures/capes/arg_container_cape_contributor_default.png"), "cape.the_arg_container.contributor_default", CapeCategory.CONTRIBUTOR_DEFAULT, null));
		CAPES.add(new CapeDefinition("reviewer_default", new ResourceLocation("the_arg_container", "textures/capes/arg_container_cape_reviewer_default.png"), "cape.the_arg_container.reviewer_default", CapeCategory.REVIEWER_DEFAULT, null));
		CAPES.add(new CapeDefinition("enderplay_cape", new ResourceLocation("the_arg_container", "textures/capes/anderblay_cape.png"), "cape.the_arg_container.enderplay", CapeCategory.PERSONAL, UUID.fromString("d80b49d1-213c-4f5a-8086-dc3e968725a2")));
		CAPES.add(new CapeDefinition("glitchy_cape", new ResourceLocation("the_arg_container", "textures/capes/glitchys_cape.png"), "cape.the_arg_container.glitchy", CapeCategory.PERSONAL, UUID.fromString("2d10b449-51f2-4d96-b2e3-c3bdc0332a81")));
		CAPES.add(new CapeDefinition("mark101_cape", new ResourceLocation("the_arg_container", "textures/capes/wonderful_cape.png"), "cape.the_arg_container.mark101", CapeCategory.PERSONAL, UUID.fromString("b7c088fc-4232-4ead-a6fc-0fd06ecd0476")));
		CAPES.add(new CapeDefinition("divided_cape", new ResourceLocation("the_arg_container", "textures/capes/div_cape.png"), "cape.the_arg_container.divided", CapeCategory.PERSONAL, UUID.fromString("e770e1d4-8b72-43cd-a354-d9a6658d7804")));
		// CAPES.add(new CapeDefinition("personal_example", new ResourceLocation("the_arg_container", "textures/capes/personal_example.png"), "Display Name", CapeCategory.PERSONAL, UUID.fromString("00000000-0000-0000-0000-000000000000")));
		for (CapeDefinition cape : CAPES) {
			BY_ID.put(cape.getId(), cape);
		}
	}

	public static List<CapeDefinition> getAll() {
		// return CAPES;
		return Collections.unmodifiableList(CAPES);
	}

	public static CapeDefinition getById(String id) {
		// for (CapeDefinition cape : CAPES) {
		//     if (cape.getId().equals(id)) return cape;
		// }
		// return null;
		return BY_ID.get(id);
	}

	public static boolean isLeadDev(ServerPlayer player) {
		if (player.getUUID().equals(LEAD_DEV_UUID))
			return true;
		if (!FMLEnvironment.production && LEAD_DEV_NAME.equals(player.getName().getString()))
			return true;
		return false;
	}

	public static List<CapeDefinition> getSelectableCapes(ServerPlayer player) {
		if (isLeadDev(player)) {
			return new ArrayList<>(CAPES);
		}

		UUID uuid = player.getUUID();

		boolean dev = DevRegistry.isDev(player);
		boolean contributor = ContributorRegistry.isContributor(player);
		boolean reviewer = ReviewerRegistry.isReviewer(player);

		List<CapeDefinition> result = new ArrayList<>();
		for (CapeDefinition cape : CAPES) {
			switch (cape.getCategory()) {
				case DEV_DEFAULT :
					if (dev)
						result.add(cape);
					break;
				case CONTRIBUTOR_DEFAULT :
					if (contributor)
						result.add(cape);
					break;
				case REVIEWER_DEFAULT :
					if (reviewer)
						result.add(cape);
					break;
				case PERSONAL :
					if (uuid.equals(cape.getOwnerUuid()))
						result.add(cape);
					break;
			}
		}
		return result;
	}

	public static boolean isSelectable(ServerPlayer player, String capeId) {
		// for (CapeDefinition cape : getSelectableCapes(player)) {
		//     if (cape.getId().equals(capeId)) return true;
		// }
		// return false;
		if (capeId == null)
			return true;
		if (isLeadDev(player))
			return true;
		UUID uuid = player.getUUID();
		boolean dev = DevRegistry.isDev(player);
		boolean contributor = ContributorRegistry.isContributor(player);
		boolean reviewer = ReviewerRegistry.isReviewer(player);
		for (CapeDefinition cape : CAPES) {
			if (!cape.getId().equals(capeId))
				continue;
			return switch (cape.getCategory()) {
				case DEV_DEFAULT -> dev;
				case CONTRIBUTOR_DEFAULT -> contributor;
				case REVIEWER_DEFAULT -> reviewer;
				case PERSONAL -> uuid.equals(cape.getOwnerUuid());
			};
		}
		return false;
	}

	public static boolean hasAnyRole(ServerPlayer player) {
		return DevRegistry.isDev(player) || ContributorRegistry.isContributor(player) || ReviewerRegistry.isReviewer(player);
	}

	public static boolean canAccessCapes(ServerPlayer player) {
		return hasAnyRole(player) || isLeadDev(player);
	}
}