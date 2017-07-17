package messages;

import beans.Player;
import peer.ConnectionData;
import services.ExitProcedure;
import singletons.Peer;


/** 
 * This message indicates a dead player.
 * When a peer received this, the player indicated in the message
 * is deleted from the ring and it will never receive the token again 
 */
public class DeadMessage extends Message {

	private static final long serialVersionUID = -1902910439389667349L;
	private static final int DEAD_PRIORITY = 2;
	Player deadPlayer;
	
	public DeadMessage(){ }

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
	
	/** 
	 * This messages tells the receiver that another player has dead 
	 * */
	@Override
	public boolean handleInMessage(ConnectionData clientConnection) {
		try {
			
			// remove player from the map and from the ring 
			Peer.getInstance().deletePlayer(this.getDeadPlayer());
			Peer.getInstance().deleteConnectedSocket(this.getDeadPlayer().getId());
			
			// send ack to the dead player 
			new AckMessage().handleOutMessage(clientConnection);
		} catch (Exception e){
			System.err.println("Error handling incoming dead message");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/** 
	 * Leave the game calling the exit procedure. This procedure will perform the broadcast 
	 * and close every resource gracefully 
	 */
	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		try {
			new ExitProcedure().startRegularProcedure(false);
		}
		catch (Exception e){
			System.err.println("Error handling outgoing dead message");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	
	@Override
	public String toString(){
		return "This is a Dead message";
	}

}
