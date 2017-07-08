package messages;

import java.util.List;

import peer.Broadcast;
import peer.ConnectionData;
import singletons.OutQueue;
import singletons.Peer;
import threads.BombExplodingThread;

public class BombTossedMessage extends Message {

	private static final long serialVersionUID = -8610892414931963536L;
	private static final int BOMB_TOSSED_PRIORITY = 4;
	private String color;


	public BombTossedMessage(String color) {
		super(Type.BOMB_TOSSED, BOMB_TOSSED_PRIORITY);
		this.setColor(color);
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	/** print message and send back ack */
	@Override
	public boolean handleInMessage(ConnectionData clientConnection) {
		try {
			System.out.println("A bomb has been tossed in the " + this.color + " zone. You only got 5 seconds to escape!");
			System.out.println("You are currently in the " + Peer.INSTANCE.getCurrentPosition().getColorZone() + " zone.");
			new AckMessage().handleOutMessage(clientConnection);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/** tell everyone about the bomb and then start exploding bomb thread */
	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		try {
			/** if dead cannot toss any bomb */
			if (Peer.INSTANCE.isAlive()){
				OutQueue outQueue = OutQueue.INSTANCE;
				System.out.println("Handling message. Type: " + this);
				
				/** retrieve connections for the broadcast */
				List<ConnectionData> clientConnections = Peer.INSTANCE.getClientConnectionsList();
				System.out.println("I have " + clientConnections.size() + " connections open!");
				System.out.println("retrieved user sockets");
		
				
				/** broadcast message */
				if (clientConnections.size() != 0) /** check i'm not alone */
					new Broadcast(clientConnections, this).broadcastMessage();
				
				System.out.println("Broadcast done " + this);
				
				/** start bomb exploding thread */				
				new Thread(new BombExplodingThread(this.getColor())).start();
				
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
			System.err.println("Error with outgoing bomb tossed message");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public String toString(){
		return "This is a Bomb tossed message";
	}

}

