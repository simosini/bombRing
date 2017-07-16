package singletons;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import peer.Cell;

/** 
 * We use this to compute a new position for a new player willing 
 * to join the game. 
 */

public class PositionList {
	
	private List<Cell> playerPositions;
	private static PositionList instance;
	
	private PositionList(){
		this.playerPositions = new ArrayList<>();
	}
	
	/**
	 * singleton
	 */
	public static synchronized PositionList getInstance() {
		if (instance == null)
			instance = new PositionList();
		return instance;
	}
	
	/**
	 * yield a copy of the position saved in the list
	 * @return the list of positions
	 */
	public synchronized List<Cell> getPlayerPositions() {
		return new ArrayList<>(playerPositions);
	}
	
	/**
	 * adds a position to the current list
	 * @param the position (cell) to be added
	 */
	public synchronized void addCell(Cell c){
		this.playerPositions.add(c);
	}
	
	public synchronized void clearList() {
		if (this.playerPositions != null)
			this.playerPositions.clear();
	}
	
	/** 
	 * yields the new computed position to be assigned to a new player 
	 */
	public Cell computeNewPosition() {
		final Peer peer = Peer.getInstance();
		final int length = peer.getCurrentGame().getSideLength();
		
		final Random random =  new Random();
		Cell newCell = null;
		
		do {
			newCell = new Cell(random.nextInt(length), random.nextInt(length));
		} while (this.playerPositions.contains(newCell));
		
		return newCell;
	}
	
	
}
