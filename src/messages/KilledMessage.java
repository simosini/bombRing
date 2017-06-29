package messages;

import java.net.Socket;

import beans.Player;

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
	public void handleMessage(Socket sender) {
		System.out.println(this.toString());
	}
	
	@Override
	public String toString(){
		return "This is a Killed message";
	}
	

}
