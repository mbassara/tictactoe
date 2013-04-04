package pl.mbassara.gra.model;

public enum FieldState {
	X, O, Empty;

	@Override
	public String toString() {
		switch (this) {
		case X:
			return "X";
		case O:
			return "O";
		default:
			return ".";
		}
	};
}
