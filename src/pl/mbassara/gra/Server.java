package pl.mbassara.gra;

import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;

import pl.mbassara.gra.remotes.BotPlayer;
import pl.mbassara.gra.remotes.IServer;
import pl.mbassara.gra.remotes.Pair;
import pl.mbassara.gra.remotes.ServerImpl;

public class Server {

	public static void main(String[] args) {
		if (args.length < 4) {
			System.out
					.println("\nUsage: java Server hostname reg.ip reg.port boardsize\n");
			return;
		}

		try {
			// LocateRegistry.createRegistry(Integer.valueOf(args[2]));
			System.setProperty("java.rmi.server.codebase", "file:"
					+ Server.class.getProtectionDomain().getCodeSource()
							.getLocation().getPath());
			System.setProperty("java.rmi.server.hostname", args[0]);
			ServerImpl server = new ServerImpl();
			IServer remServer = (IServer) UnicastRemoteObject.exportObject(
					server, 0);
			Naming.rebind("rmi://" + args[1] + ":" + args[2] + "/game",
					remServer);

			int boardSize = Integer.parseInt(args[3]);
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
