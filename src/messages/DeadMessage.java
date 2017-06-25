package messages;

import java.net.Socket;
import java.util.PriorityQueue;

import restConverter.Player;

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
	public void handleMessage(Socket sender, PriorityQueue<Packets> outQueue) {
		System.out.println(this.toString());

	}
	
	@Override
	public String toString(){
		return "This is a Dead message";
	}

}
