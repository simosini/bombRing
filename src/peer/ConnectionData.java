package peer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.Socket;

public class ConnectionData {
	
	private Socket clientSocket = null;
	private DataOutputStream out = null;
	private BufferedReader in = null;
	
	public ConnectionData(Socket s, DataOutputStream os, BufferedReader br) {
		this.setClientSocket(s);
		this.setOutputStream(os);
		this.setInputStream(br);
	}

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
		return in;
	}

	public void setInputStream(BufferedReader in) {
		this.in = in;
	}
}
