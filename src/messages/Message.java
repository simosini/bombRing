package messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import beans.Player;
import peer.ConnectionData;
import singletons.Peer;

/**
 * Abstract generic class for messages. Every message has a specific code and a
 * priority associated so as to handle them accordingly. 
 * The two abstract methods to implements are: 
 * 1- handleInMessage: this method is called to handle messages received on the 
 * 	  peer's server Socket
 * 2- handleOutMessage: this method is called to handle messages that need to be 
 * 	  sent out from one of the peer's client sockets
 */

public abstract class Message implements Serializable {

	private static final long serialVersionUID = 4232448590544129467L;
	private static final String LOCALHOST = "localhost";
	private Type codeMessage;
	private int priority;

	public Message() {
	}

	public Message(Type type, int priority) {
		this.setCodeMessage(type);
		this.setPriority(priority);

	}
	
	/**
	 * setters and getters
	 */
	
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

	/**
	 * connects to the server socket of the player passed as argument 
	 * and initializes streams
	 * @param the player to be connected with
	 * @return an object containing the connected socket and the initialized streams
	 */
	public ConnectionData connectToPlayer(Player p) {
		try {
			// connect to the server socket
			final Socket s = new Socket(LOCALHOST, p.getPort());
			
			// initialize streams
			final ObjectOutputStream out =  new ObjectOutputStream(s.getOutputStream());
			out.flush();
			ObjectInputStream in =  null;

			// send current player 
			out.writeObject(Peer.getInstance().getCurrentPlayer());
			
			return new ConnectionData(s, out, in);
		} catch (IOException e){
			System.err.println("The player chosen has closed his server socket!");
			return null;
			
		}
	}

	/**
	 * method to handle incoming messages
	 * @param the connection parameters of the client who sent the message
	 * @return true if the message has been handled correctly
	 */
	public abstract boolean handleInMessage(ConnectionData clientConnection);
	
	/**
	 * method to handle outgoing messages
	 * @param the connection parameters of the client who sent the message
	 * @return true if the message has been handled correctly
	 */
	public abstract boolean handleOutMessage(ConnectionData clientConnection);

}
