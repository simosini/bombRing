package messages;

import peer.ConnectionData;

public class ExitMessage extends Message {
	
	private static final long serialVersionUID = -5807158110067786776L;
	private static final int EXIT_PRIORITY = 2;

	public ExitMessage() {
		super(Type.EXITGAME, EXIT_PRIORITY);
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
		return "This is an Exit message";
	}



	
}
