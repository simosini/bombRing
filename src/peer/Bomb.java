package peer;

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
