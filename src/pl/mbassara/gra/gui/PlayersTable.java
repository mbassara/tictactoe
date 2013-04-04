package pl.mbassara.gra.gui;

import javax.swing.JTable;

import pl.mbassara.gra.gui.PlayersTableMouseAdapter.IPlayersTableListener;
import pl.mbassara.gra.remotes.IPlayer;

public class PlayersTable extends JTable {

	private static final long serialVersionUID = -1241547265151905882L;
	private PlayersTableModel model = new PlayersTableModel();
	private PlayersTableMouseAdapter mouseAdapter = new PlayersTableMouseAdapter(
			this);

	public PlayersTable() {
		setModel(model);
		getColumn("index").setPreferredWidth(50);
		getColumn("Player").setPreferredWidth(300);

		addMouseListener(mouseAdapter);
	}

	public void addPlayer(IPlayer player) {
		model.addPlayer(player);
	}

	public void removePlayer(IPlayer player) {
		model.removePlayer(player);
	}

	public void clear() {
		model.clear();
	}

	public void addPlayersTableListener(IPlayersTableListener l) {
		mouseAdapter.addPlayersTableListener(l);
	}

}
