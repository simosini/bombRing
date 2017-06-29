package messages;

import java.net.Socket;

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

	@Override
	public void handleMessage(Socket sender) {
		System.out.println(this.toString());

	}
	
	@Override
	public String toString(){
		return "This is a Nack message";
	}

}
