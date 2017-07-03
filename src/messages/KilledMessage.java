package messages;

import org.codehaus.jackson.map.ObjectMapper;

import beans.Player;
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
			System.out.println("Score before: " + peer.getCurrentScore());
			
			/** remove player from the map. NO will remove it later when i get the DeadMessage
			peer.deletePlayer(this.getKilledPlayer());
			System.out.println("Player deleted correctly");
			*/
			
			/** add points if i'm alive and check victory */
			if (peer.isAlive() && peer.getCurrentScore() < targetScore){
				/** set new score */
				peer.incrementCurrentScore();
				System.out.println("Score after: " + peer.getCurrentScore());
				
				if (peer.getCurrentScore() == targetScore){
					System.out.println("I won!");
					
					/** game is finished set alive false */
					peer.setAlive(false);
					
					/** put the message on the outQueue */
					Message victory = new VictoryMessage();
					victory.setInput(false);
					Packets newPacket = new Packets(victory, null);
					synchronized (outQueue) {
						outQueue.add(newPacket);
					}
					
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
			ObjectMapper mapper = new ObjectMapper();
			System.out.println("You have been killed!");
			Peer.INSTANCE.setAlive(false); // i'm dead
		
			/** send killed */
			System.out.println("sending killed message");
			String message = mapper.writeValueAsString(this);
			cd.getOutputStream().writeBytes(message + "\n");
			
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
