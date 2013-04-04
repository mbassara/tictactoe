package pl.mbassara.gra.remotes;

import java.rmi.Remote;
import java.rmi.RemoteException;

import pl.mbassara.gra.model.Shot;

public interface IPlayer extends Remote {

	public String getNick() throws RemoteException;

	public void startGame() throws RemoteException;

	public Shot getShot() throws RemoteException;

	public void receiveShot(Shot shot) throws RemoteException;

	public void receiveGameResult(IPlayer winner) throws RemoteException;
}
