package peer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * ConnectionData contains the client socket and the streams to communicate
 * with another peer server socket. Every client connection data is saved in a map by the peer
 * and kept open during the whole game as far as the 2 peers are still alive.
 */
public class ConnectionData {
	
	private Socket clientSocket = null;
	private DataOutputStream out = null;
	private BufferedReader in = null;
	
	public ConnectionData(Socket s, DataOutputStream os, BufferedReader br) {
		this.setClientSocket(s);
		this.setOutputStream(os);
		this.setInputStream(br);
	}
	
	/**
	 * setters and getters
	 */
	
	public Socket getClientSocket() {
		return clientSocket;
	}

	private void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public DataOutputStream getOutputStream() {
		return out;
	}

	private void setOutputStream(DataOutputStream out) {
		this.out = out;
	}

	public BufferedReader getInputStream() {
		if (this.in == null){
			try {
				this.in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return this.in;
	}

	public void setInputStream(BufferedReader br) {
		this.in = br;
	}
}
