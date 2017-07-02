package messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.TreeMap;

import beans.Player;

public class TryMessages {

	private static final int PORT = 4445;

	public static void main(String[] args) {
		TryMessages tr = new TryMessages();
		
		if (Integer.parseInt(args[0]) == 0){
			try {
				
				ServerSocket srv = new ServerSocket(PORT);
				System.out.println("Server started");
				Socket cli = srv.accept();
				ObjectInputStream inStream = new ObjectInputStream(cli.getInputStream());
	
				TreeMap<Integer, Player> players = (TreeMap<Integer, Player>) inStream.readObject();
				System.out.println(players);
				cli.close();
				srv.close();

			} 
			catch (IOException e) {
				e.printStackTrace();
			} 
			catch (ClassNotFoundException cn) {
				cn.printStackTrace();
			}
		}
		
		else {

			TreeMap<Integer, Player> pl = new TreeMap<>();
			Player p =  new Player("a","a","a",3);
			pl.put(p.getId(), p);
			tr.communicate(pl);
		}
			

	}

	public void communicate(TreeMap<Integer, Player> pl) {

		try {
			Socket socket = new Socket("localhost", PORT);
			ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("Object to be written = " + pl);
			outputStream.writeObject(pl);
			socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
