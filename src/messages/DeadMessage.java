package messages;

import beans.Player;
import peer.ConnectionData;
import services.ExitProcedure;
import singletons.Peer;


/** This message indicates a dead player */
public class DeadMessage extends Message {

	private static final long serialVersionUID = -1902910439389667349L;
	private static final int DEAD_PRIORITY = 2;
	Player deadPlayer;


	public DeadMessage(Player player) {
		super(Type.DEAD, DEAD_PRIORITY);
		this.setDeadPlayer(player); 
	}
	
	public void setDeadPlayer(Player p){
		this.deadPlayer = p;
	}
	
	public Player getDeadPlayer(){
		return this.deadPlayer;
	}
	
	/** This messages tells the receiver that another player has dead */
	@Override
	public boolean handleInMessage(ConnectionData clientConnection) {
		try {
			/** remove player from the map and from the ring */
			Peer.INSTANCE.deletePlayer(this.getDeadPlayer());
			
			/** send ack to the dead player */
			new AckMessage().handleOutMessage(clientConnection);
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/** exit game calling the exit procedure */
	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		try {
			System.out.println("Starting exit procedure");
			new ExitProcedure().startRegularProcedure(true);
		}
	}

	
	@Override
	public String toString(){
		return "This is a Dead message";
	}

}
