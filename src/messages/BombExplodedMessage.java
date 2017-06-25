package messages;

import java.net.Socket;
import java.util.PriorityQueue;

public class BombExplodedMessage extends Message {

	private static final long serialVersionUID = 493773639985449364L;
	private static final int BOMB_EXPLODED_PRIORITY = 2;
	private String color;

	public BombExplodedMessage(String color) {
		super(Type.BOMB_EXPLODED, BOMB_EXPLODED_PRIORITY);
		this.setColor(color);
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	@Override
	public void handleMessage(Socket sender, PriorityQueue<Packets> outQueue) {
		System.out.println(this.toString());

	}

	@Override
	public String toString(){
		return "This is a Bomb exploded message";
	}

}
