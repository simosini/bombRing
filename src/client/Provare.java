package client;

import beans.Game;
import beans.Player;
import peer.Cell;
import peer.Peer;

public class Provare {

	public static void main(String[] args) {
		Peer peer = new Peer();
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
	}

}
