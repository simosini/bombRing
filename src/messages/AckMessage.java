package messages;

import peer.ConnectionData;

/** 
 * this message is sent by the peers to confirm reception of particular message.
 */
public class AckMessage extends Message {
	
	private static final long serialVersionUID = -2461861844306614558L;
	private static final int ACK_PRIORITY = 5;
	
	public AckMessage() {
		super(Type.ACK, ACK_PRIORITY);
	}
	
	/** 
	 * this message is never put on the outQueue so do nothing 
	 */
	@Override
	public boolean handleInMessage(ConnectionData cd){
		return true;
	}
	
	/** 
	 * send out ack 
	 */
	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		try {
			clientConnection.getOutputStream().writeObject(this);
		}
		catch(Exception e){
			System.err.println("Error sending out Ack");
			return false;
		}
		return true;
	}
	
	@Override
	public String toString(){
		return "This is an Ack message";
	}

}
