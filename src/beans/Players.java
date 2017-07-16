package beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** 
 * This class contains the map of all players of a game.
 * The Integer key is used to identify a player and to find the next Peer on the ring.
 * Using a treeMap impose a total order on the keys, meaning that in order to get 
 * the next peer on the ring is sufficient to look for the closest Id greatest than your own.
 * If a peer is the last in the map it picks the first one as next, thus creating a ring.
 * As the map is passed between peers this object must be serialized.
 */
public class Players implements Serializable {

	private static final long serialVersionUID = -9053084189406099568L;
	private TreeMap<Integer, Player> usersMap;
	
	/** 
	 * initialize an empty Map
	 */
	public Players() {
		this.setUsersMap(new TreeMap<>());
	}
	
	/** 
	 * create a copy of the given object 
	 */
	public Players(Players players){
		this.setUsersMap(players.getUsersMap());
	}
	
	/** 
	 * setters and getters 
	 */

	public synchronized TreeMap<Integer, Player> getUsersMap() {
		// returns a copy
		return new TreeMap<>(usersMap);
	}

	public synchronized void setUsersMap(TreeMap<Integer, Player> userMap) {

		this.usersMap = userMap;
	}
	
	/**
	 * @return a copy of the players of the game as list
	 */
	public synchronized List<Player> retrievePlayersList() {
		// yields a copy to guarantee synchronization
		return new ArrayList<>(getUsersMap().values());

	}
	
	/**
	 * add the given player to the current map
	 * @param the player to be added to the current map
	 */
	public synchronized void addPlayer(Player p) {
		usersMap.put(p.getId(), p);

	}
	
	/**
	 * delete the given player from the map if it exists
	 * @param the player to be deleted from the map
	 */
	public synchronized void deletePlayer(Player p) {		
		Player player = usersMap.remove(p.getId());
		if (player == null) // the key does not exist
			throw new IllegalArgumentException("The player given does not exist!");		
	}

	/**
	 * @param playerName of the player to be retrieved
	 * @return a copy of the requested player or null if the player does not exist
	 */
	public Player getByName(String playerName) {
		List<Player> usersCopy = this.retrievePlayersList();
		// the players of the copied map are also copies, no sync needed 
		for (Player p : usersCopy)
			if (p.getName().equalsIgnoreCase(playerName))
				return p;

		return null;
	}
	
	/**
	 * Add all players of the given list to the map.
	 * This is always called on a copy so it won't actually change the current map.
	 * This means no sync is needed.
	 * @param list of players to be added to the map
	 */
	public void addAll(List<Player> list) {
		for (Player p : list) {
			this.addPlayer(p);
		}

	}
	
	/**
	 * @return the number of players in the map. No need to sync on the whole object
	 */
	public int size() {
		synchronized (this.usersMap) {
			return this.usersMap.size();
		}
		
	}
	
	@Override
	public String toString(){
		if (this.size() == 0)
			return "There are no players at the moment";
		else {
			StringBuilder sb = new StringBuilder("This is the list of the current active players: \n");
			Map<Integer, Player> pls = this.getUsersMap();			
			pls.forEach((k,v)-> sb.append(v.getNickname() + "\n"));
			return sb.toString();
		}
	}

}
