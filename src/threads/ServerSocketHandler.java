package threads;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/** 
 * This thread is in charge of handling the server socket.
 * it accepts requests from outside 
 */

public class ServerSocketHandler implements Runnable {
	
	private ServerSocket srvSocket;

	public ServerSocketHandler(ServerSocket server) {
		this.setSrvSocket(server);
	}

	public void setSrvSocket(ServerSocket server) {
		this.srvSocket = server;
		
	}

	@Override
	public void run() {
		try {	
			while(true){
				Socket sender = srvSocket.accept();	
				System.out.println("Received message from: " + sender.getPort());
				new Thread(new IncomingMessageHandlerThread(sender)).start();
				System.out.println("ReceivMessHandlThread started!");
			
			} 
		}
		catch (IOException e) {
			System.out.println("The server socket has been correctly closed!");
			
		}

	}

}
