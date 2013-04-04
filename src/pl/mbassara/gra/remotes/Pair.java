package pl.mbassara.gra.remotes;

import java.io.Serializable;

public class Pair implements Serializable {
	private static final long serialVersionUID = -2119256562682151000L;
	private IPlayer xPlayer;
	private IPlayer oPlayer;

	public Pair(IPlayer xPlayer, IPlayer oPlayer) {
		this.xPlayer = xPlayer;
		this.oPlayer = oPlayer;
	}

	public IPlayer getXPlayer() {
		return xPlayer;
	}

	public IPlayer getOPlayer() {
		return oPlayer;
	}
}
