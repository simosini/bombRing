package messages;

import java.net.Socket;
import java.util.PriorityQueue;

import peer.Peer;

public class AckMessage extends Message {
	
	private static final long serialVersionUID = -2461861844306614558L;
	private static final int ACK_PRIORITY = 5;

	public AckMessage() {
		super(Type.ACK, ACK_PRIORITY);
	}
	
	@Override
	public void handleMessage(Socket sender, PriorityQueue<Packets> outQueue, Peer peer){
		System.out.println(this.toString());
	}
	
	@Override
	public String toString(){
		return "This is an Ack message";
	}

}
