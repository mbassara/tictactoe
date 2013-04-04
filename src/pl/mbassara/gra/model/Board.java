package pl.mbassara.gra.model;

import java.awt.Point;
import java.io.Serializable;

import pl.mbassara.gra.model.Field.IFieldStateListener;

public class Board implements Serializable {

	private static final long serialVersionUID = -5302693533492157406L;
	private Field[][] fields;

	public Board(int size) {
		fields = new Field[size][size];
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				fields[i][j] = new Field(new Point(i, j));
	}

	public Field getField(int x, int y) {
		return fields[x][y];
	}

	public void shoot(Shot shot) {
		if (shot.getCoord().x < 0 || shot.getCoord().x >= fields.length
				|| shot.getCoord().y < 0 || shot.getCoord().y >= fields.length)
			throw new IndexOutOfBoundsException(
					"Coordinates have to be in range: [0, " + fields.length
							+ "]. Your point is " + shot.getCoord().toString());

		fields[shot.getCoord().x][shot.getCoord().y].setState(shot.getType());
	}

	public void addFieldStateListener(IFieldStateListener listener) {
		for (int i = 0; i < fields.length; i++)
			for (int j = 0; j < fields.length; j++)
				fields[i][j].addFieldStateListener(listener);
	}

	/**
	 * Checks if game is ended and returns information about winner
	 * 
	 * @return FieldState.X or FieldState.O accordingly to the type of winner,
	 *         FieldState.Empty if there is a draw or null if game is not ended
	 *         yet.
	 */
	public FieldState getGameResult() {
		// check if there is a winner
		final int SIZE = fields.length;
		for (FieldState state : FieldState.values()) {
			if (state == FieldState.Empty)
				continue;

			boolean isWinner = false; // horizontal
			for (int i = 0; i < SIZE && !isWinner; i++) {
				isWinner = true;
				for (int j = 0; j < SIZE; j++)
					if (fields[i][j].getState() != state)
						isWinner = false;
			}

			if (isWinner)
				return state;

			// vertical
			for (int j = 0; j < SIZE && !isWinner; j++) {
				isWinner = true;
				for (int i = 0; i < SIZE; i++)
					if (fields[i][j].getState() != state)
						isWinner = false;
			}

			if (isWinner)
				return state;

			// descending diagonal
			isWinner = true;
			for (int i = 0; i < SIZE; i++)
				if (fields[i][i].getState() != state)
					isWinner = false;

			if (isWinner)
				return state;

			// ascending diagonal
			isWinner = true;
			for (int i = 0; i < SIZE; i++)
				if (fields[SIZE - i - 1][i].getState() != state)
					isWinner = false;

			if (isWinner)
				return state;
		}

		// check if there is a draw
		boolean isDraw = true;
		for (int i = 0; i < SIZE; i++)
			for (int j = 0; j < SIZE; j++)
				if (fields[i][j].getState() == FieldState.Empty)
					isDraw = false;

		if (isDraw)
			return FieldState.Empty;

		return null;
	}

	public int getSize() {
		return fields.length;
	}

	@Override
	public String toString() {
		String string = "   ";
		for (int i = 0; i < fields.length; i++)
			string += i + " ";
		for (int i = 0; i < fields.length; i++) {
			string += "\n" + i + "  ";
			for (int j = 0; j < fields.length; j++)
				string += fields[i][j] + " ";
		}

		return string;
	}
}
