package messages;

import java.util.List;

import beans.Player;
import peer.Broadcast;
import peer.ConnectionData;
import services.ExitProcedure;
import singletons.OutQueue;
import singletons.Peer;

/** tell everyone that the bomb has exploded */
public class BombExplodedMessage extends Message {

	private static final long serialVersionUID = 493773639985449364L;
	private static final int BOMB_EXPLODED_PRIORITY = 3;
	private String color;

	public BombExplodedMessage(String color) {
		super(Type.BOMB_EXPLODED, BOMB_EXPLODED_PRIORITY);
		this.setColor(color);
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	 
	/** If i received this i'm either dead or out of the bomb zone */
	@Override
	public boolean handleInMessage(ConnectionData clientConnection) {
		try {
			Peer peer = Peer.INSTANCE;
			OutQueue outQueue = OutQueue.INSTANCE;
			
			/** check my position */			
			if(peer.isAlive() && this.getColor().equalsIgnoreCase(peer.getCurrentPosition().getColorZone())) {
				
				/** build killed message and call handler */
				Player myself = peer.getCurrentPlayer();
				KilledMessage km = new KilledMessage(myself);
				km.handleOutMessage(clientConnection);
				
				/** create dead message */
				System.out.println("creating dead message");
				Packets packet = new Packets(new DeadMessage(myself), null);
				
				/** put message on the outQueue */
				outQueue.add(packet);
				
				System.out.println("Dead packet added to the outQueue");
			}
			else { /** just send ack */
				new AckMessage().handleOutMessage(clientConnection);
				
			} 
			
		}
		catch (Exception e){
			System.err.println("Error with incoming position message");
			return false;
		}
		return true;
	}

	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		try {
			Peer peer = Peer.INSTANCE;
			/** if i'm dead before the bomb count down finished it dies with me */
			if (peer.isAlive()){
				System.out.println("Handling message. Type: " + this);
				
				/** retrieve connections for the broadcast */
				List<ConnectionData> clientConnections = peer.getClientConnectionsList();
				System.out.println("I have " + clientConnections.size() + " connections open!");
				System.out.println("retrieved user sockets");
				
				/** now check the bomb did not killed me. Needed in case i killed someone else */
				if (this.getColor().equalsIgnoreCase(peer.getCurrentPosition().getColorZone())) 
					peer.setAlive(false);
					
				/** broadcast bomb exploded message */
				if (clientConnections.size() != 0) /** check i'm not alone */
					new Broadcast(clientConnections, this).broadcastMessage();
				
				System.out.println("Broadcast done " + this);
				
				/** this is true if the bomb killed me */
				if(!peer.isAlive()) {
					/** tell everyone else i'm dead */
					new Broadcast(clientConnections, new DeadMessage(peer.getCurrentPlayer())).broadcastMessage();
					
					/** now die gracefully */
					new ExitProcedure().startRegularProcedure(false);
				}
				
			}
		} catch (Exception e){
			System.err.println("Error with outgoing position message");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public String toString(){
		return "This is a Bomb exploded message";
	}


}
