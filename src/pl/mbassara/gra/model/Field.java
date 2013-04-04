package pl.mbassara.gra.model;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;

import pl.mbassara.gra.model.gui.FieldButton;

public class Field implements Serializable, ActionListener {

	private static final long serialVersionUID = 4555095403814174434L;
	private FieldState state;
	private Point position;
	private ArrayList<IFieldStateListener> listeners;

	public Field(Point position) {
		state = FieldState.Empty;
		this.position = position;
		listeners = new ArrayList<Field.IFieldStateListener>();
	}

	public void addFieldStateListener(IFieldStateListener listener) {
		listeners.add(listener);
	}

	public Point getPosition() {
		return position;
	}

	public FieldState getState() {
		return state;
	}

	public void setState(FieldState state) {
		this.state = state;
		for (IFieldStateListener l : listeners)
			l.onFieldStateChange(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!(e.getSource() instanceof FieldButton))
			return;

		FieldButton source = (FieldButton) e.getSource();
		setState(source.getPlayerType());
	}

	@Override
	public String toString() {
		return state.toString();
	}

	public interface IFieldStateListener {
		public void onFieldStateChange(Field source);
	}

}
