package messages;

import java.net.Socket;
import java.util.PriorityQueue;

import restConverter.Player;

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
	public void handleMessage(Socket sender, PriorityQueue<Packets> outQueue) {
		System.out.println(this.toString());
	}
	
	@Override
	public String toString(){
		return "This is a Killed message";
	}
	

}
