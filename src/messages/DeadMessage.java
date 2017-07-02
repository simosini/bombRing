package messages;

import beans.Player;
import peer.ConnectionData;

public class DeadMessage extends Message {

	private static final long serialVersionUID = -1902910439389667349L;
	private static final int DEAD_PRIORITY = 2;
	Player currentPlayer;

	public DeadMessage(Player player) {
		super(Type.DEAD, DEAD_PRIORITY);
		this.setCurrentPlayer(player); 
	}
	
	public void setCurrentPlayer(Player p){
		this.currentPlayer = p;
	}
	
	public Player getCurrentPlayer(){
		return this.currentPlayer;
	}
	
	@Override
	public boolean handleInMessage(ConnectionData clientConnection) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		// TODO Auto-generated method stub
		return false;
	}

	
	@Override
	public String toString(){
		return "This is a Dead message";
	}

}
