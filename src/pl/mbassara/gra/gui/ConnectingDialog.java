package pl.mbassara.gra.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.rmi.RemoteException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import pl.mbassara.gra.remotes.IPlayer;
import pl.mbassara.gra.remotes.IServer;

public class ConnectingDialog extends JDialog {

	private static final long serialVersionUID = -763243464527826826L;

	public ConnectingDialog(final MainWindow parent, final IServer server,
			final IPlayer player) {
		super(parent, "Please wait...", true);

		setLayout(new BorderLayout());
		setResizable(false);
		setLocationByPlatform(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		try {
			BufferedImage icon = ImageIO.read(getClass().getClassLoader()
					.getResourceAsStream("icon.png"));
			setIconImage(icon);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		add(new JLabel("    Waiting for other player to connect..."),
				BorderLayout.CENTER);

		JPanel southPane = new JPanel();
		add(southPane, BorderLayout.SOUTH);

		JProgressBar bar = new JProgressBar();
		bar.setIndeterminate(true);
		southPane.add(bar);

		JButton button = new JButton("Cancel");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					server.unregisterPlayer(player);
					parent.switchToConnectingMode();
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
				dispose();
			}
		});
		southPane.add(button);

		pack();
	}

}
