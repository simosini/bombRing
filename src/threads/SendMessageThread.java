package threads;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

import messages.Message;
import peer.ConnectionData;

/** 
 * This is one of the broadcast threads. It just sends out the passed message
 * and wait for the answer. The handling is done calling the suitable methods. 
 */

public class SendMessageThread implements Runnable {
	
	private ConnectionData clientConnection; // server socket to send the message 
	private Message msgToSend;
	
	public SendMessageThread(final ConnectionData cd, final Message m) {
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
	
	/**
	 * send out the message from the client socket and wait for the answer
	 */
	@Override
	public void run() {
		try  {
			
			// retrieve streams 
			final DataOutputStream out = this.getClientConnection().getOutputStream();
			final BufferedReader in = this.getClientConnection().getInputStream();
			final ObjectMapper mapper = new ObjectMapper();
						
			// send message 
			final String msgToSend = mapper.writeValueAsString(this.getMsgToSend());
			out.writeBytes(msgToSend + "\n");
			
			// wait for answer
			final String msgReceived = in.readLine();
			final Message response = mapper.readValue(msgReceived, Message.class);
			
			// handle incoming message 
			response.handleInMessage(this.getClientConnection());
			
		}
		catch (IOException e){
			e.printStackTrace();
			System.err.println("Error communicating with other player's server sockets");
		} 
		
	}

}
