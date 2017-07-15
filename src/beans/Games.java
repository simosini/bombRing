package beans;

import java.util.ArrayList;
import java.util.List;

import com.sun.jersey.api.NotFoundException;

/** 
 * This class is used by the REST server to create a single instance of the list of 
 * currently active games. A game with no players can not exist. When the last player of a
 * game leaves or when the game is finished, it's immediately deleted from this list.
 * Games names must be unique, and players with the same name cannot be in the same game. 
 */
public class Games {

	private List<Game> gamesList;
	private static Games instance; 

	private Games() {
		setGamesList(new ArrayList<>());
	}

	/** 
	 * singleton in order to have only one list of games 
	 */
	public static synchronized Games getInstance() {
		if (instance == null)
			instance = new Games();
		return instance;
	}
	
	/** 
	 * return a copy of the list of games 
	 */
	public synchronized List<Game> getGamesList() {
		// yield a copy for synchronization
		return new ArrayList<>(gamesList);

	}

	public synchronized void setGamesList(List<Game> gameslist) {
		this.gamesList = gameslist;
	}

	/** 
	 * Add a game to the list.
	 * It cannot work on a copy cause need to change the original list. 
	 */
	public synchronized void addGame(Game g) {

		for (Game game : this.gamesList)
			if (game.getName().equalsIgnoreCase(g.getName()))
				throw new IllegalArgumentException("Game name must be unique! Please try again");
		gamesList.add(g);

	}

	/** 
	 *  Add a player to the game passed as argument. 
	 *  This must update the games list, cannot use a copy. 
	 */
	public synchronized void addPlayer(String gameName, Player p) {
		
		// first check the game still exists
		Game g = this.getByName(gameName);
		if (g == null)
			throw new IllegalArgumentException(
					"Game " + gameName + " does not exists anymore or you input the wrong name! Please try again");

		// now check a player with the same name is not in the game
		String playerName = p.getNickname();
		if(!playerExists(g, playerName)){

			// g is a copy need the original list to change it
			for (Game game : this.gamesList)
				if (game.getName().equalsIgnoreCase(gameName)){
					game.addPlayerToGame(p);
					break;
				}
		}
	}
	
	/** 
	 * check existence of a player in a game 
	 */
	private boolean playerExists(Game g, String playerName) {
		for (Player player : g.getPlayers().retrievePlayersList()) {
			if (player.getNickname().equalsIgnoreCase(playerName))
				throw new IllegalArgumentException(
						"A player called " + playerName + " is already playing! Please choose another game!");
		}
		return false;
	}
	
	/** 
	 * yield a game given its name. 
	 * If the game does not exist return null. 
	 */
	public synchronized Game getByName(String name) {
		List<Game> gamesCopy = getGamesList();

		for (Game g : gamesCopy)
			if (g.getName().equalsIgnoreCase(name))
				return g;

		return null;
	}
	
	/** 
	 * delete the given player from the game passed as parameter.
	 * If the player does not exist it does nothing 
	 */
	public synchronized void deletePlayer(String gameName, Player p) {
		// need to check the game still exists
		Game g = this.getByName(gameName);
		if (g == null) // should never get here
			throw new IllegalArgumentException(
					"Game" + gameName + " does not exists anymore. Cannot remove player!");
		
		// no need to check existence: if the player does not exist nothing is done
		// g is a copy need the original list to change it
		for (Game game : this.gamesList)
			if (game.getName().equalsIgnoreCase(gameName)){
				try{
					game.deletePlayerFromGame(p);
					break;
				}
				catch(IllegalArgumentException e){
					throw new NotFoundException(e.getMessage());
				}
			}		
	}
	
	/** 
	 * delete a game given its name. 
	 * This method is only called by the REST server.
	 */
	public synchronized void deleteGame(String gameName) {
		// need to check the game still exists
		Game g = this.getByName(gameName);
		if (g == null) // the game is not in the list
			throw new IllegalArgumentException(
				"Game" + gameName + " does not exists anymore. Cannot remove it!");
		
		// g is a copy need the original list to change it
		for (Game game : this.gamesList)
			if (game.getName().equalsIgnoreCase(gameName)){
				try{
					this.gamesList.remove(game);
					break;
				}
				catch(IllegalArgumentException e){
					throw new NotFoundException(e.getMessage());
				}
			}
		
	}
	

}
