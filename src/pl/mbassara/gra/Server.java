package pl.mbassara.gra;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import pl.mbassara.gra.remotes.BotPlayer;
import pl.mbassara.gra.remotes.IServer;
import pl.mbassara.gra.remotes.Pair;
import pl.mbassara.gra.remotes.ServerImpl;

public class Server {

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("\nUsage: Server hostname port\n");
			return;
		}

		try {
			LocateRegistry.createRegistry(Integer.valueOf(args[1]));
			System.setProperty("java.rmi.server.hostname", args[0]);
			ServerImpl server = new ServerImpl();
			IServer remServer = (IServer) UnicastRemoteObject.exportObject(
					server, 0);
			Naming.rebind("rmi://localhost:" + args[1] + "/game", remServer);
			int boardSize = 3;
			server.registerPlayer(new BotPlayer(boardSize));

			while (true) {
				Pair pair = server.getPair();
				if (pair != null) {
					if (pair.getOPlayer().getNick().equals("computer"))
						server.registerPlayer(new BotPlayer(boardSize));

					GameServerThread thread = new GameServerThread(
							pair.getOPlayer(), pair.getXPlayer(), boardSize);
					server.addPlayerUnregisteredListener(thread);
					thread.start();
				}

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
