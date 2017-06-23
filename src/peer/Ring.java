package peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Ring {

	private static final int DEFAULT_PORT = 0;
	private List<Integer> peerPorts;
	private boolean hasToken;

	public Ring() {
		this.setOtherPeers(new ArrayList<>());
	}

	public List<Integer> getOtherPeers() {
		return peerPorts;
	}

	public void setOtherPeers(List<Integer> otherPeers) {
		this.peerPorts = otherPeers;
	}

	public void addPeer(int port) {
		this.peerPorts.add(port);
	}

	public void deletePeer(int port) {
		this.peerPorts.remove(port);
	}

	public boolean getTokenValue() {
		return hasToken;
	}

	public void setTokenValue(boolean hasToken) {
		this.hasToken = hasToken;
	}

	public void initializeRing() {
		try {
			//Ring element = new Ring();
			boolean exit = false;
			/** needed to accept token */
			ServerSocket srv = new ServerSocket(DEFAULT_PORT);
			System.out.println("My port is : " + srv.getLocalPort());
			Socket previousPeer = srv.accept();
			
			/** retrieve its server socket port */
			
			BufferedReader in = 
					new BufferedReader(
							new InputStreamReader(previousPeer.getInputStream()));
			int port = Integer.parseInt(in.readLine());
			//element.addPeer(port);
			
			/** client socket to send token */
			Thread.sleep(2000);
			Socket nextPeer = new Socket("localhost", port);
			PrintWriter out = new PrintWriter(nextPeer.getOutputStream(), true);
			while (!exit) { /** start the ring */
				/** client part that sends token to other peer server socket */
				Thread.sleep(2000);
				out.println("take the token");
				System.out.println("Master passed the token!");
				/** server part that accepts token from previous peer */
				in.readLine();
				System.out.println("Master received the token!");
			}
			srv.close();
			nextPeer.close();
			in.close();
			out.close();
		} catch (IOException | InterruptedException ie) {
			ie.printStackTrace();
		}
	}
	
	public void joinRing(int servPort){
		try {
			//Ring element = new Ring();
			boolean exit = false;
			/** needed to accept token */
			ServerSocket srv = new ServerSocket(DEFAULT_PORT);
			int myPort = srv.getLocalPort();
			//System.out.println("My port is : " + srv.getLocalPort());
			//Socket previousPeer = srv.accept();
			
			/** connect to server and send own socket port */
			Socket nextPeer = new Socket("localhost", servPort);
			PrintWriter out = new PrintWriter(nextPeer.getOutputStream(), true);
			out.println(myPort);
			/** server part we wait the token */
			Socket previousPeer = srv.accept();
			BufferedReader in = 
					new BufferedReader(
							new InputStreamReader(previousPeer.getInputStream()));
			
			
			while (!exit) { /** start the ring */
				
				/** server part that accepts token from previous peer */
				in.readLine();
				System.out.println("Joiner received the token!");
				
				/** client part that sends token to other peer server socket */
				Thread.sleep(2000);
				out.println("take the token");
				System.out.println("Joiner passed the token!");
			}
			srv.close();
			nextPeer.close();
			in.close();
			out.close();
		} catch (IOException | InterruptedException ie) {
			ie.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Ring el = new Ring();
		int choice = Integer.parseInt(args[0]);
		if (choice == 0) /** it's the master */
			el.initializeRing();
		else
			el.joinRing(Integer.parseInt(args[1]));
	}

}
