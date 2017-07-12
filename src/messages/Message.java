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
 * priority associated so as to handle them accordingly. There is also a boolean
 * value to check if is a received message or a message to be sent, this allows
 * different handling of the same message.
 **/

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

	
	public ConnectionData connectToPlayer(Player p) {
		try {
			Socket s = new Socket(LOCALHOST, p.getPort());
			//System.out.println("Connection established to port " + s.getPort());
			//System.out.println("My port is " + s.getLocalPort());
			ObjectOutputStream out =  new ObjectOutputStream(s.getOutputStream());
			out.flush();
			//System.out.println("out stream done");
			ObjectInputStream in =  null;
			//System.out.println("Streams done!");
			/** send current player */
			out.writeObject(Peer.getInstance().getCurrentPlayer());
			return new ConnectionData(s, out, in);
		} catch (IOException e){
			System.err.println("The player chosen has closed his server socket!");
			return null;
			
		}
	}
	/** true means the message has been handled correctly */
	public abstract boolean handleInMessage(ConnectionData clientConnection);
	
	public abstract boolean handleOutMessage(ConnectionData clientConnection);

}
