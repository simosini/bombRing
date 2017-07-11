package singletons;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import peer.Cell;

/** we use this to compute a new position for a new player willing 
 *  to join the game */

public class PositionList {
	
	private List<Cell> playerPositions;
	private static PositionList instance;
	
	private PositionList(){
		this.playerPositions = new ArrayList<>();
	}
	
	public static synchronized PositionList getInstance() {
		if (instance == null)
			instance = new PositionList();
		return instance;
	}

	public synchronized List<Cell> getPlayerPositions() {
		return new ArrayList<>(playerPositions);
	}
	
	public synchronized void addCell (Cell c){
		this.playerPositions.add(c);
	}
	
	public synchronized void clearList() {
		if (this.playerPositions != null)
			this.playerPositions.clear();
	}
	
	/** the list contains all players positions */
	public Cell computeNewPosition() {
		Peer peer= Peer.INSTANCE;
		int length = peer.getCurrentGame().getSideLength();
		
		Random random =  new Random();
		Cell newCell = null;
		
		do {
			newCell = new Cell(random.nextInt(length), random.nextInt(length));
		} while (this.playerPositions.contains(newCell));
		
		return newCell;
	}
	
	
}
