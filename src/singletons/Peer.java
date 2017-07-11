package singletons;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import beans.Game;
import beans.Player;
import beans.Players;
import peer.Cell;
import peer.ConnectionData;

public class Peer {

	private Game currentGame;
	private Player currentPlayer; 
	private ServerSocket mainSocket;
	private int currentScore = 0;
	private boolean isAlive = false;
	private Cell currentPosition;
	private HashMap<Integer, ConnectionData> clientConnections;
	private static Peer instance;

	private Peer() {}

	/** getters and setters method */
	
	public static synchronized Peer getInstance() {
		if (instance == null)
			instance = new Peer();
		return instance;
	}
	
	public synchronized Game getCurrentGame() {
		// yields a copy
		return new Game(this.currentGame);
	}

	public void setCurrentGame(Game currentGame) {
		this.currentGame = currentGame;
	}
	
	public void setServerSocket(ServerSocket srv){
		this.mainSocket = srv;
	}
	
	public synchronized void closeServerSocket(){
		if (this.mainSocket != null) {
			try {
				this.mainSocket.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
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
	
	public synchronized void updateMapPlayers(TreeMap<Integer, Player> map){
		this.currentGame.setUserMap(map);
	}
	
	/** methods to add and delete a player. Work on actual object not a copy. 
	 *  Those methods are only called by the handler */
	public void addNewPlayer(Player p){
		synchronized (this.currentGame) {
			this.currentGame.addPlayerToGame(p);
		}
		
	}
	
	public synchronized void addConnectedSocket(Integer playerId, ConnectionData data){
		this.clientConnections.put(playerId, data);
	}
	
	public synchronized void deleteConnectedSocket(Integer playerId){
		if (this.clientConnections.containsKey(playerId)){
			this.clientConnections.remove(playerId);
		}
	}
	
	public synchronized void setClientConnections(HashMap<Integer, ConnectionData> map){
		if (this.clientConnections != null)
			this.clientConnections.clear();
		this.clientConnections = map;
	}
	
	public synchronized List<ConnectionData> getClientConnectionsList(){
		//it's a copy
		return new ArrayList<ConnectionData>(this.clientConnections.values());
	}
	
	public synchronized void deletePlayer(Player p){
		
		this.currentGame.deletePlayerFromGame(p);
		
	}
	
	public int getNumberOfPlayers(){
		
		return this.getCurrentGame().retrievePlayersNumber();
	}
	
	public Player getNextPeer(Players players) {
		TreeMap<Integer, Player> gamePlayers = players.getUsersMap(); //it's a copy
		if (gamePlayers.size() > 1) {
			Integer nextPlayerKey = findNextPlayerKey(this.getCurrentPlayer().getId(), gamePlayers);
			return gamePlayers.get(nextPlayerKey);
		} else {
			return null; // no more players, only me in the map

		}
	}

	private Integer findNextPlayerKey(Integer key, TreeMap<Integer, Player> gamePlayers) {
		Integer nextKey = null;
		if ((nextKey = gamePlayers.higherKey(key)) == null)
			return gamePlayers.firstKey();
		else
			return nextKey;
	}

	public synchronized TreeMap<Integer, Player> getUserMap() {
		// yield a copy of the current userMap
		return this.getCurrentGame().getPlayers().getUsersMap();
	}

	public synchronized void incrementCurrentScore() {
		this.currentScore++;
		
	}

	public ConnectionData getClientConnectionById(int id) {
		return this.clientConnections.get(id);
	}

}
