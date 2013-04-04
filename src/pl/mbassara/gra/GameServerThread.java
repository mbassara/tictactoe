package pl.mbassara.gra;

import java.rmi.RemoteException;

import pl.mbassara.gra.model.Board;
import pl.mbassara.gra.model.FieldState;
import pl.mbassara.gra.model.Shot;
import pl.mbassara.gra.remotes.IPlayer;
import pl.mbassara.gra.remotes.ServerImpl.IPlayerUnregisteredListener;

public class GameServerThread extends Thread implements
		IPlayerUnregisteredListener {

	private static int instanceCount = 0;
	private IPlayer xPlayer, oPlayer;
	private int boardSize;

	public GameServerThread(IPlayer oPlayer, IPlayer xPlayer, int boardSize) {
		this.xPlayer = xPlayer;
		this.oPlayer = oPlayer;
		setName("GameServer #" + (instanceCount++));
		this.boardSize = boardSize;
	}

	@Override
	public void run() {
		Board board = new Board(boardSize);
		Shot shot = null;

		String xNick = "xPlayer", oNick = "oPlayer";

		try {
			xNick = xPlayer.getNick();
			oNick = oPlayer.getNick();
			System.out.println(getName() + " started. " + oNick + " vs. "
					+ xNick);

			oPlayer.startGame();

			while (board.getGameResult() == null && xPlayer != null
					&& oPlayer != null) {

				do {
					try {
						shot = oPlayer.getShot();
					} catch (Exception e) {
						oPlayer = null;
					}
					sleep(500);
				} while (shot == null && board.getGameResult() == null
						&& xPlayer != null && oPlayer != null);

				if (shot == null)
					break;

				board.shoot(shot);

				xPlayer.receiveShot(shot);

				do {
					try {
						shot = xPlayer.getShot();
					} catch (Exception e) {
						xPlayer = null;
					}
					sleep(500);
				} while (shot == null && board.getGameResult() == null
						&& xPlayer != null && oPlayer != null);

				if (shot == null)
					break;

				board.shoot(shot);

				oPlayer.receiveShot(shot);
			}

			if (xPlayer == null)
				oPlayer.receiveGameResult(oPlayer);
			else if (oPlayer == null)
				xPlayer.receiveGameResult(xPlayer);
			else if (board.getGameResult() == FieldState.X) {
				xPlayer.receiveGameResult(xPlayer);
				oPlayer.receiveGameResult(xPlayer);
			} else if (board.getGameResult() == FieldState.O) {
				xPlayer.receiveGameResult(oPlayer);
				oPlayer.receiveGameResult(oPlayer);
			} else if (board.getGameResult() == FieldState.Empty) {
				xPlayer.receiveGameResult(null);
				oPlayer.receiveGameResult(null);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		String result = "Game ended! ";
		if (board.getGameResult() == FieldState.Empty)
			result += "There is a draw...";
		else if (board.getGameResult() == FieldState.O)
			result += oNick + " won!";
		else if (board.getGameResult() == FieldState.X)
			result += xNick + " won!";
		System.out.println(result);
	}

	@Override
	public void playerUnregistered(IPlayer player) {
		if (player.equals(xPlayer))
			xPlayer = null;
		if (player.equals(oPlayer))
			xPlayer = null;
	}
}
