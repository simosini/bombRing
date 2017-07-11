package messages;

import java.util.Arrays;
import java.util.List;

import beans.Player;
import peer.Broadcast;
import peer.ConnectionData;
import singletons.OutQueue;
import singletons.Peer;

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

	
	/** send position in broadcast to all other players */
	@Override
	public boolean handleOutMessage(ConnectionData cd) {
		try {
			Peer peer = Peer.getInstance();
			/** if dead cannot move */
			if (peer.isAlive()){
				OutQueue outQueue = OutQueue.getInstance();
				//System.out.println("Handling message. Type: " + this);
				
				/** retrieve connections for the broadcast */
				List<ConnectionData> clientConnections = peer.getClientConnectionsList();
				//System.out.println("I have " + clientConnections.size() + " connections open!");
				//System.out.println("retrieved user sockets");
		
				
				/** broadcast message */
				if (clientConnections.size() != 0) /** check i'm not alone */
					new Broadcast(clientConnections, this).broadcastMessage();
				
				//System.out.println("Broadcast done " + this);
				
				/** needs the position to set the new one */
				//System.out.println("Next position: " + this.getRow() + " " + this.getCol());
				
				/** set new position */
				peer.setNewPosition(this.getRow(), this.getCol());
				
				/** notify the stdin i've finished handling the message */			
				if (peer.getNumberOfPlayers() > 1){
					//System.out.println("Notifying stdin");
					synchronized (outQueue) {
						outQueue.notify();
					}
				}
				
				//System.out.println("Done notification");
			}
		} catch (Exception e){
			System.err.println("Error with outgoing position message");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	@Override
	public boolean handleInMessage(ConnectionData cd) {
		try {
			Peer peer = Peer.getInstance();
			OutQueue outQueue = OutQueue.getInstance();
			
			/** check my position */			
			if(peer.isAlive() && Arrays.equals(peer.getCurrentPosition().getPosition(), new int[]{this.getRow(),this.getCol()})){
				
				/** build killed message and call handler */
				Player myself = peer.getCurrentPlayer();
				KilledMessage km = new KilledMessage(myself);
				km.handleOutMessage(cd);
				
				/** create dead message */
				//System.out.println("creating dead message");
				Packets packet = new Packets(new DeadMessage(myself), null);
				
				/** put message on the outQueue */
				outQueue.add(packet);
				
				//System.out.println("Dead packet added to the outQueue");
			}
			else { /** just send ack */
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
