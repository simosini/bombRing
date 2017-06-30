package singletons;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
	private HashMap<String, Socket> clientSocket;

	private Peer() {}

	/** getters and setters method */
	
	
	public synchronized Game getCurrentGame() {
		// yields a copy
		return new Game(this.currentGame);
	}

	public void setCurrentGame(Game currentGame) {
		this.currentGame = currentGame;
	}

	public synchronized Player getCurrentPlayer() {
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

	public synchronized boolean isAlive() {
		return isAlive;
	}

	public synchronized void setAlive(boolean isAlive) {
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
	
	/** methods to add and delete a player. Work on actual object not a copy. 
	 *  Those methods are only called by the handler */
	public void addNewPlayer(Player p){
		synchronized (this.currentGame) {
			this.currentGame.addPlayerToGame(p);
		}
		
	}
	
	public synchronized void addSocket(String playerName, Socket s){
		this.clientSocket.put(playerName, s);
	}
	
	public synchronized void deleteSocket(String playerName){
		if (this.clientSocket.containsKey(playerName)){
			this.clientSocket.remove(playerName);
		}
	}
	
	public synchronized void setClientSockets(HashMap<String, Socket> map){
		this.clientSocket.clear();
		this.clientSocket = map;
	}
	
	public synchronized List<Socket> getSocketList(){
		//it's a copy
		return new ArrayList<Socket>(this.clientSocket.values());
	}
	
	public synchronized void deletePlayer(Player p){
		
		this.currentGame.deletePlayerFromGame(p);
		
	}
	
	public int getNumberOfPlayers(){
		
		return this.getCurrentGame().retrievePlayersNumber();
	}

}
