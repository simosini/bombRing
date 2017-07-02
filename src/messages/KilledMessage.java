package messages;

import beans.Player;
import peer.ConnectionData;
import singletons.Peer;

public class KilledMessage extends Message {

	private static final long serialVersionUID = 184832401348782267L;
	private static final int KILLED_PRIORITY = 5;
	private Player killedPlayer;

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

	@Override
	public boolean handleInMessage(ConnectionData cd) {
		try {
			Peer peer = Peer.INSTANCE;
			/** add points if i'm alive and check victory */
			if (peer.isAlive()){
				peer.setCurrentScore(peer.getCurrentScore() + 1);
				/****** need to check victory but avoid other thread to increment score***/
				
			}
		}
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
