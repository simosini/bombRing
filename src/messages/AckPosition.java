package messages;

import java.io.IOException;

import peer.Cell;
import peer.ConnectionData;
import singletons.PositionList;
 
/** 
 * This message is sent by a peer to respond to a AddPlayer request.
 * Basically the sender put its current position on the message.
 * The receiver, instead, use this info to compute a free position for the 
 * new player willing to enter the game.   
 */
public class AckPosition extends Message {

	private static final long serialVersionUID = 5910422326757924525L;
	private static final int ACK_POS_PRIORITY = 5;
	private Cell position;

	public AckPosition(Cell pos) {
		super(Type.ACK, ACK_POS_PRIORITY);
		this.setPosition(pos);
	}
	
	/** 
	 * set the position. Private cause can only be called by the constructor 
	 */
	private void setPosition(Cell pos) {
		this.position = pos;
		
	}
	
	/** 
	 * returns a copy of the position saved in the message 
	 */
	public Cell getPosition(){
		return new Cell(this.position);
		
	}
	
	/** 
	 * Received by the broadcast thread, it puts it in the PositionList 
	 */
	@Override
	public boolean handleInMessage(ConnectionData clientConnection) {
		try {
			// add position to PositionList 
			PositionList.getInstance().addCell(this.getPosition());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/** 
	 * send out your position to the requested client 
	 */
	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		try {
			// send message to the player containing position 
			clientConnection.getOutputStream().writeObject(this);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
