package net.mcreator.minecraftalphaargmod;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

@OnlyIn(Dist.CLIENT)
public class ClientBoardCache {
	private static boolean hasBoard = false;
	private static int minesRemaining = 0;
	private static long startTime = -1;
	private static long endTime = -1;
	private static boolean gameOver = false;
	private static boolean won = false;
	private static long gameOverTime = -1;

	public static void update(MinesweeperSyncPacket pkt) {
		hasBoard = pkt.hasBoard();
		minesRemaining = pkt.getMinesRemaining();
		startTime = pkt.getStartTime();
		endTime = pkt.getEndTime();
		gameOver = pkt.isGameOver();
		won = pkt.isWon();
		gameOverTime = pkt.getGameOverTime();
	}

	public static boolean hasBoard() {
		return hasBoard;
	}

	public static int getMinesRemaining() {
		return minesRemaining;
	}

	public static boolean isGameOver() {
		return gameOver;
	}

	public static boolean isWon() {
		return won;
	}

	public static long getGameOverTime() {
		return gameOverTime;
	}

	public static long getElapsedSeconds() {
		if (startTime == -1)
			return 0;
		if (endTime != -1)
			return (endTime - startTime) / 1000;
		return (System.currentTimeMillis() - startTime) / 1000;
	}
}
