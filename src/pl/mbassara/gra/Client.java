package pl.mbassara.gra;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import pl.mbassara.gra.model.Board;
import pl.mbassara.gra.model.FieldState;
import pl.mbassara.gra.model.Shot;
import pl.mbassara.gra.remotes.IPlayer;
import pl.mbassara.gra.remotes.IServer;
import pl.mbassara.gra.remotes.PlayerImpl;
import pl.mbassara.gra.remotes.PlayerImpl.IGameEndedListener;
import pl.mbassara.gra.remotes.PlayerImpl.IShotReceivedListener;
import pl.mbassara.gra.remotes.PlayerImpl.IStartGameListener;

public class Client {

	private final BufferedReader in = new BufferedReader(new InputStreamReader(
			System.in));
	private IServer server = null;
	private PlayerImpl player = null;
	private FieldState playerType;
	private final Semaphore semaphore = new Semaphore(0);
	private boolean isGameEnded = false;
	private IPlayer gameWinner = null;
	private int boardSize = 3;

	private boolean connect(String nick, String ip, String port) {
		try {
			server = (IServer) Naming.lookup("rmi://" + ip + ":" + port
					+ "/game");
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (server == null) {
			System.out.println("\nCannot connect to " + ip + ":" + port + "\n");
			return false;
		}

		System.out.println("\nConnected to " + ip + ":" + port + "\n");
		return true;
	}

	private boolean initGame(String nick) {
		try {
			if (!server.isNickAvailable(nick)) {
				System.out.println("This nick is already taken!");
				return false;
			}

			player = new PlayerImpl(nick);
			ArrayList<IPlayer> registeredPlayers = new ArrayList<IPlayer>(
					server.getRegisteredPlayers());

			int answer = -1;
			do {
				System.out
						.print("\nThere are "
								+ registeredPlayers.size()
								+ " registered players. Type:\n\t0 to register\n\t1 to choose one of registered players to play.\nanswer: ");

				try {
					answer = Integer.parseInt(in.readLine());
				} catch (NumberFormatException e) {
					answer = -1;
				}
			} while (answer != 0 && answer != 1);

			boolean meFirst;
			if (answer == 0) {
				if (!server.registerPlayer(player)) {
					System.out.println("This nick is already taken!");
					return false;
				}
				meFirst = true;
			} else {
				System.out.println("\nRegistered players:\n");
				for (int i = 0; i < registeredPlayers.size(); i++)
					System.out.println(i + ". "
							+ registeredPlayers.get(i).getNick());

				do {
					System.out
							.print("\nType number of player you want to play: ");
					try {
						answer = Integer.parseInt(in.readLine());
					} catch (NumberFormatException e) {
						answer = -1;
					}
				} while (answer < 0 || answer >= registeredPlayers.size());

				if (!server
						.startGameWith(player, registeredPlayers.get(answer))) {
					System.out.println("Theres no such player...");
					return false;
				}
				meFirst = false;
			}

			playerType = meFirst ? FieldState.O : FieldState.X;
			return true;

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	private void game() {
		try {

			int x, y;
			final Board board = new Board(boardSize);

			player.addShotReceivedListener(new IShotReceivedListener() {
				@Override
				public void shotReceived(Shot shot) {
					board.shoot(shot);
					System.out.println("\n" + board + "\n");
					semaphore.release();
				}
			});

			player.addStartGameListener(new IStartGameListener() {
				@Override
				public void startGame() {
					semaphore.release();
				}
			});

			player.addGameEndedListener(new IGameEndedListener() {
				@Override
				public void gameEnded(IPlayer winner) {
					gameWinner = winner;
					isGameEnded = true;
					semaphore.release();
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});

			System.out.println("\nWaiting for other player to connect...\n");

			semaphore.acquire();

			System.out.println("\nLet's start!!!\n");
			do {
				x = -1;
				y = -1;
				try {
					while (x < 0 || x >= boardSize) {
						System.out.print("Enter X: ");
						try {
							x = Integer.parseInt(in.readLine());
						} catch (NumberFormatException e) {
							x = -1;
						}
					}
					while (y < 0 || y >= boardSize) {
						System.out.print("Enter Y: ");
						try {
							y = Integer.parseInt(in.readLine());
						} catch (NumberFormatException e) {
							y = -1;
						}
					}
				} catch (IOException e) {
					break;
				}

				if (board.getField(x, y).getState() != FieldState.Empty) {
					System.out.println("\nThis field has been already shot\n");
					continue;
				}

				Shot shot = new Shot(playerType, new Point(x, y));
				player.pushShot(shot);
				board.shoot(shot);
				System.out.println("\n" + board + "\n");

				if (isGameEnded)
					break;

				semaphore.acquire();
			} while (!isGameEnded);

			String result = "Game ended! ";
			if (gameWinner == null)
				result += "There is a draw...";
			else
				result += (gameWinner.getNick().equals(player.getNick()) ? "You"
						: "Your opponent")
						+ " won.";
			System.out.println(result);

			server.unregisterPlayer(player);
			UnicastRemoteObject.unexportObject(player, true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		if (args.length < 4) {
			System.out
					.println("\nUsage: java Client nick reg.ip reg.port own_hostname\n");
			return;
		}
		System.setProperty("java.rmi.server.codebase", "file:"
				+ Client.class.getProtectionDomain().getCodeSource()
						.getLocation().getPath());
		System.setProperty("java.rmi.server.hostname", args[3]);

		Client client = new Client();

		if (!client.connect(args[0], args[1], args[2]))
			return;

		if (!client.initGame(args[0]))
			return;

		client.game();
	}
}
