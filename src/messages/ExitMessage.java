package messages;

import java.io.Serializable;

public class ExitMessage extends InMessage implements Serializable {
	
	private static final long serialVersionUID = -5807158110067786776L;
	private static final int EXIT_PRIORITY = 4;
	private String playerName;

	public ExitMessage(String playerName) {
		super(Type.EXITGAME, EXIT_PRIORITY);
		this.setPlayerName(playerName);
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;		
	}
	
	public String getPlayerName(){
		return this.playerName;
	}

	@Override
	public void handleMessage() {
		System.out.println(this.toString());

	}

	@Override
	public String toString(){
		return "This is an Exit message";
	}
}
