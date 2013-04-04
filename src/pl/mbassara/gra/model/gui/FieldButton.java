package pl.mbassara.gra.model.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

import org.imgscalr.Scalr;

import pl.mbassara.gra.model.Field;
import pl.mbassara.gra.model.Field.IFieldStateListener;
import pl.mbassara.gra.model.FieldState;

public class FieldButton extends JToggleButton implements IFieldStateListener {

	private static final long serialVersionUID = -556101662847217916L;
	private Point position;
	private Field model;
	private FieldState playerType;
	private boolean choosable = true;

	public FieldButton(int width, Field model) {
		super();

		setSize(new Dimension(width, width));
		setContentAreaFilled(false);
		setBorderPainted(false);
		setFocusPainted(false);

		this.position = model.getPosition();
		this.model = model;
		this.model.addFieldStateListener(this);
		addActionListener(model);

		// setLocation(position.y * width, position.x * width);
		try {
			BufferedImage image = ImageIO.read(getClass().getClassLoader()
					.getResourceAsStream("img/active.png"));

			setIcon(new ImageIcon(Scalr.resize(image, getSize().width)));

			image = ImageIO.read(getClass().getClassLoader()
					.getResourceAsStream("img/back.png"));

			setDisabledIcon(new ImageIcon(Scalr.resize(image, getSize().width)));

		} catch (IOException e) {
			e.printStackTrace();
		}
		setEnabled(false);
	}

	public Point getPosition() {
		return position;
	}

	public void setPlayerType(FieldState playerType) {
		this.playerType = playerType;
		// try {
		// BufferedImage image = ImageIO.read(getClass().getClassLoader()
		// .getResourceAsStream(
		// (playerType == FieldState.X ? "x" : "o") + ".png"));
		//
		// Icon icon = new ImageIcon(Scalr.resize(image, getSize().width));
		// setSelectedIcon(icon);
		// setDisabledSelectedIcon(icon);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}

	@Override
	public void setEnabled(boolean b) {
		super.setEnabled(b && choosable);
	}

	public FieldState getPlayerType() {
		return playerType;
	}

	@Override
	public void onFieldStateChange(Field source) {
		String fileName = "img/";
		switch (source.getState()) {
		case O:
			fileName += "o.png";
			break;
		case X:
			fileName += "x.png";
			break;
		default:
			fileName += "back.png";
			break;
		}

		try {
			BufferedImage image = ImageIO.read(getClass().getClassLoader()
					.getResourceAsStream(fileName));

			Icon icon = new ImageIcon(Scalr.resize(image, getSize().width));
			setIcon(icon);
			setDisabledIcon(icon);
		} catch (IOException e) {
			e.printStackTrace();
		}

		choosable = false;
		setEnabled(false);
	}
}
