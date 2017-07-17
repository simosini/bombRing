package peer;

import java.io.Serializable;
import java.util.Arrays;

/**
 * A cell is the position of a peer in the distributed grid.
 * The grid does not actually exist it's only a logical grid.
 */
public class Cell implements Serializable{

	private static final long serialVersionUID = -454007840477943670L;
	
	/**
	 * possible directions
	 */
	public enum DIR {
		UP, DOWN, LEFT, RIGHT;
	}

	protected int[] gridLocation = new int[2];
	protected String colorZone;

	public Cell() {}

	public Cell(Cell currentCell) {
		int[] location = currentCell.getGridLocation();
		this.updatePosition(location[0], location[1]);
	}

	public Cell(int row, int col) {
		this.updatePosition(row, col);
	}
	
	/**
	 * setters and getters
	 */
	
	public void setColorZone(String color){
		this.colorZone = color;
	}
	
	public String getColorZone(){
		return this.colorZone;
	}

	public synchronized void updatePosition(int i, int j) {
		gridLocation[0] = i;
		gridLocation[1] = j;
	}
	
	public synchronized void setGridLocation(int[] location) { 
		this.gridLocation = location;
	}

	public synchronized int[] getGridLocation() {
		return gridLocation;
	}
	
	/**
	 * moves player from current position according to the direction given.
	 * @param the direction to move towards
	 * @return the new position after the move.
	 */
	public int[] move(DIR d) {
		final int[] currentPosition = this.getGridLocation();
		if (d != null) {
			int[] movement = this.vector(d);
			final int newPosition[] = { currentPosition[0] + movement[0], currentPosition[1] + movement[1] };
			return newPosition;
		}
		return null;
	}
	
	/**
	 * the result of a movement
	 * @param the direction
	 * @return how to change the position after the movement
	 */
	private int[] vector(DIR d) {
		switch (d) {
		case UP:
			return new int[] { -1, 0 };
		case RIGHT:
			return new int[] { 0, 1 };
		case DOWN:
			return new int[] { 1, 0 };
		case LEFT:
			return new int[] { 0, -1 };
		}
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Cell) {
			return Arrays.equals(this.getGridLocation(), ((Cell) obj).getGridLocation());
		}
		return false;
	}

	@Override
	public String toString() {
		return "Your position is: " + this.gridLocation[0] + "," + this.gridLocation[1];
	}
}
