package singletons;

import java.util.ArrayList;
import java.util.List;

import peer.Cell;

/** we use this to compute a new position for a new player willing 
 *  to join the game */

public enum PositionList {
	ISTANCE;
	
	private List<Cell> playerPositions;
	
	private PositionList(){
		this.playerPositions = new ArrayList<>();
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
	
	
}
