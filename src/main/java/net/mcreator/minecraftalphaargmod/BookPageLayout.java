package net.mcreator.minecraftalphaargmod.client.book;

import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class BookPageLayout {

    private static final int LINE_H = 9;

    private final TextLine[] lines;
    private final int[] lineStarts;
    private final int cursorX;
    private final int cursorY;
    private final boolean cursorAtEnd;
    private final Rect2i[] highlightBoxes;

    public BookPageLayout(TextLine[] lines, int[] lineStarts, int cursorX, int cursorY,
                          boolean cursorAtEnd, Rect2i[] highlightBoxes) {
        this.lines = lines;
        this.lineStarts = lineStarts;
        this.cursorX = cursorX;
        this.cursorY = cursorY;
        this.cursorAtEnd = cursorAtEnd;
        this.highlightBoxes = highlightBoxes;
    }

    public TextLine[] lines() {
        return lines;
    }

    public Rect2i[] highlightBoxes() {
        return highlightBoxes;
    }

    public int cursorX() {
        return cursorX;
    }

    public int cursorY() {
        return cursorY;
    }

    public boolean isCursorAtEnd() {
        return cursorAtEnd;
    }

    public static BookPageLayout empty(int originX, int originY) {
        return new BookPageLayout(
                new TextLine[]{new TextLine(Style.EMPTY, "", Component.literal(""), originX, originY)},
                new int[]{0},
                originX, originY, true, new Rect2i[0]);
    }

    public static BookPageLayout generate(String fullText, Font font, @Nullable TextFieldHelper textHandler,
                                          int maxWidth, int originX, int originY) {
        StringSplitter engine = font.getSplitter();
        List<TextLine> processedLines = new ArrayList<>();
        List<Integer> splitIndices = new ArrayList<>();
        int[] lineCounter = {0};
        boolean[] endsWithReturn = {false};

        engine.splitLines(fullText, maxWidth, Style.EMPTY, true, (style, start, end) -> {
            String subSequence = fullText.substring(start, end);
            endsWithReturn[0] = subSequence.endsWith("\n");
            String visual = StringUtils.stripEnd(subSequence, " \n");
            processedLines.add(new TextLine(style, visual,
                    Component.literal(visual).setStyle(style),
                    originX, originY + (lineCounter[0] * LINE_H)));
            splitIndices.add(start);
            lineCounter[0]++;
        });

        int[] startsArray = splitIndices.stream().mapToInt(Integer::intValue).toArray();

        int logicalCursor = textHandler != null ? textHandler.getCursorPos() : fullText.length();
        boolean cursorAtMax = logicalCursor == fullText.length();
        int cX, cY;

        if (cursorAtMax && endsWithReturn[0]) {
            cX = originX;
            cY = originY + processedLines.size() * LINE_H;
        } else {
            int activeLine = locateLineIndex(startsArray, logicalCursor);
            int textOffset = font.width(fullText.substring(startsArray[activeLine], logicalCursor));
            cX = originX + textOffset;
            cY = originY + activeLine * LINE_H;
        }

        Rect2i[] highlights = buildHighlights(fullText, engine, textHandler, startsArray, originX, originY);

        return new BookPageLayout(
                processedLines.toArray(new TextLine[0]),
                startsArray, cX, cY, cursorAtMax, highlights);
    }

    private static Rect2i[] buildHighlights(String text, StringSplitter engine,
                                            @Nullable TextFieldHelper textHandler,
                                            int[] startsArray, int originX, int originY) {
        if (textHandler == null || textHandler.getCursorPos() == textHandler.getSelectionPos()) {
            return new Rect2i[0];
        }
        int boundMin = Math.min(textHandler.getCursorPos(), textHandler.getSelectionPos());
        int boundMax = Math.max(textHandler.getCursorPos(), textHandler.getSelectionPos());
        int lineMin = locateLineIndex(startsArray, boundMin);
        int lineMax = locateLineIndex(startsArray, boundMax);

        List<Rect2i> highlights = new ArrayList<>();
        if (lineMin == lineMax) {
            highlights.add(buildPartialHighlight(text, engine, boundMin, boundMax,
                    lineMin * LINE_H, startsArray[lineMin], originX, originY));
        } else {
            int nextLineStart = lineMin + 1 >= startsArray.length ? text.length() : startsArray[lineMin + 1];
            highlights.add(buildPartialHighlight(text, engine, boundMin, nextLineStart,
                    lineMin * LINE_H, startsArray[lineMin], originX, originY));

            for (int midLine = lineMin + 1; midLine < lineMax; midLine++) {
                String segment = text.substring(startsArray[midLine], startsArray[midLine + 1]);
                int segmentW = (int) engine.stringWidth(segment);
                highlights.add(constructBounds(0, midLine * LINE_H, segmentW,
                        midLine * LINE_H + LINE_H, originX, originY));
            }
            highlights.add(buildPartialHighlight(text, engine, startsArray[lineMax], boundMax,
                    lineMax * LINE_H, startsArray[lineMax], originX, originY));
        }
        return highlights.toArray(new Rect2i[0]);
    }

    private static Rect2i buildPartialHighlight(String text, StringSplitter engine,
                                                int rangeStart, int rangeEnd,
                                                int localY, int lineOrigin,
                                                int originX, int originY) {
        int w1 = (int) engine.stringWidth(text.substring(lineOrigin, rangeStart));
        int w2 = (int) engine.stringWidth(text.substring(lineOrigin, rangeEnd));
        return constructBounds(w1, localY, w2, localY + LINE_H, originX, originY);
    }

    private static Rect2i constructBounds(int x1, int y1, int x2, int y2,
                                          int offsetX, int offsetY) {
        int absX1 = offsetX + x1;
        int absY1 = offsetY + y1;
        int absX2 = offsetX + x2;
        int absY2 = offsetY + y2;
        return new Rect2i(
                Math.min(absX1, absX2), Math.min(absY1, absY2),
                Math.abs(absX2 - absX1), Math.abs(absY2 - absY1));
    }

    public int resolveIndexAtPosition(Font font, int localX, int localY) {
        int lineIdx = localY / LINE_H;
        if (lineIdx < 0) return 0;
        if (lineIdx >= lines.length) {
            return lines.length > 0
                    ? lineStarts[lines.length - 1] + lines[lines.length - 1].rawText().length()
                    : 0;
        }
        TextLine target = lines[lineIdx];
        return lineStarts[lineIdx]
                + font.getSplitter().plainIndexAtWidth(target.rawText(), Math.max(localX, 0), target.style());
    }

    public int calculateVerticalOffset(int currentIndex, int rowOffset) {
        if (lineStarts.length == 0) return currentIndex;
        int currentLine = locateLineIndex(lineStarts, currentIndex);
        int targetLine = currentLine + rowOffset;
        if (targetLine < 0 || targetLine >= lineStarts.length) return currentIndex;
        return lineStarts[targetLine]
                + Math.min(currentIndex - lineStarts[currentLine], lines[targetLine].rawText().length());
    }

    public int locateLineStart(int currentIndex) {
        return lineStarts.length > 0
                ? lineStarts[locateLineIndex(lineStarts, currentIndex)]
                : 0;
    }

    public int locateLineEnd(int currentIndex) {
        if (lineStarts.length == 0) return 0;
        int currentLine = locateLineIndex(lineStarts, currentIndex);
        return lineStarts[currentLine] + lines[currentLine].rawText().length();
    }

    private static int locateLineIndex(int[] startIndices, int searchPos) {
        int index = Arrays.binarySearch(startIndices, searchPos);
        return Mth.clamp(index < 0 ? -(index + 2) : index, 0, Math.max(startIndices.length - 1, 0));
    }

    public record TextLine(Style style, String rawText, Component component, int x, int y) {
    }
}
