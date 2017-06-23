package messages;

import java.io.Serializable;

import restConverter.Player;

public class JoinRingMessage extends Message implements Serializable {

	private static final long serialVersionUID = -7568751517974737661L;
	private static final int JOIN_PRIORITY = 3;
	private Player player; /** the player to add to the ring */

	public JoinRingMessage(Player p) {
		super(Type.JOINRING, JOIN_PRIORITY);
		this.setPlayer(p);
	}
	
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	@Override
	public void handleMessage() {
		System.out.println(this.toString());

	}
	
	@Override
	public String toString(){
		return "This is a Join message";
	}

}
