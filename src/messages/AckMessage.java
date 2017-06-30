package messages;

import java.net.Socket;

public class AckMessage extends Message {
	
	private static final long serialVersionUID = -2461861844306614558L;
	private static final int ACK_PRIORITY = 5;

	public AckMessage() {
		super(Type.ACK, ACK_PRIORITY);
	}
	
	@Override
	public void handleInMessage(Socket sender){
		/** this message is never put on the outQueue so do nothing */
		System.out.println("Ack message received");
	}
	
	@Override
	public String toString(){
		return "This is an Ack message";
	}

}
