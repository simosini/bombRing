package messages;

import java.net.Socket;
import java.util.PriorityQueue;

import peer.Peer;

public class BombTossedMessage extends Message {

	private static final long serialVersionUID = -8610892414931963536L;
	private static final int BOMB_TOSSED_PRIORITY = 4;
	private String color;

	public BombTossedMessage(String color) {
		super(Type.BOMB_TOSSED, BOMB_TOSSED_PRIORITY);
		this.setColor(color);
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	@Override
	public void handleMessage(Socket sender, PriorityQueue<Packets> outQueue, Peer peer) {
		System.out.println(this.toString());

	}

	@Override
	public String toString(){
		return "This is a Bomb tossed message";
	}

}

