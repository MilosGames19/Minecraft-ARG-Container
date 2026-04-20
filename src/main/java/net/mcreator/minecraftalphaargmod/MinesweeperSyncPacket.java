package net.mcreator.minecraftalphaargmod;

import net.minecraftforge.network.NetworkEvent;

import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class MinesweeperSyncPacket {
	private final boolean hasBoard;
	private final int minesRemaining;
	private final long startTime;
	private final long endTime;
	private final boolean gameOver;
	private final boolean won;
	private final long gameOverTime;

	public MinesweeperSyncPacket() {
		this.hasBoard = false;
		this.minesRemaining = 0;
		this.startTime = -1;
		this.endTime = -1;
		this.gameOver = false;
		this.won = false;
		this.gameOverTime = -1;
	}

	public MinesweeperSyncPacket(MinesweeperBoard board) {
		this.hasBoard = true;
		this.minesRemaining = board.getRemainingMines();
		this.startTime = board.getStartTime();
		this.endTime = board.getEndTime();
		this.gameOver = board.isGameOver();
		this.won = board.isWon();
		this.gameOverTime = board.getGameOverTime();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBoolean(hasBoard);
		if (hasBoard) {
			buf.writeInt(minesRemaining);
			buf.writeLong(startTime);
			buf.writeLong(endTime);
			buf.writeBoolean(gameOver);
			buf.writeBoolean(won);
			buf.writeLong(gameOverTime);
		}
	}

	public static MinesweeperSyncPacket decode(FriendlyByteBuf buf) {
		boolean hasBoard = buf.readBoolean();
		if (!hasBoard) {
			return new MinesweeperSyncPacket();
		}
		int minesRemaining = buf.readInt();
		long startTime = buf.readLong();
		long endTime = buf.readLong();
		boolean gameOver = buf.readBoolean();
		boolean won = buf.readBoolean();
		long gameOverTime = buf.readLong();
		return new MinesweeperSyncPacket(hasBoard, minesRemaining, startTime, endTime, gameOver, won, gameOverTime);
	}

	private MinesweeperSyncPacket(boolean hasBoard, int minesRemaining, long startTime, long endTime, boolean gameOver, boolean won, long gameOverTime) {
		this.hasBoard = hasBoard;
		this.minesRemaining = minesRemaining;
		this.startTime = startTime;
		this.endTime = endTime;
		this.gameOver = gameOver;
		this.won = won;
		this.gameOverTime = gameOverTime;
	}

	public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
		NetworkEvent.Context ctx = ctxSupplier.get();
		ctx.enqueueWork(() -> {
			ClientBoardCache.update(this);
		});
		ctx.setPacketHandled(true);
	}

	public boolean hasBoard() {
		return hasBoard;
	}

	public int getMinesRemaining() {
		return minesRemaining;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public boolean isWon() {
		return won;
	}

	public long getGameOverTime() {
		return gameOverTime;
	}
}
