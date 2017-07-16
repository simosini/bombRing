package beans;

import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
 
/** 
 * This class contains all the details of a game.
 * A constructor with a player argument is provided in order to
 * build a game with one player whenever a game is created. 
 */
public class Game {

	private String name;
	private int sideLength;
	private int scoreNeeded;
	private Players players; // players map of the game

	public Game() {}
	
	public Game(final String name, final int length, final int score) {
		this.setName(name);
		this.setSideLength(length);
		this.setScore(score);
		this.setPlayers(new Players());
	}
		
	/**
	 * create a game inserting the player passed in the map (Players)
	 * @param name of the game
	 * @param length of the side of the grid
	 * @param score needed to win the game
	 * @param the player who created the game
	 */
	public Game(final String name, final int length, final int score, Player p) {
		this.setName(name);
		this.setSideLength(length);
		this.setScore(score);
		this.setPlayers(initList(p));
	}
	
	/**
	 * this method is only called to initialize the newly created Game
	 * @param the player who created the game
	 * @return a map initialized with the given player
	 */
	private Players initList(Player p) {
		Players players = new Players();
		players.addPlayer(p);
		return players;
	}
	
	/**
	 * create a copy of the given game
	 * @param game to be copied
	 */
	public Game(Game game) {
		this.setName(game.getName());
		this.setSideLength(game.getSideLength());
		this.setScore(game.getScoreNeeded());
		this.setPlayers(game.getPlayers());
	}
	
	/**
	 * setters and getters: get returns a copy when needed 
	 */
	
	public void setName(String name) {
		this.name = name;
	}

	public void setSideLength(int l) {
		this.sideLength = l;
	}

	public void setScore(int s) {
		this.scoreNeeded = s;
	}
	
	public synchronized void setPlayers(Players players) {
		this.players = players;
	}
	
	public synchronized void setUserMap(TreeMap<Integer, Player> map) {
		this.players.setUsersMap(map);
		
	}

	public String getName() {
		return name;
	}

	public int getSideLength() {
		return sideLength;
	}

	public int getScoreNeeded() {
		return scoreNeeded;
	}
	
	/**
	 * @return a copy of the map of game's players
	 */
	public synchronized Players getPlayers() {
		return new Players(players);

	}

	/**
	 * the same as getPlayers but returns a list
	 * @return a copy of the players of a game as a list 
	 */
	public synchronized List<Player> retrieveGamePlayers() {
 		return getPlayers().retrievePlayersList();
	}
	
	/**
	 * add a player to the current game
	 * @param the player to be added to the current game
	 */
	public synchronized void addPlayerToGame(Player p) {
		players.addPlayer(p);
	}
		
	/**
	 * delete the given player from the game. If it does not exists nothing happens
	 * @param the player to be deleted from the current game 
	 */
	public synchronized void deletePlayerFromGame(Player p) {
		players.deletePlayer(p);	
	}

	/**
	 * @return the number of players in the game 
	 */
	public synchronized int retrievePlayersNumber() {
		return this.players.size();
	}
	
	/**
	 * check existence of a player in the map
	 * @param the player to look for
	 * @return true if the map contains the given player
	 */
	public boolean contains(Player p) {
		return this.retrieveGamePlayers().contains(p);
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder("GAME NAME: " + this.getName() + "\n");
		sb.append("DIMENSION: " + this.getSideLength() + "\n");
		sb.append("SCORE TO WIN: " + this.getScoreNeeded() + "\n");
		sb.append("NUMBER OF PLAYERS: " + this.retrievePlayersNumber() + "\n");
		
		String players = this.buildPlayersList();
				 		 
		sb.append("PLAYERS NAMES: " + players + "\n");		 		 
		return sb.toString();
	}

	private String buildPlayersList() {
		return this.getPlayers().retrievePlayersList()
						 		.stream()
						 		.map(player -> player.getNickname())
						 		.collect(Collectors.joining(", "));
	}

	

}