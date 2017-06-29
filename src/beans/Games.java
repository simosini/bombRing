package beans;

import java.util.ArrayList;
import java.util.List;

import com.sun.jersey.api.NotFoundException;

public class Games {

	private List<Game> gamesList;
	private static Games instance; // singleton

	private Games() {
		setGamesList(new ArrayList<>());
	}

	// singleton
	public synchronized static Games getInstance() {
		if (instance == null)
			instance = new Games();
		return instance;
	}

	public synchronized List<Game> getGamesList() {
		// yield a copy for synchronization
		return new ArrayList<>(gamesList);

	}

	public synchronized void setGamesList(List<Game> gameslist) {

		this.gamesList = gameslist;
	}

	// cannot work on a copy cause need to change the original list
	public synchronized void addGame(Game g) {

		for (Game game : this.getGamesList())
			if (game.getName().equalsIgnoreCase(g.getName()))
				throw new IllegalArgumentException("Game name must be unique! Please try again");
		gamesList.add(g);

	}

	// this must update the games list, cannot use a copy.
	public synchronized void addPlayer(String gameName, Player p) {
		// need to check the game still exists
		Game g = this.getByName(gameName);
		String playerName = p.getNickname();
		if (g == null)
			throw new IllegalArgumentException(
					"Game " + gameName + " does not exists anymore or you input the wrong name! Please try again");

		// check a player with the same name is not in the game
		if(!playerExists(g, playerName)){

			// g is a copy need the original list to change it
			for (Game game : this.gamesList)
				if (game.getName().equalsIgnoreCase(gameName))
					game.addPlayerToGame(p);
		}
	}

	private boolean playerExists(Game g, String playerName) {
		for (Player player : g.getPlayers().retrievePlayersList()) {
			if (player.getNickname().equalsIgnoreCase(playerName))
				throw new IllegalArgumentException(
						"A player called " + playerName + " is already playing! Please choose another game!");
		}
		return false;
	}

	public synchronized Game getByName(String name) {
		List<Game> gamesCopy = getGamesList();

		for (Game g : gamesCopy)
			if (g.getName().equalsIgnoreCase(name))
				return g;

		return null;
	}
	
	// this must update the games list, cannot use a copy.
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
				}
				catch(IllegalArgumentException e){
					throw new NotFoundException(e.getMessage());
				}
			}		
	}

}
