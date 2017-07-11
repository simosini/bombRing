package threads;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import messages.Message;
import peer.ConnectionData;

/** This is one of the broadcast threads. It just sends out the passed msg
 *  and wait for the answer. The handling is done calling the suitable methods. */

public class SendMessageThread implements Runnable {
	
	private ConnectionData clientConnection; /** server socket to send the message */
	private Message msgToSend;
	
	public SendMessageThread(ConnectionData cd, Message m) {
		this.setClientConnection(cd);
		this.setMsgToSend(m);
	}

	public ConnectionData getClientConnection() {
		return this.clientConnection;
	}

	public void setClientConnection(ConnectionData cd) {
		this.clientConnection = cd;
	}

	public Message getMsgToSend() {
		return msgToSend;
	}

	public void setMsgToSend(Message msgToSend) {
		this.msgToSend = msgToSend;
	}

	@Override
	public void run() {
		try  {
			/** retrieve streams */
			ObjectOutputStream out = this.getClientConnection().getOutputStream();
			ObjectInputStream in = this.getClientConnection().getInputStream();
						
			/** send message */
			out.writeObject(this.getMsgToSend());;
			
			/** wait for answer */
			Message response = (Message) in.readObject();
			
			/** handle incoming message */
			response.handleInMessage(this.getClientConnection());
			
		}
		catch (IOException | ClassNotFoundException e){
			e.printStackTrace();
			System.err.println("Error communicating with other player's server sockets");
		} 
		
	}

}
