package messages;

import java.net.Socket;
import java.util.PriorityQueue;

public class GameEndedMessage extends Message {

	private static final long serialVersionUID = 6647186110663744043L;
	private static final int END_PRIORITY = 1;  // max priority 

	public GameEndedMessage() {
		super(Type.GAME_ENDED, END_PRIORITY);
		
	}

	@Override
	public void handleMessage(Socket sender, PriorityQueue<Packets> outQueue) {
		System.out.println(this.toString());

	}
	
	@Override
	public String toString(){
		return "This is the End message";
	}

}
