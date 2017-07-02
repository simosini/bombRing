package messages;

import peer.ConnectionData;

public class VictoryMessage extends Message {

	private static final long serialVersionUID = 4714778184603472137L;
	private static final int VICTORY_PRIORITY = 2;
	
	public VictoryMessage() {
		super(Type.VICTORY, VICTORY_PRIORITY);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean handleInMessage(ConnectionData cd) {
		return true;
	}
	
	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public String toString(){
		return "This is a Victory message";
	}

}
