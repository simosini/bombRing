package messages;

import peer.ConnectionData;

public class GameEndedMessage extends Message {

	private static final long serialVersionUID = 6647186110663744043L;
	private static final int END_PRIORITY = 1;  // max priority 

	public GameEndedMessage() {
		super(Type.GAME_ENDED, END_PRIORITY);
		
	}

	@Override
	public boolean handleInMessage(ConnectionData clientConnection) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public String toString(){
		return "This is the End message";
	}

}
