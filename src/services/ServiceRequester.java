package services;

import java.util.List;

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
import peer.Uri;

/**
 * This class contains all the necessary methods to interact with the REST server.
 * So basically this class represents the client side of the REST communication. 
 */
public class ServiceRequester {
	
	private static final String BASE_URI = Uri.BASE_URI.getPath();
	
	public ServiceRequester(){}

	/** 
	 * client methods to interact with the REST server 
	 */
	
	/**
	 * Default client configuration.
	 * @return a client configured to interact with the REST server
	 */
	private Client configureRestClient() {
		final ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		final Client client = Client.create(clientConfig);
		return client;
	}

	/**
	 * Adds a game to the REST server.
	 * @param the game to be added
	 * @return A copy of the newly inserted game
	 */
	public Game addGame(Game game) {
		
		// to save the game returned by the server
		Game addedGame = null;
		try {
			final Client client = configureRestClient();
			final WebResource wr = client.resource(BASE_URI).path(Uri.ADD_GAME.getPath());

			// POST method
			final ClientResponse response = wr.accept(MediaType.APPLICATION_JSON)
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
	
	/**
	 * Adds a player to the game passed if the game exists
	 * @param the name of the game where to add the player
	 * @param the player to be added
	 * @return the updated game returned by the REST server which is a copy of the original one
	 */
	public Game addPlayerToGame(String gameName, Player player) {
		Game updatedGame = null;
		try {
			final Client client = configureRestClient();
			final WebResource wr = client.resource(BASE_URI).path(Uri.ADD_PLAYER.getPath()).path(gameName);
			
			// PUT method
			final ClientResponse response = wr.accept(MediaType.APPLICATION_JSON)
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
	
	/**
	 * Deletes the player given from the game if the game exists
	 * @param the name of the game where to delete the player
	 * @param the player to be deleted
	 * If the operation is handled correctly nothing is returned
	 */
	public void deletePlayerFromGame(String gameName, Player player) {
		
		try {
			final Client client = configureRestClient();
			final WebResource wr = client.resource(BASE_URI).path(Uri.DELETE_PLAYER.getPath()).path(gameName);
			
			// PUT method
			final ClientResponse response = wr.accept(MediaType.APPLICATION_JSON)
											  .type(MediaType.APPLICATION_JSON)
											  .put(ClientResponse.class, player);

			if (response.getStatus() != 200) {
				throw new RuntimeException(
						"Failed: Conflict. Reason: " + response.getEntity(String.class));
			}

		} catch (RuntimeException re) {
			System.out.println(re.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 *  prints the list of all active players in the application.
	 */
	public void retrieveAllPlayers() {
		try {
			final Client client = configureRestClient();
			final WebResource wr = client.resource(BASE_URI).path(Uri.GET_ALL_PLAYERS.getPath());
			
			// GET method
			final ClientResponse response = wr.accept(MediaType.APPLICATION_JSON)
											  .get(ClientResponse.class);
			
			if (response.getStatus() != 200) {
				throw new RuntimeException(
						"Failed: " + response.getStatus() + ". Reason: " + response.getEntity(String.class));
			}
			
			// retrieve the list from the response
			final ObjectMapper mapper = new ObjectMapper();
			final List<Player> players = mapper.readValue(response.getEntity(String.class),
					new TypeReference<List<Player>>() { });
			
			// print the list of players
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
	
	/**
	 * Prints the list of the players of the game given if the game exists
	 * @param the name of the game to retrieve
	 */
	public void retrievePlayers(String gameName) {
		try {
			final Client client = configureRestClient();
			final WebResource wr = client.resource(BASE_URI)
								   		 .path(Uri.GET_PLAYERS.getPath())
								   		 .path(gameName);
			final ClientResponse response = wr.accept(MediaType.APPLICATION_JSON)
											  .get(ClientResponse.class);
			
			if (response.getStatus() != 200) {
				throw new RuntimeException(
						"Failed : " + response.getStatusInfo() + ". Reason: " + response.getEntity(String.class));
			}
			
			// retrieve list from the response
			final ObjectMapper mapper = new ObjectMapper();
			final List<Player> players = mapper.readValue(response.getEntity(String.class),
					new TypeReference<List<Player>>() { });
			
			// print player list
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
	
	/**
	 * Prints the game details if the game exists
	 * @param the name of the game to retrieve from the REST server
	 */
	public void retrieveGameInfo(String gameName) {
		try {
			final Client client = configureRestClient();
			final WebResource wr = client.resource(BASE_URI)
						 		   		 .path(Uri.GET_GAME.getPath())
						 		   		 .path(gameName);
			
			final ClientResponse response = wr.accept(MediaType.APPLICATION_JSON)
											  .get(ClientResponse.class);
			
			if (response.getStatus() != 200) {
				throw new RuntimeException(
						"Failed : " + response.getStatusInfo() + ". Reason: " + response.getEntity(String.class));
			}
			
			// retrieve the game from the response
			final Game game = response.getEntity(Game.class);
			System.out.println(game + "\n");

		} catch (RuntimeException re) {
			System.out.println(re.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Prints the names of all available games without any details.
	 */
	public void retrieveGames() {
		try {
			final Client client = configureRestClient();
			final WebResource wr = client.resource(BASE_URI)
								   		 .path(Uri.GET_GAMES.getPath());
			
			final ClientResponse response = wr.accept(MediaType.APPLICATION_JSON)
											  .get(ClientResponse.class);
			
			if (response.getStatus() != 200) {
				throw new RuntimeException(
						"Failed: " + response.getStatus() + ". Reason: " + response.getEntity(String.class));
			}
			
			// retrieve the list of games from the response
			final ObjectMapper mapper = new ObjectMapper();
			final List<Game> games = mapper.readValue(response.getEntity(String.class), 
					new TypeReference<List<Game>>() {	});
			
			// print the list
			if (games.size() == 0)
				System.out.println("There are no active games at the moment!");
			else {
				System.out.println("This is the list of current active games:");
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
