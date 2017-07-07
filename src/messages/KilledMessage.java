package messages;

import java.util.List;

import beans.Player;
import peer.Broadcast;
import peer.ConnectionData;
import singletons.OutQueue;
import singletons.Peer;

public class KilledMessage extends Message {

	private static final long serialVersionUID = 184832401348782267L;
	private static final int KILLED_PRIORITY = 5;
	private Player killedPlayer;
	
	public KilledMessage(){}

	public KilledMessage(Player p) {
		super(Type.KILLED, KILLED_PRIORITY);
		this.setKilledPlayer(p);

	}

	public Player getKilledPlayer() {
		return killedPlayer;

	}

	public void setKilledPlayer(Player killedPlayer) {
		this.killedPlayer = killedPlayer;

	}
	
	/** I receive this if i killed someone else */
	@Override
	public boolean handleInMessage(ConnectionData cd) {
		try {
			final Peer peer = Peer.INSTANCE;
			final OutQueue outQueue = OutQueue.INSTANCE;
			final int targetScore = peer.getCurrentGame().getScoreNeeded();
			System.out.println("You just killed " + this.getKilledPlayer().getNickname());
			System.out.println("Score before: " + peer.getCurrentScore());
			
			/** add points if i'm alive and check victory */
			if (peer.isAlive() && peer.getCurrentScore() < targetScore){
				/** set new score */
				peer.incrementCurrentScore();
				System.out.println("Score after: " + peer.getCurrentScore());
				
				if (peer.getCurrentScore() == targetScore){
					System.out.println("I won!");
					
					/** game is finished set alive false */
					peer.setAlive(false);
					System.out.println("Informing other players!");
					
					/** retrieve connections to other serverSockets */
					List<ConnectionData> otherPlayers  = peer.getClientConnectionsList();
					System.out.println("retrieved user sockets");
					
					/** tell every player the game is finished. I cannot be alone */
					new Broadcast(otherPlayers, new VictoryMessage()).broadcastMessage();
					
					System.out.println("Broadcast done");
					
					/** put Victory message on the outQueue */					
					Packets newPacket = new Packets(new VictoryMessage(), null);
					outQueue.add(newPacket);
					
				}
								
			}
			
		} catch(Exception e) {
			System.out.println("Error handling incoming killed message");
			return false;
		}
		return true;
	}
	
	/** I send back that i'm dead */
	@Override
	public boolean handleOutMessage(ConnectionData cd){
		try {
			System.out.println("You have been killed!");
			Peer.INSTANCE.setAlive(false); // i'm dead
		
			/** send killed */
			System.out.println("sending killed message");
			cd.getOutputStream().writeObject(this);
			
		}
		catch(Exception e){
			System.out.println("Error handling killedMessage out");
			return false;
		}
		return true;
	}
	
	@Override
	public String toString(){
		return "This is a Killed message";
	}
	

}
