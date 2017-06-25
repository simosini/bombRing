package messages;

import java.net.Socket;
import java.util.PriorityQueue;

public class AckMessage extends Message {
	
	private static final long serialVersionUID = -2461861844306614558L;
	private static final int ACK_PRIORITY = 5;

	public AckMessage() {
		super(Type.ACK, ACK_PRIORITY);
	}
	
	@Override
	public void handleMessage(Socket sender, PriorityQueue<Packets> outQueue){
		System.out.println(this.toString());
	}
	
	@Override
	public String toString(){
		return "This is an Ack message";
	}

}
