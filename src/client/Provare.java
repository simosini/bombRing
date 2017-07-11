package client;

import java.io.IOException;

import beans.Game;
import peer.Cell;
import singletons.Peer;
import singletons.PositionList;

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
		System.out.println(peer.getCurrentPosition().getZoneColor());
		PositionMessage pm = new PositionMessage(4, 4);
		Socket s = new Socket("localhost", 37693);
		System.out.println("Connected to server");
		ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject(pm);
		System.out.println("Message sent");
		ObjectInputStream in = new ObjectInputStream(s.getInputStream());
		System.out.println("Reading answer");
		System.out.println(in.readObject());
		s.close();
		MeasureBuffer buffer = MeasureBuffer.getInstance();
		Measurement m0 = new Measurement("id0","ma",10,100);
		Measurement m1 = new Measurement("id1","ma",8,100);
		buffer.addNewMeasurement(m0);
		buffer.addNewMeasurement(m1);
		System.out.println(buffer);
		System.out.println(buffer.readAllAndClean());
		System.out.println(buffer);*/
		int i = 0;
		while (i < 4){
			Cell c1 = new Cell(1, 1);
			Cell c2 = new Cell(0, 1);
			Cell c3 = new Cell(0,0);
			Game g = new Game("a",2, 2);
			Peer.INSTANCE.setCurrentGame(g);
			PositionList.getInstance().addCell(c1);
			PositionList.getInstance().addCell(c2);
			PositionList.getInstance().addCell(c3);
			System.out.println(PositionList.getInstance().getPlayerPositions());
			System.out.println(PositionList.getInstance().computeNewPosition());
			PositionList.getInstance().clearList();
			System.out.println(PositionList.getInstance().getPlayerPositions());
			i++;
		}
	}

}
