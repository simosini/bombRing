package messages;

import java.io.IOException;
import java.util.TreeMap;

import beans.Player;
import peer.Cell;
import peer.ConnectionData;
import singletons.Peer;

/**
 * This message is used to provide a new player the updated map of the game players 
 * and its new position on the grid. This is used to maintain consistency, because it might 
 * happen that the map given by the REST server is out of date and to ensure the new player
 * has been given a free spot on the grid.
 */
public class MapUpdateMessage extends Message {

	private static final long serialVersionUID = -4776904767781287642L;
	private static final int UPDATE_PRIORITY = 5;
	private TreeMap<Integer, Player> updatedMap;
	private Cell updatedPosition;
	
	public MapUpdateMessage(){}

	public MapUpdateMessage(TreeMap<Integer, Player> map, Cell pos) {
		super(Type.MAP_UPDATE, UPDATE_PRIORITY);
		this.setUpdatedMap(map);
		this.setUpdatedPosition(pos);
	}
	
	/**
	 * getters and setters
	 */
	public TreeMap<Integer, Player> getUpdatedMap() {
		return updatedMap;
	}

	private void setUpdatedMap(TreeMap<Integer, Player> updatedMap) {
		this.updatedMap = updatedMap;
	}

	public Cell getUpdatedPosition() {
		return updatedPosition;
	}

	private void setUpdatedPosition(Cell updatedPosition) {
		this.updatedPosition = updatedPosition;
	}

	/**
	 * When a peer receives this message it updates his map and set his position.
	 * This also means that the player has been added correctly to the ring.
	 */
	@Override
	public boolean handleInMessage(ConnectionData clientConnection) {
		// update map and exit 
		TreeMap<Integer, Player> updatedMap = this.getUpdatedMap();
		//System.out.println("UpdatedMap : " + updatedMap);
		// save a copy so I can change it later 
		Peer.getInstance().updateMapPlayers(new TreeMap<Integer, Player>(updatedMap));
		
		// update current position: another copy 
		Cell newPos = this.getUpdatedPosition();
		//System.out.println("My position is: " + newPos);
		Peer.getInstance().setCurrentPosition(new Cell(newPos));
		return true;
	}
	
	/** 
	 * This message is sent to a new player to inform him has been added correctly 
	 */
	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		// send the new player the updated map and position 
		try {
			clientConnection.getOutputStream().writeObject(this);
		} catch (IOException e){
			System.err.println("Error sending updated map to client");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public String toString(){
		return "This is an updateMap message";
		
	}

}
