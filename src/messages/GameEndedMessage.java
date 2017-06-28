package messages;

import java.net.Socket;
import java.util.PriorityQueue;

import peer.Peer;

public class GameEndedMessage extends Message {

	private static final long serialVersionUID = 6647186110663744043L;
	private static final int END_PRIORITY = 1;  // max priority 

	public GameEndedMessage() {
		super(Type.GAME_ENDED, END_PRIORITY);
		
	}

	@Override
	public void handleMessage(Socket sender, PriorityQueue<Packets> outQueue, Peer peer) {
		System.out.println(this.toString());

	}
	
	@Override
	public String toString(){
		return "This is the End message";
	}

}
