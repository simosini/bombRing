package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import messages.PositionMessage;

public class Provare {

	public static void main(String[] args) throws IOException, ClassNotFoundException{
		/*Peer peer = new Peer();
		Player p = new Player("simo", "sini", "simosini", 100);
		Cell cell =  new Cell();
		cell.setPosition(3, 5);
		cell.setZoneColor("red");
		peer.setCurrentGame(new Game("trivial", 6, 3, p));
		peer.setCurrentPlayer(p);
		peer.setCurrentPosition(cell);
		
		System.out.println(peer.getCurrentGame());
		peer.addNewPlayer(new Player("a","a","a",120));
		System.out.println(peer.getCurrentGame());
		peer.deletePlayer(p);
		System.out.println(peer.getCurrentGame());
		System.out.println(peer.getCurrentPosition().getZoneColor());*/
		PositionMessage pm = new PositionMessage(4, 4);
		Socket s = new Socket("localhost", 40275);
		System.out.println("Connected to server");
		ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject(pm);
		System.out.println("Message sent");
		ObjectInputStream in = new ObjectInputStream(s.getInputStream());
		System.out.println("Reading answer");
		System.out.println(in.readObject());
		s.close();
	}

}
