package messages;

import java.net.Socket;
import java.util.PriorityQueue;

import peer.Peer;

public class VictoryMessage extends Message {

	private static final long serialVersionUID = 4714778184603472137L;
	private static final int VICTORY_PRIORITY = 2;
	
	public VictoryMessage() {
		super(Type.VICTORY, VICTORY_PRIORITY);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleMessage(Socket sender, PriorityQueue<Packets> outQueue, Peer peer) {
		System.out.println(this.toString());
	}
	
	@Override
	public String toString(){
		return "This is a Victory message";
	}

}
