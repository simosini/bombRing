package messages;

import java.util.Arrays;
import java.util.List;

import beans.Player;
import peer.Broadcast;
import peer.ConnectionData;
import singletons.OutQueue;
import singletons.Peer;

/**
 * With message is created whenever the user decides to move from its
 * current position. 
 */
public class PositionMessage extends Message {

	private static final long serialVersionUID = 6887926961459159988L;
	private static final int POSITION_PRIORITY = 4;
	private int row;
	private int col;
	

	public PositionMessage(int row, int col) {
		super(Type.POSITION, POSITION_PRIORITY);
		this.setRow(row);
		this.setCol(col);

	}
	
	/**
	 * setters and getters
	 */
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	
	/** 
	 * send peer's new position in broadcast to all other players 
	 */
	@Override
	public boolean handleOutMessage(ConnectionData cd) {
		try {
			final Peer peer = Peer.getInstance();
			
			// if dead I cannot move 
			if (peer.isAlive()){
				final OutQueue outQueue = OutQueue.getInstance();
				
				// retrieve connections for the broadcast 
				final List<ConnectionData> clientConnections = peer.getClientConnectionsList();
								
				// broadcast message if not alone
				if (clientConnections.size() != 0) 
					new Broadcast(clientConnections, this).broadcastMessage();
				
				// set new position 
				peer.setNewPosition(this.getRow(), this.getCol());
				
				// notify the standard in i've finished handling the message			
				if (peer.getNumberOfPlayers() > 1){
					synchronized (outQueue) {
						outQueue.notify();
					}
				}
				
			}
		} catch (Exception e){
			System.err.println("Error with outgoing position message");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * When this message is received the peer checks its position
	 * in order to make sure it has not been eaten. If that happens
	 * the peer sends a killed message to the sender and puts a
	 * Dead message on the outQueue to be handled with the token,
	 * otherwise it just sends an ack.
	 */
	@Override
	public boolean handleInMessage(ConnectionData cd) {
		try {
			final Peer peer = Peer.getInstance();
			final OutQueue outQueue = OutQueue.getInstance();
			final int[] otherPeerPosition = peer.getCurrentPosition().getPosition();
			final int[] myPosition = new int[]{this.getRow(),this.getCol()};
			
			// check position if alive 			
			if(peer.isAlive() && Arrays.equals(otherPeerPosition, myPosition)){
				
				// build killed message and call handler 
				final Player myself = peer.getCurrentPlayer();
				final KilledMessage km = new KilledMessage(myself);
				km.handleOutMessage(cd);
				
				// create dead message
				final Packets packet = new Packets(new DeadMessage(myself), null);
				
				// put message on the outQueue 
				outQueue.add(packet);
				
			}	
			 // otherwise just send ack
			else { 
				new AckMessage().handleOutMessage(cd);
				
			} 
			
		}
		catch (Exception e){
			System.err.println("Error with incoming position message");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "This is a position message";
	}

}
