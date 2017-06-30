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
	private Thread handler;

	public ServerSocketHandler(ServerSocket server, Thread t) {
		this.setSrvSocket(server);
		this.handler = t;
	}

	public void setSrvSocket(ServerSocket server) {
		this.srvSocket = server;
		
	}

	@Override
	public void run() {
		while(true){
			try {
				Socket sender = srvSocket.accept();	
				System.out.println("Received message from: " + sender.getPort());
				new Thread(new ReceivedMessagesHandlerThread(handler, sender)).start();
				System.out.println("ReceivMessHandlThread started!");
			
			} catch (IOException e) {
				System.out.println("The server socket has been correctly closed!");
				System.exit(-1);
			}
		}

	}

}
