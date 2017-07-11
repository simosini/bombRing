package messages;

import java.io.IOException;

import peer.Cell;
import peer.ConnectionData;
import singletons.PositionList;

public class AckPosition extends Message {

	private static final long serialVersionUID = 5910422326757924525L;
	private static final int ACK_POS_PRIORITY = 5;
	private Cell position;

	public AckPosition(Cell pos) {
		super(Type.ACK, ACK_POS_PRIORITY);
		this.setPosition(pos);
	}

	private void setPosition(Cell pos) {
		this.position = pos;
		
	}
	
	public Cell getPosition(){
		return new Cell(this.position);
		
	}
	
	/** received by broadcast thread */
	@Override
	public boolean handleInMessage(ConnectionData clientConnection) {
		try {
			/** add position to the queue */
			PositionList.getInstance().addCell(this.getPosition());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		try {
			/** send message to the player containing position */
			clientConnection.getOutputStream().writeObject(this);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
