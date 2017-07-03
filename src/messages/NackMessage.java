package messages;

import java.io.IOException;

import peer.ConnectionData;

/**
 * This message is sent to inform a player willing to join the game
 * that should ask someone else cause the current player is either
 * dead or leaving the game
 */

public class NackMessage extends Message {

	private static final long serialVersionUID = -1036121250705826323L;
	private static final int NACK_PRIORITY = 5;
	

	public NackMessage() {
		super(Type.NACK, NACK_PRIORITY);

	}
	
	/** This can only be received by a new player willing to join the game */
	@Override
	public boolean handleInMessage(ConnectionData clientConnection) {
		/** this message is never put on any queue so do nothing */
		System.out.println("Nack message received");
		return true;
	}

	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		try {
			clientConnection.getOutputStream().writeObject(this);
		} catch (IOException e){
			System.err.println("Error sending nack message");
			return false;
		}
		return true;
		
	}
	
	@Override
	public String toString(){
		return "This is a Nack message";
	}

}
