package messages;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import peer.Broadcast;
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

	

	public void handleOutMessage() {
		try {
			OutQueue outQueue = OutQueue.INSTANCE;
			System.out.println("Handling message. Type: " + this);
			List<Integer> userPorts = Peer.INSTANCE.extractPlayersPorts();
			System.out.println("retrieved user ports");
			this.setInput(true); /** becomes an in packet for the receiver */
			
			if (userPorts.size() != 0) /** check i'm not alone */
				new Broadcast(userPorts, this).broadcastMessage();
			
			System.out.println("Broadcast done");
			/** needs the position to set the new one */
			System.out.println("Next position: " + this.getRow() + " " + this.getCol());
			Peer.INSTANCE.setNewPosition(this.getRow(), this.getCol());
			System.out.println("Notifying stdin");
			synchronized (outQueue) {
				outQueue.notify();
			}
		} catch (Exception e){
			System.out.println("Error with outgoing position message");
		}
	}
	
	@Override
	public void handleInMessage(Socket sender) {
		try {
			/** check my position */
			Peer peer = Peer.INSTANCE;
			OutQueue outQueue = OutQueue.INSTANCE;
			if(peer.isAlive() && Arrays.equals(peer.getCurrentPosition().getPosition(), new int[]{this.getRow(),this.getCol()})){
				System.out.println("You have been killed!");
				peer.setAlive(false); // i'm dead
				/** send killed */
				System.out.println("sending killed message");
				ObjectOutputStream out = new ObjectOutputStream(sender.getOutputStream());
				out.writeObject(new KilledMessage(peer.getCurrentPlayer()));
				/** create dead message to put on the outQueue */
				System.out.println("crreating dead message");
				DeadMessage dm = new DeadMessage(peer.getCurrentPlayer());
				dm.setInput(false);
				Packets packet = new Packets(dm, null);
				synchronized (outQueue) {
					outQueue.add(packet);
				}
				System.out.println("Dead packet added to the outQueue");
			}
			else { /** just send ack */
				ObjectOutputStream out = new ObjectOutputStream(sender.getOutputStream());
				out.writeObject(new AckMessage());
			} 
			
		}
		catch (IOException e){
			System.out.println("Error with incoming position message");
		}
	}

	@Override
	public String toString() {
		return "This is a position message";
	}

}
