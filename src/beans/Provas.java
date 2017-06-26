package beans;

import peer.Cell;

public class Provas {

	public static void main(String[] args) {
		Cell c = new Cell();
		c.setPosition(1, 1);
		int[] newPosition = c.move(Cell.DIR.LEFT);
		System.out.println(newPosition[1]);
		//c.setPosition(newPosition[0], newPosition[1]);
		//c.getPosition();
		//System.out.println(c);

	}

}
