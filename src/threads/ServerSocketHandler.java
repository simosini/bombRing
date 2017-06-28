package threads;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Queue;

import messages.Packets;

/** 
 * This thread is in charge of handling the server socket.
 * it accepts requests from outside 
 */

public class ServerSocketHandler implements Runnable {
	
	private ServerSocket srvSocket;
	private Queue<Packets> inQueue;
	private Thread handler;

	public ServerSocketHandler(ServerSocket server, Queue<Packets> q, Thread t) {
		this.setSrvSocket(server);
		this.inQueue = q;
		this.handler = t;
	}

	public void setSrvSocket(ServerSocket server) {
		this.srvSocket = server;
		
	}

	@Override
	public void run() {
		while(true){
			try {
				ReceivedMessagesHandlerThread rmht = 
						new ReceivedMessagesHandlerThread(inQueue, handler, srvSocket.accept());
				new Thread(rmht).start();
			
			} catch (IOException e) {
				System.out.println("The server socket has been correctly closed!");
				System.exit(-1);
			}
		}

	}

}
