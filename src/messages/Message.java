package messages;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.Socket;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.ObjectMapper;

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
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
public abstract class Message implements Serializable {

	private static final long serialVersionUID = 4232448590544129467L;
	private static final String LOCALHOST = "localhost";
	private Type codeMessage;
	private int priority;

	public Message() { }

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
			final DataOutputStream out =  new DataOutputStream(s.getOutputStream());
			out.flush();
			final BufferedReader in =  new BufferedReader(new InputStreamReader(s.getInputStream()));
			
			// send current player
			final ObjectMapper mapper = new ObjectMapper();
			final String Jsonplayer = mapper.writeValueAsString(Peer.getInstance().getCurrentPlayer());
			out.writeBytes(Jsonplayer + "\n");
			
			return new ConnectionData(s, out, in);
		} catch (IOException e){
			System.err.println("The player chosen has closed his server socket!");
			return null;
			
		}
	}
	
	/**
	 * Serialize a message into a JSON String
	 * @param the message to serialize
	 * @return the serialization of the Message
	 */
	public String createJsonMessage(Message message) {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Deserialize a JSON string into a Message object
	 * @param the string to deserialize
	 * @return the deserialized message
	 */
	public Message readJsonMessage(String message) {
		final ObjectMapper mapper = new ObjectMapper();
		
		try {
			return mapper.readValue(message, Message.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
