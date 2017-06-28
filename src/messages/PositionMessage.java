package messages;

import java.net.Socket;
import java.util.List;
import java.util.PriorityQueue;

import peer.Broadcast;
import peer.Peer;

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
	
	@Override
	public void handleMessage(Socket sender, PriorityQueue<Packets> outQueue, Peer peer) {
		
		try {
			if (this.checkIsInput()){ /** it's an input packet */
				/** does nothing for now */
			}
			else { /** it's an output position packet */
				System.out.println("Handling message. Type: " + this);
				List<Integer> userPorts = peer.extractPlayersPorts();
				System.out.println("retrieved user ports");
				this.setInput(true); /** becomes an in packet for the receiver */
				new Broadcast(userPorts, this).broadcastMessage();
				System.out.println("Broadcast done");
				/** needs the position to set the new one */
				System.out.println("Next position: " + this.getRow() + " " + this.getCol());
				peer.setNewPosition(this.getRow(), this.getCol());
				System.out.println("Notifying stdin");
				synchronized (outQueue) {
					outQueue.notify();
				}
				
			}
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	

	@Override
	public String toString(){
		return "This is a position message";
	}

}
