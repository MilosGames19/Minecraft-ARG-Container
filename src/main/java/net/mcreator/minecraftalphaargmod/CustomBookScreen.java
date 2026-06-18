package net.mcreator.minecraftalphaargmod.client.book;

import net.mcreator.minecraftalphaargmod.network.CustomBookNetwork;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class CustomBookScreen extends Screen {

	public enum Mode {
		WRITABLE, READ_ONLY
	}

	private static final int BG_SIZE = 192;
	private static final int TEXT_W = 114;
	private static final int TEXT_H = 128;
	private static final int LINE_H = 9;
	private static final int MAX_VISIBLE_LINES = TEXT_H / LINE_H;
	private static final int CHAR_LIMIT_PER_PAGE = 1024;
	private static final int PAGE_LIMIT = 100;
	private static final int MARGIN_TOP = 2;
	private static final String DATA_TAG_PAGES = "pages";

	private static final Component TITLE_PROMPT = Component.translatable("book.editTitle");
	private static final Component SAVE_WARNING = Component.translatable("book.finalizeWarning");
	private static final FormattedCharSequence CURSOR_DARK = FormattedCharSequence.forward("_", Style.EMPTY.withColor(ChatFormatting.BLACK));
	private static final FormattedCharSequence CURSOR_LIGHT = FormattedCharSequence.forward("_", Style.EMPTY.withColor(ChatFormatting.GRAY));

	private final ItemStack documentStack;
	private final InteractionHand activeHand;
	private final Mode accessMode;
	private final BookDefinition bookMeta;
	private final List<String> documentPages = new ArrayList<>();

	private int activePageIndex = 0;
	private int renderTick = 0;
	private boolean hasUnsavedEdits = false;
	private boolean isAwaitingSignature = false;
	private String documentTitle = "";
	private long previousClickTime = 0L;
	private int previousClickIndex = -1;

	@Nullable
	private TextFieldHelper contentEditor;
	private final TextFieldHelper titleEditor = new TextFieldHelper(() -> documentTitle, text -> documentTitle = text, this::readClipboard, this::writeClipboard, text -> text.length() < 16);

	@Nullable
	private PageRenderData pageCache;
	private Component paginationText = CommonComponents.EMPTY;
	private int originX;

	private CustomNavButton btnNextPage;
	private CustomNavButton btnPrevPage;
	private Button btnApplyChanges;
	private Button btnInitSignature;
	private Button btnConfirmSignature;
	private Button btnCancelSignature;

	public CustomBookScreen(ItemStack stack, InteractionHand hand, Mode mode, BookDefinition meta) {
		super(meta.getTitle());
		this.documentStack = stack;
		this.activeHand = hand;
		this.accessMode = mode;
		this.bookMeta = meta;
		extractPagesFromNbt();
	}

	private void extractPagesFromNbt() {
		documentPages.clear();
		var nbt = documentStack.getTag();
		if (nbt != null && nbt.contains(DATA_TAG_PAGES, 9)) {
			var nbtList = nbt.getList(DATA_TAG_PAGES, 8);
			for (int i = 0; i < nbtList.size(); i++) {
				documentPages.add(nbtList.getString(i));
			}
		}
		if (documentPages.isEmpty())
			documentPages.add("");
	}

	private void syncDataToServer(boolean signDocument) {
		if (!hasUnsavedEdits && !signDocument)
			return;

		var iterator = documentPages.listIterator(documentPages.size());
		while (iterator.hasPrevious() && iterator.previous().isEmpty()) {
			iterator.remove();
		}

		int inventorySlot = activeHand == InteractionHand.MAIN_HAND ? minecraft.player.getInventory().selected : 40;

		if (signDocument) {
			CustomBookNetwork.sendSign(inventorySlot, documentPages, documentTitle.trim());
		} else {
			CustomBookNetwork.sendSave(inventorySlot, documentPages);
		}
	}

	private String fetchActivePageData() {
		return (activePageIndex >= 0 && activePageIndex < documentPages.size()) ? documentPages.get(activePageIndex) : "";
	}

	private void updateActivePageData(String newText) {
		if (activePageIndex >= 0 && activePageIndex < documentPages.size()) {
			documentPages.set(activePageIndex, newText);
			hasUnsavedEdits = true;
			invalidateCache();
		}
	}

	private String readClipboard() {
		return minecraft != null ? TextFieldHelper.getClipboardContents(minecraft) : "";
	}

	private void writeClipboard(String content) {
		if (minecraft != null)
			TextFieldHelper.setClipboardContents(minecraft, content);
	}

	@Override
	protected void init() {
		originX = (width - BG_SIZE) / 2;

		if (accessMode == Mode.WRITABLE) {
			contentEditor = new TextFieldHelper(this::fetchActivePageData, this::updateActivePageData, this::readClipboard, this::writeClipboard, input -> input.length() < CHAR_LIMIT_PER_PAGE && font.wordWrapHeight(input, TEXT_W) <= TEXT_H);
		}

		btnInitSignature = addRenderableWidget(Button.builder(Component.translatable("book.signButton"), btn -> {
			isAwaitingSignature = true;
			refreshButtonStates();
		}).bounds(width / 2 - 100, 196, 98, 20).build());

		int dynamicDoneX = (accessMode == Mode.READ_ONLY) ? width / 2 - 100 : width / 2 + 2;
		int dynamicDoneW = (accessMode == Mode.READ_ONLY) ? 200 : 98;

		btnApplyChanges = addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, btn -> {
			syncDataToServer(false);
			onClose();
		}).bounds(dynamicDoneX, 196, dynamicDoneW, 20).build());

		btnConfirmSignature = addRenderableWidget(Button.builder(Component.translatable("book.finalizeButton"), btn -> {
			if (isAwaitingSignature) {
				syncDataToServer(true);
				minecraft.setScreen(null);
			}
		}).bounds(width / 2 - 100, 196, 98, 20).build());

		btnCancelSignature = addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, btn -> {
			if (isAwaitingSignature)
				isAwaitingSignature = false;
			refreshButtonStates();
		}).bounds(width / 2 + 2, 196, 98, 20).build());

		btnNextPage = addRenderableWidget(new CustomNavButton(originX + 116, MARGIN_TOP + 157, true, btn -> turnPageForward()));
		btnPrevPage = addRenderableWidget(new CustomNavButton(originX + 43, MARGIN_TOP + 157, false, btn -> turnPageBackward()));

		if (accessMode == Mode.READ_ONLY) {
			btnInitSignature.visible = false;
			btnConfirmSignature.visible = false;
			btnCancelSignature.visible = false;
		}

		refreshButtonStates();
		invalidateCache();
	}

	@Override
	public void tick() {
		super.tick();
		renderTick++;
	}

	@Override
	public void onClose() {
		if (accessMode == Mode.WRITABLE && !isAwaitingSignature)
			syncDataToServer(false);
		super.onClose();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private void refreshButtonStates() {
		if (accessMode == Mode.READ_ONLY) {
			btnPrevPage.visible = activePageIndex > 0;
			btnNextPage.visible = activePageIndex < documentPages.size() - 1;
			return;
		}
		btnPrevPage.visible = !isAwaitingSignature && activePageIndex > 0;
		btnNextPage.visible = !isAwaitingSignature;
		btnApplyChanges.visible = !isAwaitingSignature;
		btnInitSignature.visible = !isAwaitingSignature;
		btnCancelSignature.visible = isAwaitingSignature;
		btnConfirmSignature.visible = isAwaitingSignature;
		btnConfirmSignature.active = !documentTitle.trim().isEmpty();
	}

	private void appendNewPage() {
		if (documentPages.size() < Math.min(PAGE_LIMIT, bookMeta.getMaxPages())) {
			documentPages.add("");
			hasUnsavedEdits = true;
		}
	}

	private void turnPageForward() {
		if (activePageIndex < documentPages.size() - 1) {
			activePageIndex++;
		} else if (accessMode == Mode.WRITABLE) {
			appendNewPage();
			if (activePageIndex < documentPages.size() - 1)
				activePageIndex++;
		}
		triggerPageUpdate();
	}

	private void turnPageBackward() {
		if (activePageIndex > 0) {
			activePageIndex--;
			triggerPageUpdate();
		}
	}

	private void triggerPageUpdate() {
		if (contentEditor != null)
			contentEditor.setCursorToEnd();
		invalidateCache();
		refreshButtonStates();
	}

	private void invalidateCache() {
		pageCache = null;
	}

	private PageRenderData fetchRenderCache() {
		if (pageCache == null) {
			String activeText = fetchActivePageData();
			pageCache = activeText.isEmpty() ? PageRenderData.createEmpty(originX + 36, MARGIN_TOP + 30) : PageRenderData.generate(activeText, font, contentEditor, TEXT_W, LINE_H, originX + 36, MARGIN_TOP + 30);
			paginationText = Component.translatable("book.pageIndicator", activePageIndex + 1, Math.max(documentPages.size(), 1));
		}
		return pageCache;
	}

	private int translateToLocalX(int screenX) {
		return screenX - originX - 36;
	}

	private int translateToLocalY(int screenY) {
		return screenY - MARGIN_TOP - 30;
	}

	@Override
	public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
		renderBackground(gfx);
		setFocused((GuiEventListener) null);

		gfx.blit(bookMeta.getTexture(), originX, MARGIN_TOP, 0, 0, BG_SIZE, BG_SIZE);

		if (isAwaitingSignature) {
			drawSignatureOverlay(gfx);
		} else {
			int indicatorWidth = font.width(paginationText);
			gfx.drawString(font, paginationText, originX + BG_SIZE - 44 - indicatorWidth, MARGIN_TOP + 16, 0x000000, false);

			if (accessMode == Mode.WRITABLE)
				drawEditorElements(gfx);
			else
				drawStaticText(gfx);
		}

		super.render(gfx, mouseX, mouseY, partialTick);
	}

	private void drawSignatureOverlay(GuiGraphics gfx) {
		boolean isBlinking = renderTick / 6 % 2 == 0;
		FormattedCharSequence formattedTitle = FormattedCharSequence.composite(FormattedCharSequence.forward(documentTitle, Style.EMPTY), isBlinking ? CURSOR_DARK : CURSOR_LIGHT);

		int labelW = font.width(TITLE_PROMPT);
		gfx.drawString(font, TITLE_PROMPT, originX + 36 + (TEXT_W - labelW) / 2, MARGIN_TOP + 32, 0x000000, false);

		int titleW = font.width(formattedTitle);
		gfx.drawString(font, formattedTitle, originX + 36 + (TEXT_W - titleW) / 2, MARGIN_TOP + 48, 0x000000, false);

		Component authorTag = Component.translatable("book.byAuthor", minecraft.player.getName()).withStyle(ChatFormatting.DARK_GRAY);
		int authorW = font.width(authorTag);
		gfx.drawString(font, authorTag, originX + 36 + (TEXT_W - authorW) / 2, MARGIN_TOP + 58, 0x000000, false);

		gfx.drawWordWrap(font, SAVE_WARNING, originX + 36, MARGIN_TOP + 80, TEXT_W, 0x000000);
	}

	private void drawEditorElements(GuiGraphics gfx) {
		PageRenderData cache = fetchRenderCache();

		for (Rect2i rect : cache.highlightBoxes()) {
			gfx.fill(RenderType.guiTextHighlight(), rect.getX(), rect.getY(), rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight(), -16776961);
		}

		for (TextLine line : cache.lines()) {
			gfx.drawString(font, line.component(), line.x(), line.y(), 0x000000, false);
		}

		if (renderTick / 6 % 2 == 0) {
			if (!cache.isCursorAtEnd()) {
				gfx.fill(cache.cursorX(), cache.cursorY() - 1, cache.cursorX() + 1, cache.cursorY() + LINE_H, -16777216);
			} else {
				gfx.drawString(font, "_", cache.cursorX(), cache.cursorY(), 0x000000, false);
			}
		}
	}

	private void drawStaticText(GuiGraphics gfx) {
		PageRenderData cache = fetchRenderCache();
		int visibleCount = Math.min(MAX_VISIBLE_LINES, cache.lines().length);
		for (int i = 0; i < visibleCount; i++) {
			gfx.drawString(font, cache.lines()[i].component(), cache.lines()[i].x(), cache.lines()[i].y(), 0x000000, false);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (super.mouseClicked(mouseX, mouseY, button))
			return true;
		if (button == 0 && accessMode == Mode.WRITABLE && contentEditor != null && !isAwaitingSignature) {
			long currentTime = Util.getMillis();
			int index = fetchRenderCache().resolveIndexAtPosition(font, translateToLocalX((int) mouseX), translateToLocalY((int) mouseY));

			if (index >= 0) {
				if (index == previousClickIndex && currentTime - previousClickTime < 250L) {
					if (!contentEditor.isSelecting())
						highlightWordBounds(index);
					else
						contentEditor.selectAll();
				} else {
					contentEditor.setCursorPos(index, Screen.hasShiftDown());
				}
				invalidateCache();
			}
			previousClickIndex = index;
			previousClickTime = currentTime;
		}
		return true;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (super.mouseDragged(mouseX, mouseY, button, dragX, dragY))
			return true;
		if (button == 0 && accessMode == Mode.WRITABLE && contentEditor != null && !isAwaitingSignature) {
			int index = fetchRenderCache().resolveIndexAtPosition(font, translateToLocalX((int) mouseX), translateToLocalY((int) mouseY));
			contentEditor.setCursorPos(index, true);
			invalidateCache();
		}
		return true;
	}

	private void highlightWordBounds(int index) {
		String activeText = fetchActivePageData();
		contentEditor.setSelectionRange(StringSplitter.getWordPosition(activeText, -1, index, false), StringSplitter.getWordPosition(activeText, 1, index, false));
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers))
			return true;

		if (isAwaitingSignature)
			return handleTitleInput(keyCode);

		if (keyCode == GLFW.GLFW_KEY_PAGE_UP) {
			btnPrevPage.onPress();
			return true;
		}
		if (keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
			btnNextPage.onPress();
			return true;
		}
		if (accessMode != Mode.WRITABLE || contentEditor == null)
			return false;

		boolean inputHandled = handleEditorInput(keyCode);
		if (inputHandled)
			invalidateCache();
		return inputHandled;
	}

	private boolean handleEditorInput(int key) {
		if (Screen.isSelectAll(key)) {
			contentEditor.selectAll();
			return true;
		}
		if (Screen.isCopy(key)) {
			contentEditor.copy();
			return true;
		}
		if (Screen.isPaste(key)) {
			contentEditor.paste();
			return true;
		}
		if (Screen.isCut(key)) {
			contentEditor.cut();
			return true;
		}

		TextFieldHelper.CursorStep stepModifier = Screen.hasControlDown() ? TextFieldHelper.CursorStep.WORD : TextFieldHelper.CursorStep.CHARACTER;

		return switch (key) {
			case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> {
				contentEditor.insertText("\n");
				yield true;
			}
			case GLFW.GLFW_KEY_BACKSPACE -> {
				contentEditor.removeFromCursor(-1, stepModifier);
				yield true;
			}
			case GLFW.GLFW_KEY_DELETE -> {
				contentEditor.removeFromCursor(1, stepModifier);
				yield true;
			}
			case GLFW.GLFW_KEY_RIGHT -> {
				contentEditor.moveBy(1, Screen.hasShiftDown(), stepModifier);
				yield true;
			}
			case GLFW.GLFW_KEY_LEFT -> {
				contentEditor.moveBy(-1, Screen.hasShiftDown(), stepModifier);
				yield true;
			}
			case GLFW.GLFW_KEY_DOWN -> {
				moveCursorLine(1);
				yield true;
			}
			case GLFW.GLFW_KEY_UP -> {
				moveCursorLine(-1);
				yield true;
			}
			case GLFW.GLFW_KEY_HOME -> {
				moveCursorToBound(true);
				yield true;
			}
			case GLFW.GLFW_KEY_END -> {
				moveCursorToBound(false);
				yield true;
			}
			default -> false;
		};
	}

	private boolean handleTitleInput(int key) {
		switch (key) {
			case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> {
				if (!documentTitle.isEmpty()) {
					syncDataToServer(true);
					minecraft.setScreen(null);
				}
				return true;
			}
			case GLFW.GLFW_KEY_BACKSPACE -> {
				titleEditor.removeCharsFromCursor(-1);
				refreshButtonStates();
				hasUnsavedEdits = true;
				return true;
			}
			default -> {
				return false;
			}
		}
	}

	@Override
	public boolean charTyped(char character, int modifiers) {
		if (super.charTyped(character, modifiers))
			return true;
		if (accessMode != Mode.WRITABLE)
			return false;

		if (isAwaitingSignature) {
			boolean isValid = titleEditor.charTyped(character);
			if (isValid) {
				refreshButtonStates();
				hasUnsavedEdits = true;
			}
			return isValid;
		}

		if (contentEditor == null || !SharedConstants.isAllowedChatCharacter(character))
			return false;
		contentEditor.insertText(Character.toString(character));
		invalidateCache();
		return true;
	}

	private void moveCursorLine(int offset) {
		if (contentEditor == null)
			return;
		contentEditor.setCursorPos(fetchRenderCache().calculateVerticalOffset(contentEditor.getCursorPos(), offset), Screen.hasShiftDown());
		invalidateCache();
	}

	private void moveCursorToBound(boolean toHome) {
		if (contentEditor == null)
			return;
		if (Screen.hasControlDown()) {
			if (toHome)
				contentEditor.setCursorToStart(Screen.hasShiftDown());
			else
				contentEditor.setCursorToEnd(Screen.hasShiftDown());
		} else {
			int newPos = toHome ? fetchRenderCache().locateLineStart(contentEditor.getCursorPos()) : fetchRenderCache().locateLineEnd(contentEditor.getCursorPos());
			contentEditor.setCursorPos(newPos, Screen.hasShiftDown());
		}
		invalidateCache();
	}

	private record TextLine(Style style, String rawText, Component component, int x, int y) {
	}

	private record PageRenderData(TextLine[] lines, int[] lineStarts, int cursorX, int cursorY, boolean isCursorAtEnd, Rect2i[] highlightBoxes) {

		static PageRenderData createEmpty(int originX, int originY) {
			return new PageRenderData(new TextLine[]{new TextLine(Style.EMPTY, "", Component.literal(""), originX, originY)}, new int[]{0}, originX, originY, true, new Rect2i[0]);
		}

		static PageRenderData generate(String fullText, Font fontRenderer, @Nullable TextFieldHelper textHandler, int maxW, int lineH, int originX, int originY) {
			StringSplitter engine = fontRenderer.getSplitter();
			List<TextLine> processedLines = new ArrayList<>();
			List<Integer> splitIndices = new ArrayList<>();
			int[] lineIterator = {0};
			boolean[] endsWithReturn = {false};

			engine.splitLines(fullText, maxW, Style.EMPTY, true, (style, start, end) -> {
				String subSequence = fullText.substring(start, end);
				endsWithReturn[0] = subSequence.endsWith("\n");
				String visualFormat = StringUtils.stripEnd(subSequence, " \n");

				processedLines.add(new TextLine(style, visualFormat, Component.literal(visualFormat).setStyle(style), originX, originY + (lineIterator[0] * lineH)));
				splitIndices.add(start);
				lineIterator[0]++;
			});

			int[] startsArray = splitIndices.stream().mapToInt(Integer::intValue).toArray();

			int logicalCursor = textHandler != null ? textHandler.getCursorPos() : fullText.length();
			boolean cursorAtMax = logicalCursor == fullText.length();
			int cX = 0, cY = 0;

			if (cursorAtMax && endsWithReturn[0]) {
				cX = originX;
				cY = originY + processedLines.size() * lineH;
			} else {
				int activeLine = locateLineIndex(startsArray, logicalCursor);
				int textOffset = fontRenderer.width(fullText.substring(startsArray[activeLine], logicalCursor));
				cX = originX + textOffset;
				cY = originY + activeLine * lineH;
			}

			List<Rect2i> highlights = new ArrayList<>();
			if (textHandler != null && textHandler.getCursorPos() != textHandler.getSelectionPos()) {
				int boundMin = Math.min(textHandler.getCursorPos(), textHandler.getSelectionPos());
				int boundMax = Math.max(textHandler.getCursorPos(), textHandler.getSelectionPos());
				int lineMin = locateLineIndex(startsArray, boundMin);
				int lineMax = locateLineIndex(startsArray, boundMax);

				if (lineMin == lineMax) {
					highlights.add(buildPartialHighlight(fullText, engine, boundMin, boundMax, lineMin * lineH, startsArray[lineMin], originX, originY));
				} else {
					int nextLineStart = lineMin + 1 >= startsArray.length ? fullText.length() : startsArray[lineMin + 1];
					highlights.add(buildPartialHighlight(fullText, engine, boundMin, nextLineStart, lineMin * lineH, startsArray[lineMin], originX, originY));

					for (int midLine = lineMin + 1; midLine < lineMax; midLine++) {
						String segment = fullText.substring(startsArray[midLine], startsArray[midLine + 1]);
						int segmentW = (int) engine.stringWidth(segment);
						highlights.add(constructBounds(0, midLine * lineH, segmentW, midLine * lineH + lineH, originX, originY));
					}
					highlights.add(buildPartialHighlight(fullText, engine, startsArray[lineMax], boundMax, lineMax * lineH, startsArray[lineMax], originX, originY));
				}
			}

			return new PageRenderData(processedLines.toArray(new TextLine[0]), startsArray, cX, cY, cursorAtMax, highlights.toArray(new Rect2i[0]));
		}

		private static Rect2i buildPartialHighlight(String text, StringSplitter engine, int rangeStart, int rangeEnd, int localY, int lineOrigin, int originX, int originY) {
			int w1 = (int) engine.stringWidth(text.substring(lineOrigin, rangeStart));
			int w2 = (int) engine.stringWidth(text.substring(lineOrigin, rangeEnd));
			return constructBounds(w1, localY, w2, localY + LINE_H, originX, originY);
		}

		private static Rect2i constructBounds(int x1, int y1, int x2, int y2, int offsetX, int offsetY) {
			int absX1 = offsetX + x1;
			int absY1 = offsetY + y1;
			int absX2 = offsetX + x2;
			int absY2 = offsetY + y2;
			return new Rect2i(Math.min(absX1, absX2), Math.min(absY1, absY2), Math.abs(absX2 - absX1), Math.abs(absY2 - absY1));
		}

		int resolveIndexAtPosition(Font font, int localX, int localY) {
			int lineIdx = localY / LINE_H;
			if (lineIdx < 0)
				return 0;
			if (lineIdx >= lines.length)
				return lines.length > 0 ? lineStarts[lines.length - 1] + lines[lines.length - 1].rawText().length() : 0;
			TextLine target = lines[lineIdx];
			return lineStarts[lineIdx] + font.getSplitter().plainIndexAtWidth(target.rawText(), Math.max(localX, 0), target.style());
		}

		int calculateVerticalOffset(int currentIndex, int rowOffset) {
			if (lineStarts.length == 0)
				return currentIndex;
			int currentLine = locateLineIndex(lineStarts, currentIndex);
			int targetLine = currentLine + rowOffset;
			if (targetLine < 0 || targetLine >= lineStarts.length)
				return currentIndex;
			return lineStarts[targetLine] + Math.min(currentIndex - lineStarts[currentLine], lines[targetLine].rawText().length());
		}

		int locateLineStart(int currentIndex) {
			return lineStarts.length > 0 ? lineStarts[locateLineIndex(lineStarts, currentIndex)] : 0;
		}

		int locateLineEnd(int currentIndex) {
			if (lineStarts.length == 0)
				return 0;
			int currentLine = locateLineIndex(lineStarts, currentIndex);
			return lineStarts[currentLine] + lines[currentLine].rawText().length();
		}

		private static int locateLineIndex(int[] startIndices, int searchPos) {
			int index = Arrays.binarySearch(startIndices, searchPos);
			return Mth.clamp(index < 0 ? -(index + 2) : index, 0, Math.max(startIndices.length - 1, 0));
		}
	}

	private class CustomNavButton extends Button {
		private final boolean isNext;

		public CustomNavButton(int x, int y, boolean isNext, OnPress action) {
			super(x, y, 23, 13, CommonComponents.EMPTY, action, Button.DEFAULT_NARRATION);
			this.isNext = isNext;
		}

		@Override
		public void renderWidget(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
			int texX = this.isHoveredOrFocused() ? 23 : 0;
			int texY = this.isNext ? 192 : 205;

			gfx.blit(bookMeta.getTexture(), this.getX(), this.getY(), texX, texY, this.width, this.height);
		}

		@Override
		public void playDownSound(net.minecraft.client.sounds.SoundManager soundManager) {
			soundManager.play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(net.minecraft.sounds.SoundEvents.BOOK_PAGE_TURN, 1.0F));
		}
	}
}