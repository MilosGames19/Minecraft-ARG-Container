package net.mcreator.minecraftalphaargmod.client;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.*;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ModConfigScreen extends Screen {
	private static final int LEFT_PANEL_W = 115;
	private static final int TOP_BAR_H = 28;
	private static final int BOTTOM_BAR_H = 30;
	private static final int SEARCH_H = 20;
	private static final int ENTRY_H = 42;
	private static final int BG_PANEL = 0xCC000000;
	private static final int BORDER_COLOR = 0x44FFFFFF;
	private static final int TEXT_PRIMARY = 0xFFFFFF;
	private static final int TEXT_SECONDARY = 0xA0A0A0;
	private static final int ACCENT_RED = 0xF44336;
	private static final int ACCENT_ORANGE = 0xFFAA00;
	private static final int ACCENT_BLUE = 0x2196F3;

	private static final float COMMENT_SCROLL_SPEED = 30.0f;
	private static final long COMMENT_SCROLL_PAUSE_MS = 1500;

	private boolean showRestartPopup = false;
	private Button restartOkBtn;

	private final Screen parent;
	private final ForgeConfigSpec spec;
	private final String modId;

	private ConfigList list;
	private EditBox searchBox;
	private Button resetBtn, applyBtn, cancelBtn;
	private String selectedCategory = "ALL";
	private String searchQuery = "";

	private final Map<String, List<ConfigEntry>> categorizedEntries = new LinkedHashMap<>();
	private final List<ConfigEntry> allEntries = new ArrayList<>();
	private final Map<String, Button> categoryButtons = new LinkedHashMap<>();

	public ModConfigScreen(Screen parent, ForgeConfigSpec spec, String modId) {
		super(Component.literal("Config - Settings"));
		this.parent = parent;
		this.spec = spec;
		this.modId = modId;
	}

	@Override
	protected void init() {
		if (categorizedEntries.isEmpty()) {
			parseConfigSpec();
		}

		categoryButtons.clear();

		int searchWidth = this.width - LEFT_PANEL_W - 110;
		this.searchBox = new EditBox(this.font, LEFT_PANEL_W + 6, TOP_BAR_H + 4, searchWidth, SEARCH_H, Component.literal("Search settings..."));
		this.searchBox.setMaxLength(50);
		this.searchBox.setResponder(text -> {
			this.searchQuery = text.toLowerCase();
			rebuildList();
		});
		this.addRenderableWidget(this.searchBox);

		this.resetBtn = Button.builder(Component.literal("Reset All"), btn -> {
			allEntries.forEach(e -> {
				e.resetToDefault();
				e.refreshWidget();
			});
			updateApplyState();
		}).pos(this.width - 102, TOP_BAR_H + 4).size(96, SEARCH_H).build();
		this.addRenderableWidget(this.resetBtn);

		int catY = TOP_BAR_H + SEARCH_H + 8;
		addCategoryButton("ALL", "All Settings", catY);
		catY += 24;
		for (String cat : categorizedEntries.keySet()) {
			String display = formatName(cat);
			addCategoryButton(cat, display, catY);
			catY += 24;
		}

		int listLeft = LEFT_PANEL_W + 2;
		int listWidth = this.width - LEFT_PANEL_W - 4;
		int listTop = TOP_BAR_H + SEARCH_H + 8;
		int listBottom = this.height - BOTTOM_BAR_H - 4;

		this.list = new ConfigList(this.minecraft, listWidth, this.height, listTop, listBottom, ENTRY_H);
		this.list.setLeftPos(listLeft);

		rebuildList();
		this.addRenderableWidget(this.list);
		int cx = this.width / 2;
		int btnY = this.height - 24;
		this.applyBtn = Button.builder(Component.literal("Apply Changes"), btn -> saveAndClose()).pos(cx - 102, btnY).size(100, 20).build();
		this.cancelBtn = Button.builder(Component.literal("Cancel"), btn -> tryClose()).pos(cx + 2, btnY).size(100, 20).build();
		this.addRenderableWidget(this.applyBtn);
		this.addRenderableWidget(this.cancelBtn);

		this.restartOkBtn = Button.builder(Component.literal("OK"), btn -> {
			this.showRestartPopup = false;
			this.minecraft.setScreen(parent);
		}).pos(this.width / 2 - 40, this.height / 2 + 20).size(80, 20).build();
		this.restartOkBtn.visible = false;
		this.addRenderableWidget(this.restartOkBtn);

		updateApplyState();
	}

	private void addCategoryButton(String key, String label, int y) {
		Button btn = Button.builder(Component.literal(label), b -> {
			this.selectedCategory = key;
			rebuildList();
		}).pos(4, y).size(LEFT_PANEL_W - 8, 20).build();
		this.addRenderableWidget(btn);
		categoryButtons.put(key, btn);
	}

	private void parseConfigSpec() {
		walkConfigValues("", spec.getValues(), "General");
	}

	private void walkConfigValues(String parentPath, UnmodifiableConfig config, String category) {
		config.valueMap().forEach((key, obj) -> {
			List<String> path = new ArrayList<>();
			if (!parentPath.isEmpty()) {
				path.addAll(Arrays.asList(parentPath.split("\\.")));
			}
			path.add(key);

			if (obj instanceof UnmodifiableConfig nested) {
				String newCategory = formatName(key);
				walkConfigValues(String.join(".", path), nested, newCategory);
			} else if (obj instanceof ForgeConfigSpec.ConfigValue<?> configValue) {
				ForgeConfigSpec.ValueSpec valueSpec = spec.getRaw(configValue.getPath());
				if (valueSpec == null)
					return;

				String configKey = key;
				Object value = configValue.get();
				ConfigEntry entry = null;

				if (value instanceof Boolean b) {
					entry = new BooleanEntry(configKey, category, valueSpec, configValue, b);
				} else if (value instanceof Integer i) {
					entry = new IntegerEntry(configKey, category, valueSpec, configValue, i);
				} else if (value instanceof Long l) {
					entry = new LongEntry(configKey, category, valueSpec, configValue, l);
				} else if (value instanceof Double d) {
					entry = new DoubleEntry(configKey, category, valueSpec, configValue, d);
				} else if (value instanceof String s) {
					entry = new StringEntry(configKey, category, valueSpec, configValue, s);
				} else if (value instanceof Enum<?> e) {
					entry = new EnumEntry(configKey, category, valueSpec, configValue, e);
				} else if (value instanceof List<?> l && !l.isEmpty() && l.get(0) instanceof String) {
					entry = new StringListEntry(configKey, category, valueSpec, configValue, l);
				}

				if (entry != null) {
					categorizedEntries.computeIfAbsent(category, k -> new ArrayList<>()).add(entry);
					allEntries.add(entry);
				}
			}
		});
	}

	private void rebuildList() {
		this.list.clearListEntries();
		for (Map.Entry<String, List<ConfigEntry>> catEntry : categorizedEntries.entrySet()) {
			if (!selectedCategory.equals("ALL") && !selectedCategory.equals(catEntry.getKey()))
				continue;
			List<ConfigEntry> filtered = catEntry.getValue().stream().filter(e -> e.matchesSearch(searchQuery)).toList();
			if (!filtered.isEmpty()) {
				this.list.addEntryPublic(new HeaderEntry(catEntry.getKey()));
				filtered.forEach(this.list::addEntryPublic);
			}
		}
		this.list.setScrollAmount(0);
	}

	public void updateApplyState() {
		this.applyBtn.active = allEntries.stream().anyMatch(ConfigEntry::isChanged);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		renderBackground(graphics);

		graphics.fill(0, 0, LEFT_PANEL_W, this.height, 0xDD111111);
		graphics.fill(LEFT_PANEL_W - 1, 0, LEFT_PANEL_W, this.height, BORDER_COLOR);

		Button selectedBtn = categoryButtons.get(selectedCategory);
		if (selectedBtn != null) {
			graphics.fill(selectedBtn.getX() - 2, selectedBtn.getY() - 1, selectedBtn.getX() + selectedBtn.getWidth() + 2, selectedBtn.getY() + selectedBtn.getHeight() + 1, 0x44FFFFFF);
		}

		graphics.fill(0, 0, this.width, TOP_BAR_H, BG_PANEL);
		graphics.fill(0, TOP_BAR_H - 1, this.width, TOP_BAR_H, BORDER_COLOR);
		graphics.drawCenteredString(this.font, this.title, this.width / 2, 8, TEXT_PRIMARY);

		graphics.fill(0, this.height - BOTTOM_BAR_H, this.width, this.height, BG_PANEL);
		graphics.fill(0, this.height - BOTTOM_BAR_H, this.width, this.height - BOTTOM_BAR_H + 1, BORDER_COLOR);

		super.render(graphics, mouseX, mouseY, partialTick);

		if (showRestartPopup) {
			this.restartOkBtn.visible = true;

			graphics.pose().pushPose();
			graphics.pose().translate(0, 0, 400);

			graphics.fill(0, 0, this.width, this.height, 0xAA000000);

			int pw = 220, ph = 80;
			int px = this.width / 2 - pw / 2;
			int py = this.height / 2 - ph / 2;
			graphics.fill(px, py, px + pw, py + ph, 0xFF1A1A2E);
			graphics.fill(px, py, px + pw, py + 1, 0xFFFFAA00);
			graphics.fill(px, py + ph - 1, px + pw, py + ph, 0xFFFFAA00);
			graphics.fill(px, py, px + 1, py + ph, 0xFFFFAA00);
			graphics.fill(px + pw - 1, py, px + pw, py + ph, 0xFFFFAA00);

			graphics.drawCenteredString(this.font, "Restart Required", this.width / 2, py + 12, 0xFFAA00);
			graphics.drawCenteredString(this.font, "Some changes require a game restart.", this.width / 2, py + 28, 0xFFFFFF);
			graphics.drawCenteredString(this.font, "to take effect.", this.width / 2, py + 40, 0xFFFFFF);

			this.restartOkBtn.setX(this.width / 2 - 40);
			this.restartOkBtn.setY(py + ph - 26);
			this.restartOkBtn.render(graphics, mouseX, mouseY, partialTick);

			graphics.pose().popPose();
		} else {
			if (this.restartOkBtn != null)
				this.restartOkBtn.visible = false;
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (showRestartPopup) {
			if (this.restartOkBtn.mouseClicked(mouseX, mouseY, button)) {
				return true;
			}
			return true;
		}

		boolean searchClicked = this.searchBox.mouseClicked(mouseX, mouseY, button);
		this.searchBox.setFocused(searchClicked);
		if (searchClicked) {
			this.setFocused(this.searchBox);
			return true;
		}
		if (this.list.mouseClicked(mouseX, mouseY, button)) {
			this.setFocused(this.list);
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (showRestartPopup)
			return true;
		if (this.searchBox.keyPressed(keyCode, scanCode, modifiers))
			return true;
		if (this.list.keyPressed(keyCode, scanCode, modifiers))
			return true;
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		if (showRestartPopup)
			return true;
		if (this.searchBox.charTyped(codePoint, modifiers))
			return true;
		if (this.list.charTyped(codePoint, modifiers))
			return true;
		return super.charTyped(codePoint, modifiers);
	}

	private void tryClose() {
		if (allEntries.stream().anyMatch(ConfigEntry::isChanged)) {
			this.minecraft.setScreen(new ConfirmScreen(confirmed -> {
				if (confirmed)
					this.minecraft.setScreen(parent);
				else
					this.minecraft.setScreen(this);
			}, Component.literal("Unsaved Changes"), Component.literal("You have unsaved changes. Exit anyway?")));
		} else {
			this.minecraft.setScreen(parent);
		}
	}

	private void saveAndClose() {
		boolean needsRestart = false;
		for (ConfigEntry e : allEntries) {
			if (e.isChanged()) {
				if (ConfigRestartPolicy.requiresRestart(e.key)) {
					needsRestart = true;
				}
				if (e.valueSpec != null && e.valueSpec.needsWorldRestart()) {
					needsRestart = true;
				}
				e.commit();
			}
		}
		spec.save();
		ConfigTracker.INSTANCE.configSets().values().stream().flatMap(Set::stream).filter(mc -> mc.getSpec() == spec).findFirst().ifPresent(mc -> MinecraftForge.EVENT_BUS.post(new ModConfigEvent.Reloading(mc)));
		if (needsRestart) {
			this.showRestartPopup = true;
		} else {
			this.minecraft.setScreen(parent);
		}
	}

	@Override
	public void onClose() {
		tryClose();
	}

	private static String formatName(String key) {
		return Arrays.stream(key.split("_")).filter(s -> !s.isEmpty()).map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1)).collect(Collectors.joining(" "));
	}

	private static int getCommentScrollOffset(int overflow) {
		if (overflow <= 0) return 0;

		long scrollDurationMs = (long) (overflow / COMMENT_SCROLL_SPEED * 1000);
		long cycleDurationMs = COMMENT_SCROLL_PAUSE_MS + scrollDurationMs + COMMENT_SCROLL_PAUSE_MS + scrollDurationMs;

		long t = System.currentTimeMillis() % cycleDurationMs;

		if (t < COMMENT_SCROLL_PAUSE_MS) {
			return 0;
		}
		t -= COMMENT_SCROLL_PAUSE_MS;

		if (t < scrollDurationMs) {
			return (int) (t * COMMENT_SCROLL_SPEED / 1000.0f);
		}
		t -= scrollDurationMs;

		if (t < COMMENT_SCROLL_PAUSE_MS) {
			return overflow;
		}
		t -= COMMENT_SCROLL_PAUSE_MS;

		return overflow - (int) (t * COMMENT_SCROLL_SPEED / 1000.0f);
	}

	abstract static class ConfigEntry extends ContainerObjectSelectionList.Entry<ConfigEntry> {
		protected final String key;
		protected final String category;
		protected final ForgeConfigSpec.ValueSpec valueSpec;
		protected final ForgeConfigSpec.ConfigValue<?> configValue;
		protected final Object originalValue;
		protected Object currentValue;
		protected GuiEventListener widget;
		protected Button resetBtn;
		protected int entryX, entryY, entryW, entryH;
		protected boolean invalid = false;

		ConfigEntry(String key, String category, ForgeConfigSpec.ValueSpec valueSpec, ForgeConfigSpec.ConfigValue<?> configValue, Object current) {
			this.key = key;
			this.category = category;
			this.valueSpec = valueSpec;
			this.configValue = configValue;
			this.originalValue = current;
			this.currentValue = current;
			this.resetBtn = Button.builder(Component.literal("R"), btn -> resetToDefault()).size(20, 20).build();
		}

		public void resetToDefault() {
			if (valueSpec != null) {
				this.currentValue = valueSpec.getDefault();
			}
			refreshWidget();
			if (Minecraft.getInstance().screen instanceof ModConfigScreen sc) {
				sc.updateApplyState();
			}
		}

		public abstract void refreshWidget();

		public abstract void commit();

		public boolean isChanged() {
			return !Objects.equals(currentValue, originalValue);
		}

		public boolean matchesSearch(String query) {
			if (query.isEmpty())
				return true;
			String name = formatName(key).toLowerCase();
			String comment = getComment().toLowerCase();
			return name.contains(query) || comment.contains(query);
		}

		public String getComment() {
			String comment = valueSpec.getComment();
			return comment != null && !comment.isEmpty() ? comment : "No description.";
		}

		protected void renderBase(GuiGraphics g, int top, int left, int w, int h, String label, int controlWidth) {
			boolean changed = isChanged();
			int titleColor = changed ? ACCENT_ORANGE : TEXT_PRIMARY;
			String prefix = valueSpec.needsWorldRestart() ? "* " : "";
			String suffix = changed ? " [Modified]" : "";

			if (changed) {
				g.fill(left, top, left + 3, top + h, ACCENT_ORANGE);
			}

			int rightReserved = controlWidth + 37;

			String displayLabel = prefix + label + suffix;
			int maxLabelWidth = w - rightReserved;
			if (Minecraft.getInstance().font.width(displayLabel) > maxLabelWidth) {
				displayLabel = Minecraft.getInstance().font.substrByWidth(Component.literal(displayLabel), maxLabelWidth).getString() + "...";
			}
			g.drawString(Minecraft.getInstance().font, displayLabel, left + 8, top + 4, titleColor);

			int commentX = left + 8;
			int commentY = top + 16;
			int commentClipRight = left + w - rightReserved;
			int commentClipWidth = commentClipRight - commentX;

			String comment = getComment();
			int textWidth = Minecraft.getInstance().font.width(comment);

			if (commentClipWidth > 0) {
				if (textWidth > commentClipWidth) {
					int overflow = textWidth - commentClipWidth;
					int scrollOffset = getCommentScrollOffset(overflow);
					g.enableScissor(commentX, top, commentClipRight, top + h);
					g.drawString(Minecraft.getInstance().font, comment, commentX - scrollOffset, commentY, TEXT_SECONDARY);
					g.disableScissor();
				} else {
					g.drawString(Minecraft.getInstance().font, comment, commentX, commentY, TEXT_SECONDARY);
				}
			}

			g.fill(left + w - 30, top + 6, left + w - 29, top + h - 6, 0x44FFFFFF);
			this.resetBtn.setX(left + w - 26);
			this.resetBtn.setY(top + 8);
			this.resetBtn.active = valueSpec != null && !Objects.equals(currentValue, valueSpec.getDefault());

			this.entryX = left;
			this.entryY = top;
			this.entryW = w;
			this.entryH = h;
		}

		@Override
		public List<? extends GuiEventListener> children() {
			List<GuiEventListener> children = new ArrayList<>();
			if (widget != null)
				children.add(widget);
			children.add(resetBtn);
			return children;
		}

		@Override
		public List<? extends NarratableEntry> narratables() {
			return widget != null ? List.of((NarratableEntry) widget) : List.of();
		}
	}

	static class BooleanEntry extends ConfigEntry {
		private final Button toggleBtn;

		BooleanEntry(String key, String category, ForgeConfigSpec.ValueSpec valueSpec, ForgeConfigSpec.ConfigValue<?> configValue, Boolean current) {
			super(key, category, valueSpec, configValue, current);
			this.widget = this.toggleBtn = Button.builder(Component.literal(current ? "ON" : "OFF"), b -> {
				this.currentValue = !(Boolean) this.currentValue;
				refreshWidget();
				if (Minecraft.getInstance().screen instanceof ModConfigScreen sc)
					sc.updateApplyState();
			}).size(50, 20).build();
		}

		@Override
		public void refreshWidget() {
			this.toggleBtn.setMessage(Component.literal((Boolean) currentValue ? "ON" : "OFF"));
		}

		@Override
		public void commit() {
			@SuppressWarnings("unchecked")
			ForgeConfigSpec.ConfigValue<Boolean> cv = (ForgeConfigSpec.ConfigValue<Boolean>) configValue;
			cv.set((Boolean) currentValue);
		}

		@Override
		public void render(GuiGraphics g, int idx, int top, int left, int w, int h, int mx, int my, boolean hover, float pt) {
			if (hover)
				g.fill(left, top, left + w, top + h, 0x22FFFFFF);
			renderBase(g, top, left, w, h, formatName(key), 50);
			this.toggleBtn.setX(left + w - 82);
			this.toggleBtn.setY(top + 8);
			this.toggleBtn.render(g, mx, my, pt);
			this.resetBtn.render(g, mx, my, pt);
		}
	}

	static class IntegerEntry extends ConfigEntry {
		private final EditBox input;

		IntegerEntry(String key, String category, ForgeConfigSpec.ValueSpec valueSpec, ForgeConfigSpec.ConfigValue<?> configValue, Integer current) {
			super(key, category, valueSpec, configValue, current);
			this.widget = this.input = new EditBox(Minecraft.getInstance().font, 0, 0, 50, 20, Component.literal(""));
			this.input.setValue(current.toString());
			this.input.setResponder(val -> {
				try {
					int parsed = Integer.parseInt(val);
					if (valueSpec.test(parsed)) {
						this.currentValue = parsed;
						this.invalid = false;
					} else {
						this.invalid = true;
					}
					if (Minecraft.getInstance().screen instanceof ModConfigScreen sc)
						sc.updateApplyState();
				} catch (NumberFormatException ignored) {
					this.invalid = true;
				}
			});
		}

		@Override
		public void refreshWidget() {
			this.input.setValue(this.currentValue.toString());
			this.invalid = false;
		}

		@Override
		public void commit() {
			if (!invalid) {
				@SuppressWarnings("unchecked")
				ForgeConfigSpec.ConfigValue<Integer> cv = (ForgeConfigSpec.ConfigValue<Integer>) configValue;
				cv.set((Integer) currentValue);
			}
		}

		@Override
		public void render(GuiGraphics g, int idx, int top, int left, int w, int h, int mx, int my, boolean hover, float pt) {
			if (hover)
				g.fill(left, top, left + w, top + h, 0x22FFFFFF);
			renderBase(g, top, left, w, h, formatName(key), 50);
			if (invalid) {
				g.fill(left + w - 68, top + 8, left + w - 68 + 50, top + 28, 0x88FF0000);
			}
			this.input.setX(left + w - 68);
			this.input.setY(top + 8);
			this.input.render(g, mx, my, pt);
			this.resetBtn.render(g, mx, my, pt);
		}
	}

	static class LongEntry extends ConfigEntry {
		private final EditBox input;

		LongEntry(String key, String category, ForgeConfigSpec.ValueSpec valueSpec, ForgeConfigSpec.ConfigValue<?> configValue, Long current) {
			super(key, category, valueSpec, configValue, current);
			this.widget = this.input = new EditBox(Minecraft.getInstance().font, 0, 0, 50, 20, Component.literal(""));
			this.input.setValue(current.toString());
			this.input.setResponder(val -> {
				try {
					long parsed = Long.parseLong(val);
					if (valueSpec != null && valueSpec.test(parsed)) {
						this.currentValue = parsed;
						this.invalid = false;
					} else if (valueSpec != null) {
						this.invalid = true;
					} else {
						this.currentValue = parsed;
						this.invalid = false;
					}
					if (Minecraft.getInstance().screen instanceof ModConfigScreen sc)
						sc.updateApplyState();
				} catch (NumberFormatException ignored) {
					this.invalid = true;
				}
			});
		}

		@Override
		public void refreshWidget() {
			this.input.setValue(this.currentValue.toString());
			this.invalid = false;
		}

		@Override
		public void commit() {
			if (!invalid) {
				@SuppressWarnings("unchecked")
				ForgeConfigSpec.ConfigValue<Long> cv = (ForgeConfigSpec.ConfigValue<Long>) configValue;
				cv.set((Long) currentValue);
			}
		}

		@Override
		public void render(GuiGraphics g, int idx, int top, int left, int w, int h, int mx, int my, boolean hover, float pt) {
			if (hover)
				g.fill(left, top, left + w, top + h, 0x22FFFFFF);
			renderBase(g, top, left, w, h, formatName(key), 50);
			if (invalid) {
				g.fill(left + w - 68, top + 8, left + w - 68 + 50, top + 28, 0x88FF0000);
			}
			this.input.setX(left + w - 68);
			this.input.setY(top + 8);
			this.input.render(g, mx, my, pt);
			this.resetBtn.render(g, mx, my, pt);
		}
	}

	static class DoubleEntry extends ConfigEntry {
		private final EditBox input;

		DoubleEntry(String key, String category, ForgeConfigSpec.ValueSpec valueSpec, ForgeConfigSpec.ConfigValue<?> configValue, Double current) {
			super(key, category, valueSpec, configValue, current);
			this.widget = this.input = new EditBox(Minecraft.getInstance().font, 0, 0, 50, 20, Component.literal(""));
			this.input.setValue(current.toString());
			this.input.setResponder(val -> {
				try {
					double parsed = Double.parseDouble(val);
					if (valueSpec.test(parsed)) {
						this.currentValue = parsed;
						this.invalid = false;
					} else {
						this.invalid = true;
					}
					if (Minecraft.getInstance().screen instanceof ModConfigScreen sc)
						sc.updateApplyState();
				} catch (NumberFormatException ignored) {
					this.invalid = true;
				}
			});
		}

		@Override
		public void refreshWidget() {
			this.input.setValue(this.currentValue.toString());
			this.invalid = false;
		}

		@Override
		public void commit() {
			if (!invalid) {
				@SuppressWarnings("unchecked")
				ForgeConfigSpec.ConfigValue<Double> cv = (ForgeConfigSpec.ConfigValue<Double>) configValue;
				cv.set((Double) currentValue);
			}
		}

		@Override
		public void render(GuiGraphics g, int idx, int top, int left, int w, int h, int mx, int my, boolean hover, float pt) {
			if (hover)
				g.fill(left, top, left + w, top + h, 0x22FFFFFF);
			renderBase(g, top, left, w, h, formatName(key), 50);
			if (invalid) {
				g.fill(left + w - 68, top + 8, left + w - 68 + 50, top + 28, 0x88FF0000);
			}
			this.input.setX(left + w - 68);
			this.input.setY(top + 8);
			this.input.render(g, mx, my, pt);
			this.resetBtn.render(g, mx, my, pt);
		}
	}

	static class StringEntry extends ConfigEntry {
		private final EditBox input;

		StringEntry(String key, String category, ForgeConfigSpec.ValueSpec valueSpec, ForgeConfigSpec.ConfigValue<?> configValue, String current) {
			super(key, category, valueSpec, configValue, current);
			this.widget = this.input = new EditBox(Minecraft.getInstance().font, 0, 0, 100, 20, Component.literal(""));
			this.input.setValue(current);
			this.input.setResponder(val -> {
				this.currentValue = val;
				if (Minecraft.getInstance().screen instanceof ModConfigScreen sc)
					sc.updateApplyState();
			});
		}

		@Override
		public void refreshWidget() {
			this.input.setValue((String) currentValue);
		}

		@Override
		public void commit() {
			@SuppressWarnings("unchecked")
			ForgeConfigSpec.ConfigValue<String> cv = (ForgeConfigSpec.ConfigValue<String>) configValue;
			cv.set((String) currentValue);
		}

		@Override
		public void render(GuiGraphics g, int idx, int top, int left, int w, int h, int mx, int my, boolean hover, float pt) {
			if (hover)
				g.fill(left, top, left + w, top + h, 0x22FFFFFF);
			renderBase(g, top, left, w, h, formatName(key), 100);
			this.input.setX(left + w - 118);
			this.input.setY(top + 8);
			this.input.render(g, mx, my, pt);
			this.resetBtn.render(g, mx, my, pt);
		}
	}

	static class EnumEntry extends ConfigEntry {
		private final Button cycleBtn;
		private final List<Enum<?>> enumValues;

		EnumEntry(String key, String category, ForgeConfigSpec.ValueSpec valueSpec, ForgeConfigSpec.ConfigValue<?> configValue, Enum<?> current) {
			super(key, category, valueSpec, configValue, current);
			this.enumValues = new ArrayList<>();

			Class<?> enumClass = current.getClass();
			for (Object obj : enumClass.getEnumConstants()) {
				if (obj instanceof Enum<?> e && valueSpec.test(e)) {
					enumValues.add(e);
				}
			}

			this.widget = this.cycleBtn = Button.builder(Component.literal(current.name()), btn -> {
				int idx = enumValues.indexOf(currentValue);
				idx = (idx + 1) % enumValues.size();
				this.currentValue = enumValues.get(idx);
				refreshWidget();
				if (Minecraft.getInstance().screen instanceof ModConfigScreen sc)
					sc.updateApplyState();
			}).size(80, 20).build();
		}

		@Override
		public void refreshWidget() {
			Enum<?> e = (Enum<?>) currentValue;
			this.cycleBtn.setMessage(Component.literal(e.name()));
		}

		@Override
		public void commit() {
			@SuppressWarnings("unchecked")
			ForgeConfigSpec.ConfigValue<Enum<?>> cv = (ForgeConfigSpec.ConfigValue<Enum<?>>) configValue;
			cv.set((Enum<?>) currentValue);
		}

		@Override
		public void render(GuiGraphics g, int idx, int top, int left, int w, int h, int mx, int my, boolean hover, float pt) {
			if (hover)
				g.fill(left, top, left + w, top + h, 0x22FFFFFF);
			renderBase(g, top, left, w, h, formatName(key), 80);
			this.cycleBtn.setX(left + w - 98);
			this.cycleBtn.setY(top + 8);
			this.cycleBtn.render(g, mx, my, pt);
			this.resetBtn.render(g, mx, my, pt);
		}
	}

	static class StringListEntry extends ConfigEntry {
		private final EditBox input;

		StringListEntry(String key, String category, ForgeConfigSpec.ValueSpec valueSpec, ForgeConfigSpec.ConfigValue<?> configValue, List<?> current) {
			super(key, category, valueSpec, configValue, current);
			this.widget = this.input = new EditBox(Minecraft.getInstance().font, 0, 0, 110, 20, Component.literal(""));
			updateInputFromList((List<?>) current);
			this.input.setResponder(val -> {
				this.currentValue = Arrays.asList(val.split(",\\s*"));
				if (Minecraft.getInstance().screen instanceof ModConfigScreen sc)
					sc.updateApplyState();
			});
		}

		private void updateInputFromList(List<?> list) {
			this.input.setValue(list.stream().map(Object::toString).collect(Collectors.joining(", ")));
		}

		@Override
		public void refreshWidget() {
			updateInputFromList((List<?>) currentValue);
		}

		@Override
		public void commit() {
			@SuppressWarnings("unchecked")
			ForgeConfigSpec.ConfigValue<List<?>> cv = (ForgeConfigSpec.ConfigValue<List<?>>) configValue;
			cv.set((List<?>) currentValue);
		}

		@Override
		public void render(GuiGraphics g, int idx, int top, int left, int w, int h, int mx, int my, boolean hover, float pt) {
			if (hover)
				g.fill(left, top, left + w, top + h, 0x22FFFFFF);
			renderBase(g, top, left, w, h, formatName(key), 110);
			this.input.setX(left + w - 128);
			this.input.setY(top + 8);
			this.input.render(g, mx, my, pt);
			this.resetBtn.render(g, mx, my, pt);
		}
	}

	static class HeaderEntry extends ConfigEntry {
		HeaderEntry(String category) {
			super("HEADER", category, null, null, null);
		}

		@Override
		public void refreshWidget() {
		}

		@Override
		public void commit() {
		}

		@Override
		public boolean matchesSearch(String q) {
			return true;
		}

		@Override
		public List<? extends GuiEventListener> children() {
			return List.of();
		}

		@Override
		public List<? extends NarratableEntry> narratables() {
			return List.of();
		}

		@Override
		public void render(GuiGraphics g, int idx, int top, int left, int w, int h, int mx, int my, boolean hover, float pt) {
			boolean isDanger = category.contains("Dangerous");
			int bg = isDanger ? 0x44330000 : 0x33000000;
			int border = isDanger ? ACCENT_RED : ACCENT_BLUE;
			g.fill(left, top, left + w, top + h, bg);
			g.fill(left, top, left + w, top + 1, border);
			g.fill(left, top + h - 1, left + w, top + h, border);
			String indicatorPrefix = isDanger ? "! " : "> ";
			g.drawCenteredString(Minecraft.getInstance().font, Component.literal(indicatorPrefix + formatName(category).toUpperCase()), left + w / 2, top + (h - 8) / 2, isDanger ? ACCENT_RED : TEXT_PRIMARY);
		}
	}

	static class ConfigList extends ContainerObjectSelectionList<ConfigEntry> {
		ConfigList(Minecraft mc, int width, int height, int top, int bottom, int itemHeight) {
			super(mc, width, height, top, bottom, itemHeight);
			this.setRenderBackground(false);
			this.setRenderTopAndBottom(false);
		}

		@Override
		protected int getScrollbarPosition() {
			return this.getRowRight() + 4;
		}

		@Override
		public int getRowWidth() {
			return this.width - 12;
		}

		public void clearListEntries() {
			super.clearEntries();
		}

		public void addEntryPublic(ConfigEntry e) {
			super.addEntry(e);
		}
	}
}