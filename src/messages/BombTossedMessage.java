package messages;

import java.io.Serializable;

public class BombTossedMessage extends InMessage implements Serializable {

	private static final long serialVersionUID = -8610892414931963536L;
	private static final int BOMB_TOSSED_PRIORITY = 2;
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
	public void handleMessage() {
		System.out.println(this.toString());

	}

	@Override
	public String toString(){
		return "This is a Bomb tossed message";
	}

}

