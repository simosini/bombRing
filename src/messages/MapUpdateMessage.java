package messages;

import java.util.TreeMap;

import org.codehaus.jackson.map.ObjectMapper;

import beans.Player;
import peer.ConnectionData;
import singletons.Peer;

public class MapUpdateMessage extends Message {

	private static final long serialVersionUID = -4776904767781287642L;
	private static final int UPDATE_PRIORITY = 5;
	private TreeMap<Integer, Player> updatedMap;
	
	public MapUpdateMessage(){}

	public MapUpdateMessage(TreeMap<Integer, Player> map) {
		super(Type.MAP_UPDATE, UPDATE_PRIORITY);
		this.setUpdatedMap(map);
	}

	public TreeMap<Integer, Player> getUpdatedMap() {
		return updatedMap;
	}

	public void setUpdatedMap(TreeMap<Integer, Player> updatedMap) {
		this.updatedMap = updatedMap;
	}

	@Override
	public boolean handleInMessage(ConnectionData clientConnection) {
		/** update map and exit */
		TreeMap<Integer, Player> updatedMap = this.getUpdatedMap();
		/** send a copy to be saved so I can change it */
		Peer.INSTANCE.updateMapPlayers(new TreeMap<Integer, Player>(updatedMap));
		return true;
	}

	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		/** send the new player the updated map */
		try {
			ObjectMapper mapper = new ObjectMapper();
			String message = mapper.writeValueAsString(this);
			clientConnection.getOutputStream().writeBytes(message + "\n");
		} catch (Exception e){
			System.err.println("Error sending updated map to client");
			return false;
		}
		return true;
	}
	
	@Override
	public String toString(){
		return "This is an updateMap message";
		
	}

}
