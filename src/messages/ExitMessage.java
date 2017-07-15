package messages;

import peer.ConnectionData;
import services.ExitProcedure;
import singletons.Peer;

/** 
 * This message is used in case a player decides to leave the game 
 * while it's still alive.
 */
public class ExitMessage extends Message {
	
	private static final long serialVersionUID = -5807158110067786776L;
	private static final int EXIT_PRIORITY = 2;

	public ExitMessage() {
		super(Type.EXITGAME, EXIT_PRIORITY);
	}
	
	/** 
	 * This messages will be mapped to the Dead message so 
	 * this can never be received 
	 */
	@Override
	public boolean handleInMessage(ConnectionData clientConnection) {
		return true;
		
	}

	/** 
	 * Current player has decided to leave the game: starts regular exit procedure 
	 */
	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		try {
			Peer peer = Peer.getInstance();
			peer.setAlive(false);
			new ExitProcedure().startRegularProcedure(false);
			if (peer.getNumberOfPlayers() > 1)
				System.out.println("done!");
		}
		catch (Exception e) {
			System.err.println("Error handling outgoing dead message");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	

	@Override
	public String toString(){
		return "This is an Exit message";
	}



	
}
