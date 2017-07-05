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
			/** if dead cannot move */
			if (Peer.INSTANCE.isAlive()){
				OutQueue outQueue = OutQueue.INSTANCE;
				System.out.println("Handling message. Type: " + this);
				
				/** retrieve connections for the broadcast */
				List<ConnectionData> clientConnections = Peer.INSTANCE.getClientConnectionsList();
				System.out.println("retrieved user sockets");
				this.setInput(true); /** becomes an in packet for the receiver */
				
				/** broadcast message */
				if (clientConnections.size() != 0) /** check i'm not alone */
					new Broadcast(clientConnections, this).broadcastMessage();
				
				System.out.println("Broadcast done");
				
				/** needs the position to set the new one */
				System.out.println("Next position: " + this.getRow() + " " + this.getCol());
				
				/** set new position */
				Peer.INSTANCE.setNewPosition(this.getRow(), this.getCol());
				
				/** notify the stdin i've finished handling the message */			
				if (Peer.INSTANCE.getNumberOfPlayers() > 1){
					System.out.println("Notifying stdin");
					synchronized (outQueue) {
						outQueue.notify();
					}
				}
				System.out.println("Done notification");
			}
		} catch (Exception e){
			System.err.println("Error with outgoing position message");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/** check if i'm still alive */
	@Override
	public boolean handleInMessage(ConnectionData cd) {
		try {
			Peer peer = Peer.INSTANCE;
			OutQueue outQueue = OutQueue.INSTANCE;
			
			/** check my position */			
			if(peer.isAlive() && Arrays.equals(peer.getCurrentPosition().getPosition(), new int[]{this.getRow(),this.getCol()})){
				
				/** build killed message and call handler */
				Player myself = peer.getCurrentPlayer();
				KilledMessage km = new KilledMessage(myself);
				km.handleOutMessage(cd);
				
				/** create dead message */
				System.out.println("creating dead message");
				DeadMessage dm = new DeadMessage(myself);
				dm.setInput(false);
				Packets packet = new Packets(dm, null);
				
				/** put message on the outQueue */
				synchronized (outQueue) {
					outQueue.add(packet);
				}
				System.out.println("Dead packet added to the outQueue");
			}
			else { /** just send ack */
				new AckMessage().handleOutMessage(cd);
				
			} 
			
		}
		catch (Exception e){
			System.err.println("Error with incoming position message");
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "This is a position message";
	}

}
