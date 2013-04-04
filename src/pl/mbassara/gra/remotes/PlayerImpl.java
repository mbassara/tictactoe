package pl.mbassara.gra.remotes;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedList;

import pl.mbassara.gra.model.Shot;

public class PlayerImpl extends UnicastRemoteObject implements IPlayer {

	private static final long serialVersionUID = 7408238598516003715L;
	private String nick;
	private LinkedList<Shot> shotBuffer;
	private ArrayList<IShotReceivedListener> shotListeners;
	private ArrayList<IGameEndedListener> gameEndedListeners;
	private ArrayList<IStartGameListener> startGameListeners;

	public PlayerImpl(String nick) throws RemoteException {
		super();
		this.nick = nick;
		this.shotBuffer = new LinkedList<Shot>();
		shotListeners = new ArrayList<IShotReceivedListener>();
		gameEndedListeners = new ArrayList<IGameEndedListener>();
		startGameListeners = new ArrayList<IStartGameListener>();
	}

	public void addShotReceivedListener(IShotReceivedListener listener) {
		shotListeners.add(listener);
	}

	public void addGameEndedListener(IGameEndedListener listener) {
		gameEndedListeners.add(listener);
	}

	public void addStartGameListener(IStartGameListener listener) {
		startGameListeners.add(listener);
	}

	public void pushShot(Shot shot) {
		shotBuffer.offer(shot);
	}

	@Override
	public String getNick() throws RemoteException {
		return nick;
	}

	@Override
	public Shot getShot() throws RemoteException {
		return shotBuffer.poll();
	}

	@Override
	public void receiveShot(final Shot shot) throws RemoteException {
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (IShotReceivedListener l : shotListeners)
					l.shotReceived(shot);
			}
		}).start();
	}

	@Override
	public void receiveGameResult(final IPlayer winner) throws RemoteException {
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (IGameEndedListener l : gameEndedListeners)
					l.gameEnded(winner);
			}
		}).start();
	}

	@Override
	public void startGame() throws RemoteException {
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (IStartGameListener l : startGameListeners)
					l.startGame();
			}
		}).start();
	}

	public interface IShotReceivedListener {
		public void shotReceived(Shot shot);
	}

	public interface IGameEndedListener {
		public void gameEnded(IPlayer winner);
	}

	public interface IStartGameListener {
		public void startGame();
	}

	public static void disconnect(IServer server, IPlayer player) {
		try {
			if (server != null && player != null)
				server.unregisterPlayer(player);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		try {
			if (player != null)
				UnicastRemoteObject.unexportObject(player, true);
		} catch (NoSuchObjectException e) {
			e.printStackTrace();
		}
	}

}
