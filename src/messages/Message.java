package messages;

import java.io.Serializable;
import java.net.Socket;
import java.util.TreeMap;

import beans.Player;
import beans.Players;
import singletons.Peer;

/**
 * Abstract generic class for messages. Every message has a specific code and a
 * priority associated so as to handle them accordingly. There is also a boolean
 * value to check if is a received message or a message to be sent, this allows
 * different handling of the same message.
 **/

public abstract class Message implements Serializable {

	private static final long serialVersionUID = 4232448590544129467L;
	private Type codeMessage;
	private int priority;
	private boolean isInput;

	public Message() {
	}

	public Message(Type type, int priority) {
		this.setCodeMessage(type);
		this.setPriority(priority);
		this.setInput(true); // default value to be set at creation
	}

	public Type getCodeMessage() {
		return codeMessage;
	}

	public void setCodeMessage(Type codeMessage) {
		this.codeMessage = codeMessage;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public boolean checkIsInput() {
		return isInput;
	}

	public void setInput(boolean flag) {
		this.isInput = flag;

	}

	public Player getNextPeer(Players players) {
		Peer peer = Peer.INSTANCE;
		TreeMap<Integer, Player> gamePlayers = players.getUsersMap(); //it's a copy
		if (gamePlayers.size() > 1) {
			Integer nextPlayerKey = findNextPlayerKey(peer.getCurrentPlayer().getId(), gamePlayers);
			return gamePlayers.get(nextPlayerKey);
		} else {
			return null; // no more players, only me in the map

		}
	}

	private Integer findNextPlayerKey(Integer key, TreeMap<Integer, Player> gamePlayers) {
		Integer nextKey = null;
		if ((nextKey = gamePlayers.higherKey(key)) == null)
			return gamePlayers.firstKey();
		else
			return nextKey;
	}

	public abstract void handleInMessage(Socket sender);
	
	public abstract void handleOutMessage(Socket sender);

}
