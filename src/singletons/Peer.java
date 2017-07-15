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
/**
 * This is a singleton that contains any details about the player and the game
 * it has been inserted in. This avoids the burden to pass the various argument to
 * every thread.
 */
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
	
	/**
	 * Closes the server socket.
	 * This method is called during the exit procedure
	 */
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


	public synchronized int getCurrentScore() {
		return currentScore;
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
	
	/**
	 * The same as setCurrentPosition but with the new coordinates
	 * @param row number
	 * @param column number
	 */
	public synchronized void setNewPosition(int row, int col) {
	
		this.currentPosition.setPosition(row, col);
		
		
	}
	
	public synchronized void updateMapPlayers(TreeMap<Integer, Player> map){
		this.currentGame.setUserMap(map);
	}
	
	/**
	 * Methods to add and delete a player. Work on actual object not a copy. 
	 * Those methods are only called by the handler.  
	 */
	public void addNewPlayer(Player p){
		synchronized (this.currentGame) {
			this.currentGame.addPlayerToGame(p);
		}
		
	}
	
	/**
	 * Adds a new client socket and the streams to the ConnectedSockets list
	 * @param the Id of the player to which a connection has been made
	 * @param the connection data.
	 */
	public synchronized void addConnectedSocket(Integer playerId, ConnectionData data){
		this.clientConnections.put(playerId, data);
	}
	
	/**
	 * deletes a closed socket from the list.
	 * @param the Id of the player that closed his server socket
	 */
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
	
	/**
	 * @return the list of all client sockets connected
	 */
	public synchronized List<ConnectionData> getClientConnectionsList(){
		//it's a copy
		return new ArrayList<ConnectionData>(this.clientConnections.values());
	}
	
	/**
	 * deletes a player from the current game
	 * @param the player to be deleted
	 */
	public synchronized void deletePlayer(Player p){
		
		this.currentGame.deletePlayerFromGame(p);
		
	}
	
	/**
	 * @return the number of the players of the current game
	 */
	public int getNumberOfPlayers(){
		
		return this.getCurrentGame().retrievePlayersNumber();
	}
	
	/**
	 * This method finds the next peer in the ring to which pass the token 
	 * or to which ask to be inserted in the ring
	 * @param the map of all active players of the game
	 * @return the next player in the ring
	 */
	public Player getNextPeer(Players players) {
		//it's a copy
		TreeMap<Integer, Player> gamePlayers = players.getUsersMap(); 
		
		if (gamePlayers.size() > 1) {
			Integer nextPlayerKey = findNextPlayerKey(this.getCurrentPlayer().getId(), gamePlayers);
			return gamePlayers.get(nextPlayerKey);
		} else {
			// no more players, only me in the map
			return null; 

		}
	}

	private Integer findNextPlayerKey(Integer key, TreeMap<Integer, Player> gamePlayers) {
		Integer nextKey = null;
		if ((nextKey = gamePlayers.higherKey(key)) == null)
			return gamePlayers.firstKey();
		else
			return nextKey;
	}
	
	/**
	 * @return a copy of the current userMap of all players
	 */
	public synchronized TreeMap<Integer, Player> getUserMap() {
		
		return this.getCurrentGame().getPlayers().getUsersMap();
	}
	
	/**
	 * Increments current score when the peer has killed someone else
	 */
	public synchronized void incrementCurrentScore() {
		this.currentScore++;
		
	}
	
	/**
	 * retrieve the connection data from the list given the player Id
	 * @param the Id of the player to contact
	 * @return the connected socket and the streams
	 */
	public ConnectionData getClientConnectionById(int id) {
		return this.clientConnections.get(id);
	}

}
