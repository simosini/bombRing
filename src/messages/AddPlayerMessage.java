package messages;

/** 
 * This message is sent to update the map of players.
 * Basically the player who receives a message to join the game,
 * before accepting his request, must make sure that every other player 
 * have updated his players map. */

import java.io.Serializable;

import restConverter.Player;

public class AddPlayerMessage extends InMessage implements Serializable {

	private static final long serialVersionUID = 4448703639215851985L;
	private static final int ADD_PRIORITY = 4;
	private Player playerToAdd;

	public AddPlayerMessage(Player p) {
		super(Type.ADDPLAYER, ADD_PRIORITY);
		this.setPlayerToAdd(p);
	}

	public Player getPlayerToAdd() {
		return playerToAdd;
	}

	public void setPlayerToAdd(Player playerToAdd) {
		this.playerToAdd = playerToAdd;
	}
	
	@Override
	public void handleMessage() {
		System.out.println(this.toString());

	}
	
	@Override
	public String toString(){
		return "This is an Addplayer message";
	}

}
