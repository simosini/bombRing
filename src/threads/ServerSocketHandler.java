package threads;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/** 
 * This thread is in charge of handling the main server socket.
 * It's a multiThreaded server that accept incoming connections requests
 * and starts a new thread for each new client.
 */

public class ServerSocketHandler implements Runnable {
	
	private ServerSocket srvSocket;

	public ServerSocketHandler(ServerSocket server) {
		this.setSrvSocket(server);
	}

	public void setSrvSocket(ServerSocket server) {
		this.srvSocket = server;
		
	}
	
	/**
	 * accept incoming connection and start a new server thread
	 */
	@Override
	public void run() {
		try {	
			while(true){
				Socket sender = srvSocket.accept();	
				new Thread(new IncomingMessageHandlerThread(sender)).start();
				
			} 
		}
		catch (IOException e) {
			//System.out.println("The server socket has been correctly closed!");
			
		}

	}

}
