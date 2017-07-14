package beans;

import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
 
/** This class contains all the details of a game. */
public class Game {

	private String name;
	private int sideLength;
	private int scoreNeeded;
	private Players players; // players map of the game

	public Game() {}
	
	public Game(String name, int length, int score) {
		this.setName(name);
		this.setSideLength(length);
		this.setScore(score);
		this.setPlayers(new Players());
	}
	
	/** create a game inserting the player passed in the map (Players) */
	public Game(String name, int length, int score, Player p) {
		this.setName(name);
		this.setSideLength(length);
		this.setScore(score);
		this.setPlayers(initList(p));
	}
	
	/** this method is only called to initialize the newly created Game */
	private Players initList(Player p) {
		Players players = new Players();
		players.addPlayer(p);
		return players;
	}
	
	/** create a copy of the given game */
	public Game(Game game) {
		this.setName(game.getName());
		this.setSideLength(game.getSideLength());
		this.setScore(game.getScoreNeeded());
		this.setPlayers(game.getPlayers());
	}
	

	/** setters and getters: get returns a copy when needed */
	
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
	
	/** return a copy of the map of game's players */
	public synchronized Players getPlayers() {
		return new Players(players);

	}
	
	/** helper methods */

	/** retrieve a copy of the players of a game as a list */
	public synchronized List<Player> retrieveGamePlayers() {
 
		return getPlayers().retrievePlayersList();
	}
	
	/** add a player to the current game */
	public synchronized void addPlayerToGame(Player p) {
		
		players.addPlayer(p);
	}
	
	
	/** delete the given player from the game. If it does not exists nothing happens */
	public synchronized void deletePlayerFromGame(Player p) {
		players.deletePlayer(p);	
		
	}
	
	/** return the number of players in the game */
	public synchronized int retrievePlayersNumber() {
		return this.players.size();
	}

	public boolean contains(Player p) {
		return this.retrieveGamePlayers().contains(p);
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder("GAME NAME: " + this.getName() + "\n");
		sb.append("DIMENSION: " + this.getSideLength() + "\n");
		sb.append("SCORE TO WIN: " + this.getScoreNeeded() + "\n");
		sb.append("NUMBER OF PLAYERS: " + this.retrievePlayersNumber() + "\n");
		
		String players = buildPlayersList();
				 		 
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