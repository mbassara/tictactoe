package pl.mbassara.gra.gui;

import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import pl.mbassara.gra.remotes.IPlayer;

public class PlayersTableModel implements TableModel {

	private ArrayList<TableModelListener> listeners = new ArrayList<TableModelListener>();
	private ArrayList<IPlayer> players = new ArrayList<IPlayer>();

	@Override
	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return columnIndex == 0 ? Integer.class : String.class;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return columnIndex == 0 ? "index" : "Player";
	}

	@Override
	public int getRowCount() {
		return players.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		IPlayer p = players.get(rowIndex);
		String nick = "unknown";
		try {
			nick = p.getNick();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return columnIndex == 0 ? new Integer(rowIndex + 1) : nick;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		listeners.remove(l);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		return;
	}

	public void clear() {
		players.clear();
		for (TableModelListener listener : listeners)
			listener.tableChanged(new TableModelEvent(this));
	}

	public void addPlayer(IPlayer player) {
		players.add(player);
		for (TableModelListener listener : listeners)
			listener.tableChanged(new TableModelEvent(this));
	}

	public void removePlayer(IPlayer player) {
		players.remove(player);
		for (TableModelListener listener : listeners)
			listener.tableChanged(new TableModelEvent(this));
	}

	public IPlayer getPlayerAt(int index) {
		return players.get(index);
	}
}
