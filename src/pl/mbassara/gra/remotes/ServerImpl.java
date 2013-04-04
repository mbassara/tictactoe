package pl.mbassara.gra.remotes;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class ServerImpl implements IServer {

	private ArrayList<IPlayerUnregisteredListener> unregListeners;
	private ArrayList<IPlayer> players;
	private LinkedList<Pair> pairs;

	public ServerImpl() {
		unregListeners = new ArrayList<ServerImpl.IPlayerUnregisteredListener>();
		players = new ArrayList<IPlayer>();
		pairs = new LinkedList<Pair>();
	}

	public void addPlayerUnregisteredListener(IPlayerUnregisteredListener l) {
		unregListeners.add(l);
	}

	@Override
	public boolean isNickAvailable(String nick) throws RemoteException {
		boolean result = true;
		for (IPlayer player : players)
			try {
				if (player.getNick().equals(nick))
					result = false;
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		return result;
	}

	@Override
	public boolean registerPlayer(IPlayer player) throws RemoteException {
		for (IPlayer p : players)
			if (p.getNick().equals(player.getNick()))
				return false;

		players.add(player);
		System.out.println("Player registered. Players count: "
				+ players.size());
		return true;
	}

	public void unregisterPlayer(final IPlayer player) throws RemoteException {
		int index = -1;
		for (int i = 0; i < players.size(); i++)
			if (players.get(i).getNick().equals(player.getNick()))
				index = i;

		if (index < 0)
			return; // no such player

		players.remove(index);
		System.out.println("Player unregistered. Players count: "
				+ players.size());

		new Thread(new Runnable() {
			@Override
			public void run() {
				for (IPlayerUnregisteredListener l : unregListeners)
					l.playerUnregistered(player);
			}
		}).start();
	}

	@Override
	public Collection<IPlayer> getRegisteredPlayers() throws RemoteException {
		return Collections.unmodifiableCollection(players);
	}

	@Override
	public synchronized boolean startGameWith(IPlayer myself, IPlayer player)
			throws RemoteException {

		System.out.println("startGameWith(" + myself.getNick() + ", "
				+ player.getNick() + ")");
		int index = -1;
		for (int i = 0; i < players.size(); i++)
			if (players.get(i).getNick().equals(player.getNick()))
				index = i;

		if (index < 0)
			return false; // no such player

		unregisterPlayer(players.get(index));
		pairs.offer(new Pair(myself, player));
		return true;
	}

	public Pair getPair() throws RemoteException {
		return pairs.poll();
	}

	public IPlayer getPlayer(int i) {
		return players.get(i);
	}

	public int getPlayersCount() {
		return players.size();
	}

	public interface IPlayerUnregisteredListener {
		public void playerUnregistered(IPlayer player);
	}
}
