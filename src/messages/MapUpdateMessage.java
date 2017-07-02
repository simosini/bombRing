package messages;

import java.util.TreeMap;

import beans.Player;
import peer.ConnectionData;
import singletons.Peer;

public class MapUpdateMessage extends Message {

	private static final long serialVersionUID = -4776904767781287642L;
	private static final int UPDATE_PRIORITY = 5;
	private TreeMap<Integer, Player> updatedMap;

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
		// TODO Auto-generated method stub
		return false;
	}

}
