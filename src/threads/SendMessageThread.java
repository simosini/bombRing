package threads;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

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
			final DataOutputStream out = this.getClientConnection().getOutputStream();
			final BufferedReader in = this.getClientConnection().getInputStream();
			final ObjectMapper mapper = new ObjectMapper();
			
			/** send message */
			String messageJSON = mapper.writeValueAsString(this.getMsgToSend());
			out.writeBytes(messageJSON + "\n"); 
			
			/** wait for answer */
			Message response = mapper.readValue(in.readLine(), new TypeReference<Message>() { });
			
			response.handleInMessage(this.getClientConnection());
			
		}
		catch (IOException e){
			System.out.println("Error communicating with other player's server sockets");
		}
		
	}

}
