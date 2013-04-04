package pl.mbassara.gra.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import pl.mbassara.gra.remotes.IPlayer;

import com.sun.jna.Platform;

public class PlayersTableMouseAdapter extends MouseAdapter {

	private final PlayersTable table;
	private final int[] selectedRowIndex = new int[1];
	private ArrayList<IPlayersTableListener> listeners = new ArrayList<IPlayersTableListener>();

	public PlayersTableMouseAdapter(PlayersTable table) {
		this.table = table;
	}

	public void addPlayersTableListener(IPlayersTableListener l) {
		listeners.add(l);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		selectedRowIndex[0] = table.rowAtPoint(e.getPoint());
		if (selectedRowIndex[0] >= 0
				&& selectedRowIndex[0] < table.getRowCount()) {
			table.setRowSelectionInterval(selectedRowIndex[0],
					selectedRowIndex[0]);
		} else {
			table.clearSelection();

		}

		if (Platform.isLinux() && e.isPopupTrigger()
				&& e.getComponent() instanceof JTable) {
			createMenu().show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		selectedRowIndex[0] = table.rowAtPoint(e.getPoint());
		if (selectedRowIndex[0] >= 0
				&& selectedRowIndex[0] < table.getRowCount()) {
			table.setRowSelectionInterval(selectedRowIndex[0],
					selectedRowIndex[0]);
		} else {
			table.clearSelection();

		}

		if (e.getClickCount() == 2) {
			TableModel model = table.getModel();
			if (model instanceof PlayersTableModel)
				join(((PlayersTableModel) model)
						.getPlayerAt(selectedRowIndex[0]));
		} else if (!Platform.isLinux() && e.isPopupTrigger()
				&& e.getComponent() instanceof JTable) {
			createMenu().show(e.getComponent(), e.getX(), e.getY());
		}
	}

	private JPopupMenu createMenu() {
		JPopupMenu menu = new JPopupMenu("menu");

		JMenuItem item = new JMenuItem("Join");
		// item.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
		item.addActionListener(joinListener);
		menu.add(item);

		return menu;
	}

	private void join(IPlayer player) {
		for (IPlayersTableListener l : listeners)
			l.playerClicked(player);
	}

	private ActionListener joinListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			TableModel model = table.getModel();
			if (model instanceof PlayersTableModel)
				join(((PlayersTableModel) model)
						.getPlayerAt(selectedRowIndex[0]));
		}
	};

	public interface IPlayersTableListener {
		public void playerClicked(IPlayer player);
	}
}
