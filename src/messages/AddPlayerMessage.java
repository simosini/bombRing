package messages;

import java.net.Socket;
import java.util.PriorityQueue;

import beans.Player;

public class AddPlayerMessage extends Message {

	private static final long serialVersionUID = 4448703639215851985L;
	private static final int ADD_PRIORITY = 3;
	private Player playerToAdd;

	public AddPlayerMessage(Player p) {
		super(Type.ADDPLAYER, ADD_PRIORITY);
		this.setPlayerToAdd(p);
	}

	public Player getPlayerToAdd() {
		return playerToAdd;
	}

	public void setPlayerToAdd(Player playerToAdd) {
		this.playerToAdd = playerToAdd;
	}
	
	@Override
	public void handleMessage(Socket sender, PriorityQueue<Packets> outQueue) {
		System.out.println(this.toString());

	}
	
	@Override
	public String toString(){
		return "This is an Addplayer message";
	}

}
