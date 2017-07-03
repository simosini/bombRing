package messages;

import peer.ConnectionData;

public class AckMessage extends Message {
	
	private static final long serialVersionUID = -2461861844306614558L;
	private static final int ACK_PRIORITY = 5;
	
	public AckMessage() {
		super(Type.ACK, ACK_PRIORITY);
	}
	
	@Override
	public boolean handleInMessage(ConnectionData cd){
		/** this message is never put on the outQueue so do nothing */
		System.out.println("Ack message received");
		return true;
	}
	
	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		try {
			clientConnection.getOutputStream().writeObject(this);
			System.out.println("Ack sent");
		}
		catch(Exception e){
			System.out.println("Error sending out Ack");
			return false;
		}
		return true;
	}
	
	@Override
	public String toString(){
		return "This is an Ack message";
	}

}
