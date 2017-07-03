package messages;

import peer.ConnectionData;

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
		return "This is a Bomb tossed message";
	}

}

