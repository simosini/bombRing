package messages;

import java.io.IOException;

import peer.ConnectionData;
import threads.OutGoingMessageHandlerThread;

public class TokenMessage extends Message {
	
	private static final long serialVersionUID = -2487582210405295762L;
	private static final int TOKEN_PRIORITY = 5;
	
	
	public TokenMessage() {
		super(Type.TOKEN, TOKEN_PRIORITY);
	}
	
	 /** whenever received a new outgoing message thread is started
	  *  This thread checks if there is any message on the outQueue
	  *  if so, it takes the first one and call the handlingOut function
	  *  on it. As soon as it's done it passes the token. */
	@Override
	public boolean handleInMessage(ConnectionData clientConnection) {
		try {
			/** start Handler. it will pass the token when is done */
			OutGoingMessageHandlerThread outHandler = new OutGoingMessageHandlerThread(this);
			Thread t = new Thread(outHandler);
			t.start();
			t.join();
		} catch (Exception e){
			System.err.println("Error handling incoming token");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		try {
			Thread.sleep(100);
			//System.out.println("sending token to port " + clientConnection.getClientSocket().getPort());
			clientConnection.getOutputStream().writeObject(this);
		} catch (IOException | InterruptedException e){
			/**if the game is finished and I won, I might not have a next active player */
			System.err.println("Error handling outgoing token");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	@Override
	public String toString(){
		return "This is a Token message";
	}

	

}
