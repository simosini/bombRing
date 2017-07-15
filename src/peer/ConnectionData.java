package peer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * ConnectionData contains the client socket and the streams to communicate
 * with another peer server socket. Every client connection data is saved in a map by the peer
 * and kept open during the whole game as far as the 2 peers are still alive.
 */
public class ConnectionData {
	
	private Socket clientSocket = null;
	private ObjectOutputStream out = null;
	private ObjectInputStream in = null;
	
	public ConnectionData(Socket s, ObjectOutputStream os, ObjectInputStream is) {
		this.setClientSocket(s);
		this.setOutputStream(os);
		this.setInputStream(is);
	}

	public Socket getClientSocket() {
		return clientSocket;
	}

	private void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public ObjectOutputStream getOutputStream() {
		return out;
	}

	private void setOutputStream(ObjectOutputStream out) {
		this.out = out;
	}

	public ObjectInputStream getInputStream() {
		if (this.in == null){
			try {
				in = new ObjectInputStream(this.clientSocket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return this.in;
	}

	public void setInputStream(ObjectInputStream in) {
		this.in = in;
	}
}
