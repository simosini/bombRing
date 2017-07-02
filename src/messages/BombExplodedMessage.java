package messages;

import peer.ConnectionData;

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
		return "This is a Bomb exploded message";
	}


}
