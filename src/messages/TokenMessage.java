package messages;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

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
			return false;
		}
		return true;
	}

	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Thread.sleep(2000);
			System.out.println("sending token to port " + clientConnection.getClientSocket().getPort());
			String message = mapper.writeValueAsString(this);
			clientConnection.getOutputStream().writeBytes(message + "\n");
		} catch (IOException | InterruptedException e){
			System.err.println("Error sending out token");
			return false;
		}
		
		return true;
	}

	@Override
	public String toString(){
		return "This is a Token message";
	}

	

}
