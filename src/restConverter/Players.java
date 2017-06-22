package restConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Players {

	private Map<Integer, Player> usersMap;

	public Players() {

		this.setUsersMap(new TreeMap<>());
	}
	
	public Players(Players players){
		this.setUsersMap(players.getUsersMap());
	}

	public synchronized List<Player> retrievePlayersList() {
		// yields a copy to guarantee synchronization
		return new ArrayList<>(getUsersMap().values());

	}

	public synchronized Map<Integer, Player> getUsersMap() {

		return new TreeMap<>(usersMap);
	}

	public synchronized void setUsersMap(Map<Integer, Player> userMap) {

		this.usersMap = userMap;
	}

	public synchronized void add(Player p) {

		usersMap.put(p.getId(), p);

	}

	public synchronized Player getByName(String name) {

		List<Player> usersCopy = retrievePlayersList();

		for (Player p : usersCopy)
			if (p.getName().equalsIgnoreCase(name))
				return p;

		return null;
	}

	public void addAll(List<Player> list) {

		for (Player p : list) {
			this.add(p);
		}

	}

	public synchronized int size() {
		
		return this.usersMap.size();
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder("This is the list of the current active players: \n");
		Map<Integer, Player> pls = this.getUsersMap();
		pls.forEach((k,v)-> sb.append(v.getNickname() + "\n"));
		return sb.toString();
	}

}
