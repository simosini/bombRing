package messages;

import java.io.Serializable;

public class BombMessage extends Message implements Serializable {

	private static final long serialVersionUID = 493773639985449364L;
	private static final int BOMB_PRIORITY = 2;
	private String color;

	public BombMessage(String color) {
		super(Type.BOMB, BOMB_PRIORITY);
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
		return "This is a Bomb message";
	}

}
