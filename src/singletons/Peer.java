package singletons;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import beans.Game;
import beans.Player;
import peer.Cell;

public enum Peer {

	INSTANCE;

	private Game currentGame;
	private Player currentPlayer; 
	private int currentScore = 0;
	private boolean isAlive = false;
	private Cell currentPosition;

	private Peer() {}

	/*public static synchronized Peer getInstance(){
		if (instance == null)
			return new Peer();
		return instance;
	}*/

	/** getters and setters method */
	
	/** not sync methods means only the handler call them */
	public Game getCurrentGame() {
		// yields a copy
		return new Game(this.currentGame);
	}

	public void setCurrentGame(Game currentGame) {
		this.currentGame = currentGame;
	}

	public Player getCurrentPlayer() {
		// yields a copy
		return new Player(this.currentPlayer);
	}

	public void addPlayer(Player currentPlayer) {
		this.currentPlayer = currentPlayer;
	}


	public int getCurrentScore() {
		return currentScore;
	}

	public void setCurrentScore(int currentScore) {
		this.currentScore = currentScore;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	
	public Cell getCurrentPosition() {
		synchronized (this.currentPosition) {
			return this.currentPosition;
		}
		
	}
	
	public synchronized void setCurrentPosition(Cell position){
		
		this.currentPosition = position;
		
		
	}

	public synchronized void setNewPosition(int row, int col) {
	
		this.currentPosition.setPosition(row, col);
		
		
	}
	/** yields a copy of the ports for the broadcast */
	public List<Integer> extractPlayersPorts(){
		synchronized (this.currentGame) {
			TreeMap<Integer, Player> players = this.currentGame.getPlayers().getUsersMap();
			players.remove(this.currentPlayer.getId());/** it's a copy I can do that */
			List<Integer> ports = new ArrayList<>();
			players.forEach((id,pl) -> ports.add(pl.getPort()));
			return ports;
		}
			
	}
	
	/** methods to add and delete a player. Work on actual object not a copy. 
	 *  Those methods are only called by the handler */
	public void addNewPlayer(Player p){
		synchronized (this.currentGame) {
			this.currentGame.addPlayerToGame(p);
		}
		
	}
	
	public synchronized void deletePlayer(Player p){
		
		this.currentGame.deletePlayerFromGame(p);
		
	}

}
