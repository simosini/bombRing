package threads;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import messages.Message;
import peer.ConnectionData;

public class SendMessageThread implements Runnable {
	
	private ConnectionData clientConnection; /** port number to connect to*/
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
			out.writeObject(this.getMsgToSend());
			/** wait for ack */
			Message response = (Message) in.readObject();
			
			response.handleInMessage(this.getClientConnection());
			
		}
		catch (IOException | ClassNotFoundException e){
			System.out.println("Error communicating with other player's server sockets");
		}
		
	}

}
