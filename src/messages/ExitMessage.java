package messages;

import peer.ConnectionData;
import services.ExitProcedure;

/** leave the game after user choice */
public class ExitMessage extends Message {
	
	private static final long serialVersionUID = -5807158110067786776L;
	private static final int EXIT_PRIORITY = 2;

	public ExitMessage() {
		super(Type.EXITGAME, EXIT_PRIORITY);
	}
	
	/** this messages will be mapped to the Dead message so 
	 *  this can never be received */
	@Override
	public boolean handleInMessage(ConnectionData clientConnection) {
		return true;
		
	}

	/** starts regular exit procedure */
	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		try {
			System.out.println("Starting exit procedure");
			new ExitProcedure().startRegularProcedure(true);
		}
		catch (Exception e) {
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
