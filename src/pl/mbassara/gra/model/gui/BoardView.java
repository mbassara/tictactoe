package pl.mbassara.gra.model.gui;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JComponent;

import pl.mbassara.gra.model.Board;
import pl.mbassara.gra.model.FieldState;
import pl.mbassara.gra.model.Shot;

public class BoardView extends JComponent {

	private static final long serialVersionUID = -1783959021492334579L;
	private FieldButton[][] fields;
	private Board model;

	public BoardView(Board model, int width) {
		setPreferredSize(new Dimension(width, width));
		setLayout(new GridLayout(model.getSize(), model.getSize()));

		this.model = model;
		this.fields = new FieldButton[model.getSize()][model.getSize()];
		for (int i = 0; i < fields.length; i++)
			for (int j = 0; j < fields.length; j++) {
				fields[i][j] = new FieldButton(width / fields.length,
						model.getField(i, j));
				add(fields[i][j]);
			}
	}

	public void shoot(Shot shot) {
		model.shoot(shot);
	}

	public void setPlayerType(FieldState playerType) {
		for (int i = 0; i < fields.length; i++)
			for (int j = 0; j < fields.length; j++)
				fields[i][j].setPlayerType(playerType);
	}

	@Override
	public void setEnabled(boolean enabled) {
		for (int i = 0; i < fields.length; i++)
			for (int j = 0; j < fields.length; j++)
				fields[i][j].setEnabled(enabled);
		super.setEnabled(enabled);
	}

}
