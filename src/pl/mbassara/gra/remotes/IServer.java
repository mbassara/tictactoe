package pl.mbassara.gra.remotes;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

public interface IServer extends Remote {
	public boolean isNickAvailable(String nick) throws RemoteException;

	public boolean registerPlayer(IPlayer player) throws RemoteException;

	public void unregisterPlayer(IPlayer player) throws RemoteException;

	public Collection<IPlayer> getRegisteredPlayers() throws RemoteException;

	public boolean startGameWith(IPlayer myself, IPlayer player)
			throws RemoteException;

}
