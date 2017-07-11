package beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Players implements Serializable {

	private static final long serialVersionUID = -9053084189406099568L;
	private TreeMap<Integer, Player> usersMap;

	public Players() {

		this.setUsersMap(new TreeMap<>());
	}
	
	public Players(Players players){
		this.setUsersMap(players.getUsersMap());
	}

	public synchronized TreeMap<Integer, Player> getUsersMap() {
		// returns a copy
		return new TreeMap<>(usersMap);
	}

	public synchronized void setUsersMap(TreeMap<Integer, Player> userMap) {

		this.usersMap = userMap;
	}
	
	public synchronized List<Player> retrievePlayersList() {
		// yields a copy to guarantee synchronization
		return new ArrayList<>(getUsersMap().values());

	}

	public synchronized void addPlayer(Player p) {

		usersMap.put(p.getId(), p);

	}
	
	public synchronized void deletePlayer(Player p) {
		
		Player player = usersMap.remove(p.getId());
		if (player == null) // the key does not exist
			throw new IllegalArgumentException("The player given does not exist!");		
	}

	public Player getByName(String name) {

		List<Player> usersCopy = retrievePlayersList();

		for (Player p : usersCopy)
			if (p.getName().equalsIgnoreCase(name))
				return p;

		return null;
	}
	
	// no sync cause is a helper function always called from within game copies
	public void addAll(List<Player> list) {

		for (Player p : list) {
			this.addPlayer(p);
		}

	}

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
