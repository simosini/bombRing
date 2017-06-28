package peer;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import beans.Game;
import beans.Player;

public class Peer {

	private static final String BASE_URI = Uri.BASE_URI.getPath();

	private Game currentGame;
	private Player currentPlayer;
	private int currentScore;
	private boolean isAlive;
	private Cell currentPosition;

	public Peer() {
		initPeer();
	}

	private void initPeer() {
		this.setAlive(false);
		this.setCurrentScore(0);
	}

	/** getters and setters method */

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

	public void setCurrentPlayer(Player currentPlayer) {
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

	/**
	 * no need to sync cause the only two threads that uses this are already
	 * sync using wait and notify. The same for setCurrentPosition
	 */
	public Cell getCurrentPosition() {
		return this.currentPosition;
	}
	
	public void setCurrentPosition(Cell position){
		this.currentPosition = position;
	}

	public void setNewPosition(int row, int col) {
		this.currentPosition.setPosition(row, col);
	}
	/** yields a copy of the ports for the broadcast */
	public List<Integer> extractPlayersPorts(){
		TreeMap<Integer, Player> players = this.currentGame.getPlayers().getUsersMap();
		players.remove(this.currentPlayer.getId());/** it's a copy I can do that */
		List<Integer> ports = new ArrayList<>();
		players.forEach((id,pl) -> ports.add(pl.getPort()));
		return ports;
		
	}
	
	/** methods to add and delete a player. Work on actual object not a copy. 
	 *  Those methods are only called by the handler */
	public void addNewPlayer(Player p){
		this.currentGame.addPlayerToGame(p);
	}
	
	public void deletePlayer(Player p){
		this.currentGame.deletePlayerFromGame(p);
	}

	/** client methods to interact with rest server */
	
	// default client configuration
	private Client configureRestClient() {
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		Client client = Client.create(clientConfig);
		return client;
	}

	// adds a game to the server
	public Game addGame(Game game) {
		Game addedGame = null; // to save the game returned by the server
		try {
			Client client = configureRestClient();
			WebResource wr = client.resource(BASE_URI).path(Uri.ADD_GAME.getPath());

			// POST method
			ClientResponse response = wr.accept(MediaType.APPLICATION_JSON)
										 .type(MediaType.APPLICATION_JSON)
										 .post(ClientResponse.class, game);
			
			if (response.getStatus() != 200) {
				throw new RuntimeException(
						"Failed : " + response.getStatusInfo() + ". Reason: " + response.getEntity(String.class));
			}
			
			addedGame = response.getEntity(Game.class);
			
		} catch (RuntimeException re) {
			System.out.println(re.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return addedGame;

	}
	
	// adds a player to the game if the game exists
	public Game addPlayerToGame(String gameName, Player player) {
		Game updatedGame = null;
		try {
			Client client = configureRestClient();
			WebResource wr = client.resource(BASE_URI).path(Uri.ADD_PLAYER.getPath()).path(gameName);
			
			// PUT method
			ClientResponse response = wr.accept(MediaType.APPLICATION_JSON)
										.type(MediaType.APPLICATION_JSON)
										.put(ClientResponse.class,player);

			if (response.getStatus() != 200) {
				throw new RuntimeException(
						"Failed: Conflict. Reason: " + response.getEntity(String.class));
			}
			
			updatedGame = response.getEntity(Game.class);

		} catch (RuntimeException re) {
			System.out.println(re.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return updatedGame;
	}
	
	//deletes the player from the game if the game exists
	public Game deletePlayerFromGame(String gameName, Player player) {
		Game updatedGame = null;
		try {
			Client client = configureRestClient();
			WebResource wr = client.resource(BASE_URI).path(Uri.DELETE_PLAYER.getPath()).path(gameName);
			
			// PUT method
			ClientResponse response = wr.accept(MediaType.APPLICATION_JSON)
										.type(MediaType.APPLICATION_JSON)
										.put(ClientResponse.class, player);

			if (response.getStatus() != 200) {
				throw new RuntimeException(
						"Failed: Conflict. Reason: " + response.getEntity(String.class));
			}
			
			updatedGame = response.getEntity(Game.class);

		} catch (RuntimeException re) {
			System.out.println(re.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return updatedGame;
	}
	
	// prints the list of all active players in the application
	public void retrieveAllPlayers() {
		try {
			Client client = configureRestClient();
			WebResource wr = client.resource(BASE_URI).path(Uri.GET_ALL_PLAYERS.getPath());
			
			// GET method
			ClientResponse response = wr.accept(MediaType.APPLICATION_JSON)
										.get(ClientResponse.class);
			
			if (response.getStatus() != 200) {
				throw new RuntimeException(
						"Failed: " + response.getStatus() + ". Reason: " + response.getEntity(String.class));
			}

			final ObjectMapper mapper = new ObjectMapper();
			List<Player> players = mapper.readValue(response.getEntity(String.class),
					new TypeReference<List<Player>>() { });
			
			if (players.size() != 0) {
				System.out.println("This is the list of all players:");
				players.forEach(el -> System.out.println(el.getNickname()));
				System.out.println();
			} 
			else
				System.out.println("There are no active players\n");

		} catch (RuntimeException re) {
			System.out.println(re.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	// prints the players of the game given if the game exists
	public void retrievePlayers(String gameName) {
		try {
			Client client = configureRestClient();
			WebResource wr = client.resource(BASE_URI)
								   .path(Uri.GET_PLAYERS.getPath())
								   .path(gameName);
			ClientResponse response = wr.accept(MediaType.APPLICATION_JSON)
										.get(ClientResponse.class);
			
			if (response.getStatus() != 200) {
				throw new RuntimeException(
						"Failed : " + response.getStatusInfo() + ". Reason: " + response.getEntity(String.class));
			}
			
			final ObjectMapper mapper = new ObjectMapper();
			List<Player> players = mapper.readValue(response.getEntity(String.class),
					new TypeReference<List<Player>>() { });
			
			if (players.size() != 0) {
				System.out.println("This is the list of the players of "+ gameName + " :");
				players.forEach(el -> System.out.println(el.getNickname()));
				System.out.println("\n");
			} 
			else
				System.out.println("There are no active players\n");
			

		} catch (RuntimeException re) {
			System.out.println(re.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	// prints the game details if the game exists
	public void retrieveGameInfo(String gameName) {
		try {
			Client client = configureRestClient();
			WebResource wr = client.resource(BASE_URI)
								   .path(Uri.GET_GAME.getPath())
								   .path(gameName);
			
			ClientResponse response = wr.accept(MediaType.APPLICATION_JSON)
										.get(ClientResponse.class);
			
			if (response.getStatus() != 200) {
				throw new RuntimeException(
						"Failed : " + response.getStatusInfo() + ". Reason: " + response.getEntity(String.class));
			}
			
			//final ObjectMapper mapper = new ObjectMapper();
			//String s = response.getEntity(String.class);
			//Game game = mapper.readValue(s, Game.class);
			Game game = response.getEntity(Game.class);
			System.out.println(game + "\n");

		} catch (RuntimeException re) {
			System.out.println(re.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	// prints the names of all available games
	public void retrieveGames() {
		try {
			Client client = configureRestClient();
			WebResource wr = client.resource(BASE_URI)
								   .path(Uri.GET_GAMES.getPath());
			
			ClientResponse response = wr.accept(MediaType.APPLICATION_JSON)
										.get(ClientResponse.class);
			
			if (response.getStatus() != 200) {
				throw new RuntimeException(
						"Failed: " + response.getStatus() + ". Reason: " + response.getEntity(String.class));
			}

			final ObjectMapper mapper = new ObjectMapper();
			List<Game> games = mapper.readValue(response.getEntity(String.class), new TypeReference<List<Game>>() {	});
			
			if (games.size() == 0)
				System.out.println("There are no active games at the moment!");
			else {
				for (Game g : games) {
					System.out.println("Name : " + g.getName());
				}
				System.out.println();
			}

		} catch(RuntimeException re){
			System.out.println(re.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	

}
