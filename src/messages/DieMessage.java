package messages;

import java.io.Serializable;

public class DieMessage extends Message implements Serializable {
	
	private static final long serialVersionUID = -4222784161899895527L;
	private static final int DIE_PRIORITY = 4;
	private String playerName;

	public DieMessage(String playerName) {
		super(Type.DIE, DIE_PRIORITY);
		this.setPlayerName(playerName);
		
	}
	
	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	@Override
	public void handleMessage() {
		System.out.println(this.toString());

	}

	@Override
	public String toString() {
		return "This is a Die message";

	}

}
