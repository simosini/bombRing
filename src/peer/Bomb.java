package peer;

/**
 * This is a bomb that can be used in the game. It can be one of 4 different colors:
 * yellow, blue, green and red.
 */
public class Bomb {
	
	private String color;
	
	public Bomb(String color) {
		this.setColor(color);
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	@Override
	public String toString(){
		return "This is a " + this.getColor() + " bomb.";
	}

}
