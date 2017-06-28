package messages;

import java.net.Socket;
import java.util.PriorityQueue;

import peer.Peer;

public class TokenMessage extends Message {
	
	private static final long serialVersionUID = -2487582210405295762L;
	private static final int TOKEN_PRIORITY = 5;
	
	public TokenMessage() {
		super(Type.TOKEN, TOKEN_PRIORITY);
	}
	
	@Override
	public void handleMessage(Socket sender, PriorityQueue<Packets> outQueue, Peer peer){
		System.out.println(this.toString());
	}
	
	@Override
	public String toString(){
		return "This is a Token message";
	}

}
