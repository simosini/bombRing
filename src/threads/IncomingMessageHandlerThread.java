package threads;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import messages.Message;
import messages.Packets;
import peer.ConnectionData;
import singletons.InQueue;

/** 
 * This handler takes care of the messages received on the server Socket.
 * It's started by the server and creates a packet to put on the inQueue.
 * It stays alive and connected with the same client.
 * It does not handle any message just put them on the inQueue and notify 
 * the handler.
 * */

public class IncomingMessageHandlerThread implements Runnable {

	private ConnectionData clientConnection;
	
	public IncomingMessageHandlerThread(Socket s) throws IOException{
	
		DataOutputStream outputStream = new DataOutputStream(s.getOutputStream());
		BufferedReader inputStream = new BufferedReader(new InputStreamReader(s.getInputStream()));
		this.clientConnection = new ConnectionData(s, outputStream, inputStream);
	}
	
	private ConnectionData getConnectionData() {
		return this.clientConnection;
	}

	@Override
	public void run() {
		InQueue inQueue = InQueue.INSTANCE;
		Message message = null;
		BufferedReader reader = null;
		final ObjectMapper mapper = new ObjectMapper();

		try {
			reader = this.getConnectionData().getInputStream();
			while(true){			
				
				message = mapper.readValue(reader.readLine(), new TypeReference<Message>() {
				});
				System.out.println("Received :" + message);
				synchronized(inQueue){
					
					System.out.println("Creating packet!");
					Packets packet = new Packets(message, this.getConnectionData());
					inQueue.add(packet);
					System.out.println("Packet added correctly to inQueue");
					System.out.println("Notifying handler");
					inQueue.notify();
					System.out.println("Notified");
				}	
				
			}
		} catch (IOException e){
			System.out.println("Client closed connection. Closing current socket...");
		} finally {
			Socket s = null;
			try{
				if ((s = this.getConnectionData().getClientSocket()) != null)
					s.close();
			} catch(IOException ie){
				ie.printStackTrace();
			}
		}
		
		
				
	}

}
