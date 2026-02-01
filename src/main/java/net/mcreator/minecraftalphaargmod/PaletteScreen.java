package net.mcreator.minecraftalphaargmod;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PaletteScreen extends Screen {
    private static final String MOD_ID = "the_arg_container";
    private static final ResourceLocation SLOT_TEXTURE = new ResourceLocation(MOD_ID, "textures/palette_slot.png");
    
    // Scale 4.0 as requested
    private static final double FORCED_SCALE = 4.0;

    // Static cache to avoid rebuilding lists every time the screen opens
    private static final List<Block> CACHED_BLOCKS = new ArrayList<>();
    private static final List<Item> CACHED_ITEMS = new ArrayList<>();
    private static boolean isCacheLoaded = false;

    private boolean showingBlocks = true;
    private int currentPage = 0;
    private int columns;
    private int rows;
    
    // Layout variables calculated in init()
    private int contentLeft;
    private int contentTop;
    private int gridWidth;
    
    private float scaleFactor = 1.0f;

    private List<Block> filteredBlocks = new ArrayList<>();
    private List<Item> filteredItems = new ArrayList<>();

    private EditBox searchBox;
    private String lastSearch = "";

    public PaletteScreen() {
        super(Component.literal("Palette"));
        loadEntries();
    }

    public static void GUI_open() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.hasPermissions(2)) {
            mc.setScreen(new PaletteScreen());
        }
    }

    private void loadEntries() {
        if (!isCacheLoaded) {
            CACHED_BLOCKS.clear();
            CACHED_ITEMS.clear();

            for (Block block : ForgeRegistries.BLOCKS) {
                ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
                if (id != null && id.getNamespace().equals(MOD_ID) && block != Blocks.AIR) {
                    if (!(block instanceof DoorBlock)) {
                        CACHED_BLOCKS.add(block);
                    }
                }
            }

            for (Item item : ForgeRegistries.ITEMS) {
                ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
                if (id != null && id.getNamespace().equals(MOD_ID)) {
                    if (!(item instanceof BlockItem) || (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof DoorBlock)) {
                        CACHED_ITEMS.add(item);
                    }
                }
            }
            isCacheLoaded = true;
        }

        // Initialize filtered lists with full content
        filteredBlocks = new ArrayList<>(CACHED_BLOCKS);
        filteredItems = new ArrayList<>(CACHED_ITEMS);
    }

    private void updateSearch() {
        if (searchBox == null) return;
        
        String search = searchBox.getValue().toLowerCase().trim();

        if (search.isEmpty()) {
            filteredBlocks = new ArrayList<>(CACHED_BLOCKS);
            filteredItems = new ArrayList<>(CACHED_ITEMS);
        } else {
            filteredBlocks = CACHED_BLOCKS.stream()
                    .filter(block -> {
                        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
                        return id != null && id.getPath().toLowerCase().contains(search);
                    })
                    .collect(Collectors.toList());

            filteredItems = CACHED_ITEMS.stream()
                    .filter(item -> {
                        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
                        return id != null && id.getPath().toLowerCase().contains(search);
                    })
                    .collect(Collectors.toList());
        }

        currentPage = 0;
        init(); 
    }

    private void giveItem(ItemStack stack) {
        if (this.minecraft == null || this.minecraft.player == null) return;

        ResourceLocation resLoc = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (resLoc == null) return;

        String itemId = resLoc.toString();
        String command = "give @s " + itemId + " " + stack.getCount();

        if (this.minecraft.player.connection != null) {
            this.minecraft.player.connection.sendCommand(command);
        }
    }

    @Override
    protected void init() {
        // --- SCALE CALCULATION ---
        double currentGuiScale = this.minecraft.getWindow().getGuiScale();
        this.scaleFactor = (float) (FORCED_SCALE / currentGuiScale);

        this.width = (int) (this.minecraft.getWindow().getWidth() / FORCED_SCALE);
        this.height = (int) (this.minecraft.getWindow().getHeight() / FORCED_SCALE);

        // State preservation
        boolean wasFocused = (searchBox != null && searchBox.isFocused());
        int cursorPos = (searchBox != null) ? searchBox.getCursorPosition() : 0;
        String currentText = (searchBox != null) ? searchBox.getValue() : lastSearch;

        this.clearWidgets();

        // --- ADAPTIVE LAYOUT CALCULATION ---
        // 1. Calculate how many columns fit (min 5, max unlimited)
        // 32px is slot width. We leave 20px margin.
        columns = Math.max(5, (width - 20) / 32);
        
        // 2. Calculate real grid width
        gridWidth = columns * 32;
        
        // 3. Determine 'start X' to center everything
        contentLeft = (width - gridWidth) / 2;
        
        // 4. Calculate available height and rows
        // Top area takes ~60px (Search + Buttons). Bottom margin ~10px.
        // 23px is slot height.
        int availableHeight = height - 70;
        rows = Math.max(1, availableHeight / 23);
        
        // 5. Determine 'start Y' to center vertically in remaining space
        int gridHeight = rows * 23;
        // Start after header (60px) + half the remaining space
        contentTop = 60 + (availableHeight - gridHeight) / 2;
        // -----------------------------------

        // Search box (Centered)
        searchBox = new EditBox(font, width / 2 - 100, 10, 200, 20, Component.literal("Search"));
        searchBox.setMaxLength(50);
        searchBox.setHint(Component.literal("Search..."));
        searchBox.setValue(currentText);
        searchBox.setFocused(wasFocused);
        searchBox.setCursorPosition(cursorPos);
        addRenderableWidget(searchBox);
        if (wasFocused) this.setFocused(searchBox);

        // --- BUTTONS ALIGNED TO GRID ---
        // Left side of grid
        addRenderableWidget(Button.builder(Component.literal("Blocks"), b -> switchMode(true))
                .bounds(contentLeft, 35, 60, 20) // Aligned to grid start
                .build());
        addRenderableWidget(Button.builder(Component.literal("Items"), b -> switchMode(false))
                .bounds(contentLeft + 65, 35, 60, 20)
                .build());

        // Right side of grid
        addRenderableWidget(Button.builder(Component.literal("<<"), b -> prevPage())
                .bounds(contentLeft + gridWidth - 85, 35, 40, 20) // Aligned relative to grid end
                .build());
        addRenderableWidget(Button.builder(Component.literal(">>"), b -> nextPage())
                .bounds(contentLeft + gridWidth - 40, 35, 40, 20) // Aligned to grid end
                .build());


        // --- GRID GENERATION ---
        List<?> currentList = showingBlocks ? filteredBlocks : filteredItems;
        int itemsPerPage = columns * rows;
        if (itemsPerPage <= 0) itemsPerPage = 1;
        
        int start = currentPage * itemsPerPage;
        int end = Math.min(start + itemsPerPage, currentList.size());

        for (int i = start; i < end; i++) {
            int idx = i - start;
            int row = idx / columns;
            int col = idx % columns;
            
            // Use calculated positions
            int x = contentLeft + col * 32;
            int y = contentTop + row * 23;

            if (showingBlocks) {
                addRenderableWidget(new ItemSlotButton(x, y, (Block) currentList.get(i)));
            } else {
                addRenderableWidget(new ItemSlotButton(x, y, (Item) currentList.get(i)));
            }
        }
    }

    // --- INPUT OVERRIDES ---
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX / scaleFactor, mouseY / scaleFactor, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX / scaleFactor, mouseY / scaleFactor, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return super.mouseDragged(mouseX / scaleFactor, mouseY / scaleFactor, button, dragX / scaleFactor, dragY / scaleFactor);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX / scaleFactor, mouseY / scaleFactor);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return super.mouseScrolled(mouseX / scaleFactor, mouseY / scaleFactor, delta);
    }
    // -----------------------

    private void switchMode(boolean blocks) {
        showingBlocks = blocks;
        currentPage = 0;
        init();
    }

    private void prevPage() {
        if (currentPage > 0) {
            currentPage--;
            init();
        }
    }

    private void nextPage() {
        if (currentPage < getTotalPages() - 1) {
            currentPage++;
            init();
        }
    }

    private int getTotalPages() {
        int total = showingBlocks ? filteredBlocks.size() : filteredItems.size();
        if (total == 0) return 1;
        int itemsPerPage = columns * rows;
        if (itemsPerPage == 0) return 1;
        return (int) Math.ceil((double) total / itemsPerPage);
    }

    @Override
    public void tick() {
        super.tick();
        if (searchBox != null) {
            searchBox.tick();
            String currentSearch = searchBox.getValue();
            if (!currentSearch.equals(lastSearch)) {
                lastSearch = currentSearch;
                updateSearch();
            }
        }
    }

	@Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 1. REMOVED: this.renderBackground(guiGraphics); 
        // Removing this allows the actual game world to be visible behind the UI.

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scaleFactor, scaleFactor, 1.0f);

        int scaledMouseX = (int)(mouseX / scaleFactor);
        int scaledMouseY = (int)(mouseY / scaleFactor);

        // This draws the dark background ONLY for the header section.
        guiGraphics.fill(0, 0, width, 58, 0x90000000); 
        
        // The horizontal separator line
        guiGraphics.hLine(0, width, 58, 0xFF555555);

        // Title
        guiGraphics.drawCenteredString(font, "Palette", width / 2, 38, 0xFFFFFF);

        int totalPages = getTotalPages();
        if (totalPages > 1) {
            String pageText = (currentPage + 1) + " / " + totalPages;
            guiGraphics.drawCenteredString(font, pageText, width / 2, 48, 0x808080);
        }

        int resultCount = showingBlocks ? filteredBlocks.size() : filteredItems.size();
        if (!lastSearch.isEmpty()) {
            String resultsText = resultCount + " results";
            // Placed at X: 7, Y: 45 to stay clear of the "Blocks" button
            guiGraphics.drawString(font, resultsText, 7, 4, 0xAAAAAA);
        }

        super.render(guiGraphics, scaledMouseX, scaledMouseY, partialTick);

        guiGraphics.pose().popPose();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 || keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        }

        if (searchBox != null && searchBox.isFocused()) {
            return searchBox.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (searchBox != null && searchBox.isFocused()) {
            return searchBox.charTyped(codePoint, modifiers);
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private class ItemSlotButton extends AbstractWidget {
        private final Block block;
        private final Item item;

        public ItemSlotButton(int x, int y, Block block) {
            super(x, y, 23, 21, Component.empty());
            this.block = block;
            this.item = null;
        }

        public ItemSlotButton(int x, int y, Item item) {
            super(x, y, 23, 21, Component.empty());
            this.block = null;
            this.item = item;
        }

        @Override
        public void renderWidget(GuiGraphics g, int mx, int my, float pt) {
            RenderSystem.setShaderTexture(0, SLOT_TEXTURE);

            int textureY = isHovered ? 20 : 0;
            g.blit(SLOT_TEXTURE, getX(), getY(), 0, textureY, 23, 21, 23, 41);

            RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();

            if (block != null) {
                g.renderItem(new ItemStack(block), getX() + 4, getY() + 3);
            } else if (item != null) {
                g.renderItem(new ItemStack(item), getX() + 4, getY() + 3);
            }

            RenderSystem.disableBlend();
        }

        @Override
        public void onClick(double mx, double my) {
            if (block != null) {
                giveItem(new ItemStack(block, 64));
            } else if (item != null) {
                giveItem(new ItemStack(item, 1));
            }
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {}
    }
}
