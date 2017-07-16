package messages;

import java.io.IOException;

import beans.Players;
import peer.ConnectionData;

/**
 * This message is sent to inform a player willing to join the game
 * that it should ask someone else cause the current player is either
 * dead or leaving the game
 */

public class NackMessage extends Message {

	private static final long serialVersionUID = -1036121250705826323L;
	private static final int NACK_PRIORITY = 5;
	private Players players; 
	

	public NackMessage(Players players) {
		super(Type.NACK, NACK_PRIORITY);
		this.setPlayers(players);

	}
	
	public Players getPlayers() {
		return players;
	}

	private void setPlayers(Players players) {
		this.players = players;
	}

	/** 
	 * This can only be received by a new player willing to join the game 
	 */
	@Override
	public boolean handleInMessage(ConnectionData clientConnection) {
		
		// this message is never put on any queue so do nothing 
		return true;
	}
	
	/**
	 * just send out the message
	 */
	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		try {
			clientConnection.getOutputStream().writeObject(this);
		} catch (IOException e){
			System.err.println("Error sending nack message");
			e.printStackTrace();
			return false;
		}
		return true;
		
	}
	
	@Override
	public String toString(){
		return "This is a Nack message";
	}

}
