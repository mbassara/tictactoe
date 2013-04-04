package pl.mbassara.gra.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.concurrent.Semaphore;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import pl.mbassara.gra.Server;
import pl.mbassara.gra.gui.PlayersTableMouseAdapter.IPlayersTableListener;
import pl.mbassara.gra.model.Board;
import pl.mbassara.gra.model.Field;
import pl.mbassara.gra.model.Field.IFieldStateListener;
import pl.mbassara.gra.model.FieldState;
import pl.mbassara.gra.model.Shot;
import pl.mbassara.gra.model.gui.BoardView;
import pl.mbassara.gra.remotes.IPlayer;
import pl.mbassara.gra.remotes.IServer;
import pl.mbassara.gra.remotes.PlayerImpl;
import pl.mbassara.gra.remotes.PlayerImpl.IGameEndedListener;
import pl.mbassara.gra.remotes.PlayerImpl.IShotReceivedListener;
import pl.mbassara.gra.remotes.PlayerImpl.IStartGameListener;

public class MainWindow extends JFrame implements ActionListener,
		IShotReceivedListener, IStartGameListener, IGameEndedListener,
		IFieldStateListener, IPlayersTableListener {

	private static final long serialVersionUID = 3334230516512096768L;
	private FieldState playerType;
	private JTextField ipTextField;
	private JTextField nickTextField;
	private JTextField hostnameTextField;
	private JScrollPane centerPane;
	private JButton connectButton;
	private JButton registerButton;
	private ConnectingDialog connectingDialog = null;

	private IServer server = null;
	private PlayerImpl player = null;

	private BoardView boardView = null;

	public MainWindow() {

		setLayout(new BorderLayout(10, 10));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setPreferredSize(new Dimension(340, 450));
		addWindowListener(new WindowClosingHandler());
		setTitle("Tic Tac Toe");

		try {
			BufferedImage icon = ImageIO.read(getClass().getClassLoader()
					.getResourceAsStream("icon.png"));
			setIconImage(icon);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		JPanel northPane = new JPanel(new BorderLayout(2, 2));
		northPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		add(northPane, BorderLayout.NORTH);

		northPane.add(new JLabel("Server addr: "), BorderLayout.WEST);
		String addr = "192.168.1.135:4321";
		addr = "62.212.76.142:4321";
		ipTextField = new JTextField(addr);
		northPane.add(ipTextField, BorderLayout.CENTER);

		connectButton = new JButton("Connect");
		connectButton.addActionListener(this);
		northPane.add(connectButton, BorderLayout.EAST);

		registerButton = new JButton("Create new game");
		registerButton.setVisible(false);
		registerButton.addActionListener(this);
		northPane.add(registerButton, BorderLayout.SOUTH);

		JPanel northNorthPane = new JPanel();
		northPane.add(northNorthPane, BorderLayout.NORTH);

		northNorthPane.add(new JLabel("Nick: "));
		nickTextField = new JTextField("nick");
		nickTextField.setPreferredSize(new Dimension(75, nickTextField
				.getPreferredSize().height));
		northNorthPane.add(nickTextField);

		northNorthPane.add(new JLabel("Your IP: "));
		String ip = "";
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		// ip = "195.234.21.203";
		hostnameTextField = new JTextField(ip);
		hostnameTextField.setPreferredSize(new Dimension(145, hostnameTextField
				.getPreferredSize().height));
		northNorthPane.add(hostnameTextField);

		centerPane = new JScrollPane();
		add(centerPane, BorderLayout.CENTER);

		switchToConnectingMode();

		pack();
		setVisible(true);
	}

	public void switchToConnectingMode() {
		PlayerImpl.disconnect(server, player);
		boardView = null;
		server = null;
		player = null;
		centerPane.setViewportView(null);
		registerButton.setVisible(false);
		myTurnSemaphore.tryAcquire();
	}

	public void switchToGameMode(Board board) {

		boardView = new BoardView(board, 300);
		centerPane.setViewportView(boardView);
		registerButton.setVisible(false);
		pack();
	}

	public void switchToPlayersListMode(JTable playersTable) {
		boardView = null;

		registerButton.setVisible(true);
		centerPane.setViewportView(playersTable);
		pack();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(connectButton)) {

			if (!ipTextField.getText().matches(".*:[0-9]+")) {
				JOptionPane.showMessageDialog(this, "Wrong address format!",
						"Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			String ip = ipTextField.getText().substring(0,
					ipTextField.getText().lastIndexOf(":"));
			String port = ipTextField.getText().substring(
					ipTextField.getText().lastIndexOf(":") + 1);

			try {
				server = (IServer) Naming.lookup("rmi://" + ip + ":" + port
						+ "/game");

				if (!server.isNickAvailable(nickTextField.getText())) {
					JOptionPane.showMessageDialog(this,
							"This nick is already taken!", "Wrong nick",
							JOptionPane.ERROR_MESSAGE);
					return;
				} else if (nickTextField.getText().isEmpty()) {
					JOptionPane.showMessageDialog(this,
							"You haven't specified your nickname!",
							"Wrong nick", JOptionPane.ERROR_MESSAGE);
					return;
				} else if (hostnameTextField.getText().isEmpty()) {
					JOptionPane.showMessageDialog(this,
							"You haven't specified your IP adress!",
							"Wrong IP", JOptionPane.ERROR_MESSAGE);
					return;
				}

				System.setProperty("java.rmi.server.hostname",
						hostnameTextField.getText());

				PlayersTable table = new PlayersTable();
				table.addPlayersTableListener(this);
				for (IPlayer p : server.getRegisteredPlayers())
					table.addPlayer(p);

				switchToPlayersListMode(table);

			} catch (Exception e1) {
				JOptionPane
						.showMessageDialog(
								this,
								"Cannot connect to server! Check if the IP address is correct.",
								"Error", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		} else if (e.getSource().equals(registerButton)) {
			if (server == null) {
				JOptionPane.showMessageDialog(this, "Connect first!", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			try {
				player = new PlayerImpl(nickTextField.getText());
				player.addShotReceivedListener(this);
				player.addStartGameListener(this);
				player.addGameEndedListener(this);
				playerType = FieldState.O;
				if (!server.registerPlayer(player)) {
					JOptionPane.showMessageDialog(this,
							"This nick is already taken!", "Wrong nick",
							JOptionPane.ERROR_MESSAGE);
					switchToConnectingMode();
					return;
				} else if (nickTextField.getText().isEmpty()) {
					JOptionPane.showMessageDialog(this,
							"You haven't specified your nickname!",
							"Wrong nick", JOptionPane.ERROR_MESSAGE);
					switchToConnectingMode();
					return;
				}
				Board board = new Board(3);
				board.addFieldStateListener(this);

				switchToGameMode(board);

				boardView.setPlayerType(playerType);

				connectingDialog = new ConnectingDialog(this, server, player);
				connectingDialog.setVisible(true);

			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void playerClicked(IPlayer clickedPlayer) {

		if (server == null) {
			JOptionPane.showMessageDialog(this, "Connect first!", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			player = new PlayerImpl(nickTextField.getText());
			player.addShotReceivedListener(this);
			player.addGameEndedListener(this);
			playerType = FieldState.X;
			Board board = new Board(3);
			board.addFieldStateListener(this);

			switchToGameMode(board);

			boardView.setPlayerType(playerType);
			boardView.setEnabled(false);

			server.startGameWith(player, clickedPlayer);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	private Semaphore myTurnSemaphore = new Semaphore(0);

	@Override
	public void startGame() {
		boardView.setEnabled(true);
		myTurnSemaphore.release();
		if (connectingDialog != null)
			connectingDialog.dispose();
	}

	@Override
	public void shotReceived(Shot shot) {
		boardView.shoot(shot);
		boardView.setEnabled(true);
		myTurnSemaphore.release();
	}

	@Override
	public void gameEnded(IPlayer winner) {
		try {
			String msg = "Game ended. ";
			if (winner == null)
				msg = "There is a draw.";
			else if (winner.getNick().equals(player.getNick()))
				msg = "You've won!";
			else
				msg = "You've lost.";
			JOptionPane.showMessageDialog(this, msg, "Game ended",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		switchToConnectingMode();
	}

	@Override
	public void onFieldStateChange(Field source) {
		if (myTurnSemaphore.tryAcquire()) {
			boardView.setEnabled(false);
			player.pushShot(new Shot(playerType, source.getPosition()));
		}
	}

	public static void main(String[] args) {
		// BotPlayer bot = new BotPlayer(3);
		// bot.startGame();
		// Shot shot = bot.getShot();
		// System.out.println("type: " + shot.getType() + "\tpoint: "
		// + shot.getCoord());
		System.setProperty("java.rmi.server.codebase", "file:"
				+ Server.class.getProtectionDomain().getCodeSource()
						.getLocation().getPath());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setLookAndFeel();
				// new MainWindow();
				new MainWindow();
			}
		});
	}

	private static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	class WindowClosingHandler extends WindowAdapter {

		public void windowClosing(WindowEvent evt) {
			PlayerImpl.disconnect(server, player);
		}
	}
}
