package threads;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import messages.AckMessage;
import messages.Message;

public class SendMessagesThread implements Runnable {
	
	private int port; /** port number to connect to*/
	private Message msgToSend;
	
	public SendMessagesThread(int port, Message m) {
		this.setPort(port);
		this.setMsgToSend(m);
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
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
			Socket cli = new Socket("localhost", this.getPort());
			ObjectOutputStream out = new ObjectOutputStream(cli.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(cli.getInputStream());
			/** send message */
			out.writeObject(this.getMsgToSend());
			/** wait for ack */
			Message response = (Message) in.readObject();
			if (!(response instanceof AckMessage)){
				cli.close();
				throw new ClassNotFoundException();
				
			}
			/** System.out.println(response);*/
			cli.close();
		}
		catch (IOException | ClassNotFoundException e){
			System.out.println("Error communicating with other player's server sockets");
		}
		
	}

}
