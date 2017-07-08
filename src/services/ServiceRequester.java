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

public class ServiceRequester {
	
	private static final String BASE_URI = Uri.BASE_URI.getPath();
	
	public ServiceRequester(){}
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
	public void deletePlayerFromGame(String gameName, Player player) {
		//Game updatedGame = null;
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
			
			//updatedGame = response.getEntity(Game.class);

		} catch (RuntimeException re) {
			System.out.println(re.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//return updatedGame;
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
