package messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import beans.Player;
import peer.ConnectionData;

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
