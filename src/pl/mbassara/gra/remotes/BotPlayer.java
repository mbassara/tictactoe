package pl.mbassara.gra.remotes;

import java.awt.Point;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import pl.mbassara.gra.model.Board;
import pl.mbassara.gra.model.FieldState;
import pl.mbassara.gra.model.Shot;

public class BotPlayer extends UnicastRemoteObject implements IPlayer {

	private static final long serialVersionUID = 3593846374339540080L;
	private Board board;
	private LinkedList<Shot> shotBuffer;
	private FieldState playerType;

	private int boardSize;

	public BotPlayer(int boardSize) throws RemoteException {
		super();
		board = new Board(boardSize);
		shotBuffer = new LinkedList<Shot>();
		playerType = FieldState.O;
		this.boardSize = boardSize;
	}

	@Override
	public String getNick() throws RemoteException {
		return "computer";
	}

	@Override
	public void startGame() throws RemoteException {
		Shot shot = new Shot(playerType,
				new Point(boardSize / 2, boardSize / 2));
		shotBuffer.offer(shot);
		System.out.println("botstart");
	}

	@Override
	public Shot getShot() throws RemoteException {
		Shot shot = null;
		if (shotBuffer.size() > 0)
			shot = shotBuffer.poll();
		else
			shot = new Shot(playerType, getBestShot());
		board.shoot(shot);
		return shot;
	}

	@Override
	public void receiveShot(Shot shot) throws RemoteException {
		board.shoot(shot);
	}

	@Override
	public void receiveGameResult(IPlayer winner) throws RemoteException {
	}

	private Point getBestShot() {
		HashMap<Point, ArrayList<Possibility>> shots = new HashMap<Point, ArrayList<Possibility>>();

		for (int i = 0; i < boardSize; i++)
			for (int j = 0; j < boardSize; j++)
				if (board.getField(i, j).getState() == FieldState.O)
					shots.put(new Point(i, j), getPossibilities(i, j));

		int max = 0;
		Point point = null;
		for (Point p : shots.keySet())
			if (shots.get(p).size() > max) {
				max = shots.get(p).size();
				point = p;
			}

		if (max == 0) { // panic mode
			for (int i = 0; i < boardSize; i++)
				for (int j = 0; j < boardSize; j++)
					if (board.getField(i, j).getState() == FieldState.Empty)
						return new Point(i, j);

			return null; // there's no empty fields
		}

		return getPossiblePoint(point, shots.get(point).get(0));
	}

	private ArrayList<Possibility> getPossibilities(int x, int y) {
		ArrayList<Possibility> pos = new ArrayList<BotPlayer.Possibility>();

		// horizontal
		boolean possible = true;
		for (int i = 0; i < boardSize; i++)
			if (board.getField(x, i).getState() == FieldState.X)
				possible = false;

		if (possible)
			pos.add(Possibility.HOR);

		// vertical
		possible = true;
		for (int i = 0; i < boardSize; i++)
			if (board.getField(i, y).getState() == FieldState.X)
				possible = false;

		if (possible)
			pos.add(Possibility.VER);

		// descending diagonal
		if (x == y) {
			for (int i = 0; i < boardSize; i++)
				if (board.getField(i, i).getState() == FieldState.X)
					possible = false;

			if (possible)
				pos.add(Possibility.DESC);
		}

		// ascending diagonal
		if (boardSize - x - 1 == y) {
			for (int i = 0; i < boardSize; i++)
				if (board.getField(boardSize - i - 1, i).getState() == FieldState.X)
					possible = false;

			if (possible)
				pos.add(Possibility.ASC);
		}

		return pos;
	}

	private Point getPossiblePoint(Point point, Possibility pos) {
		switch (pos) {
		case HOR:
			for (int i = 0; i < boardSize; i++)
				if (board.getField(point.x, i).getState() == FieldState.Empty)
					return new Point(point.x, i);
		case VER:
			for (int i = 0; i < boardSize; i++)
				if (board.getField(i, point.y).getState() == FieldState.Empty)
					return new Point(i, point.y);
		case ASC:
			for (int i = 0; i < boardSize; i++)
				if (board.getField(boardSize - i - 1, i).getState() == FieldState.Empty)
					return new Point(boardSize - i - 1, i);
		case DESC:
			for (int i = 0; i < boardSize; i++)
				if (board.getField(i, i).getState() == FieldState.Empty)
					return new Point(i, i);
		default:
			return null;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		System.out.println("bot is GC collected");
		super.finalize();
	}

	private enum Possibility {
		HOR, VER, ASC, DESC
	}
}
