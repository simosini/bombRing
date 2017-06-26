package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.List;
import java.util.TreeMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;

import beans.Game;
import beans.Player;

public class GameAdder {

	private static final String BASE_URI = "http://localhost:8080/restConverter/rest/services/";
	private final String r1 = "addgame";
	private final String r2 = "getgames";
	private final String r3 = "getgame/";
	private final String r4 = "addplayer/";
	private final String r5 = "deleteplayer/";
	private Player player;
	private Game currentGame;
	private ServerSocket srvSocket;

	public GameAdder() {
		try {
			this.setSrvSocket(new ServerSocket(0));
		} catch (IOException ie) {
			System.out.println("Error creating server socket");
		}

	}

	public static void main(String[] args) {

		printGameMenu(new GameAdder());
	}

	private void setPlayer(Player p) {
		this.player = p;

	}

	private synchronized Player getPlayer() {

		return new Player(player);
	}

	private void setCurrentGame(Game g) {
		this.currentGame = g;
	}

	private synchronized Game getCurrentGame() {

		return new Game(currentGame);
	}

	private synchronized ServerSocket getSrvSocket() {
		return srvSocket;
	}

	public void setSrvSocket(ServerSocket srvSocket) {
		this.srvSocket = srvSocket;
	}

	public void closeSrvSocket(ServerSocket srvSocket) {
		try {
			srvSocket.close();
		} catch (IOException e) {
			System.out.println("Error closing socket!");
		}
	}

	private static void printGameMenu(GameAdder ga) {

		Emoji emoji = EmojiManager.getForAlias("bomb");
		System.out.println(emoji.getUnicode() + emoji.getUnicode() + " Welcome to Bomb Ring " + emoji.getUnicode()
				+ emoji.getUnicode());

		InputStreamReader r = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(r);

		Player p = new Player();
		try {
			System.out.println("Please insert your name");
			String name = br.readLine();
			System.out.println("Please insert your surname");
			String surname = br.readLine();
			System.out.println("Please pick a nickname");
			String nickname = br.readLine();
			p.setName(name);
			p.setSurname(surname);
			p.setNickname(nickname);
			p.setId(p.hashCode());
			p.setPort(ga.getSrvSocket().getLocalPort());
		} catch (IOException e) {
			e.printStackTrace();
		}

		ga.setPlayer(p);

		int choice = 0;
		boolean exit = false;
		while (!exit) {
			System.out.println("Please select one of the option below: \n" + "1. Show current games;\n"
					+ "2. Create a new game;\n" + "3. Join a running game;\n" + "4. Show game info;\n" + "5. Exit;\n");
			try {
				choice = Integer.parseInt(br.readLine());

			} catch (IOException | NumberFormatException e) {
				choice = 6;
			}
			switch (choice) {
			case 1:
				System.out.println("This is the list of current active games:");
				try {
					ga.getGames();
					ga.getAllPlayers();
				} catch (RuntimeException e) {
					System.out.println(e.getMessage());
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case 2:
				try {
					System.out.println("Select a name for the game: ");
					String name = br.readLine();
					System.out.println("Select the length for the grid: ");
					int length = Integer.parseInt(br.readLine());
					System.out.println("Select the number of points to win: ");
					int points = Integer.parseInt(br.readLine());
					Game currentGame = ga.addGame(new Game(name, length, points, ga.getPlayer()));
					if (currentGame == null)
						break;
					ga.setCurrentGame(currentGame);
					//playGame(ga, br);
					ga.closeSrvSocket(ga.getSrvSocket());
					exit = true;

				} catch (IOException | NumberFormatException e) {
					System.out.println("Please insert correct values!");
				}
				break;

			case 3: /** save game returned by the server when you choose it */
				Game currentGame = new Game();
				try {
					System.out.println("Enter game name: ");
					String gameName = br.readLine();

					currentGame = ga.addPlayerToGame(gameName, ga.getPlayer());
					if (currentGame == null)
						break;
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
				System.out.println("Player added correctly!");
				ga.setCurrentGame(currentGame);
				playGame(ga, br);
				exit = true;
				break;
				
			case 4:
				try {
					System.out.println("Enter game name: ");
					String gameName = br.readLine();
					ga.getGame(gameName);
					break;
					
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}

			case 5:
				System.out.println("Leaving the game. Goodbye!");
				ga.closeSrvSocket(ga.getSrvSocket());
				System.out.println("Server socket closed!");
				exit = true;
				break;

			default:
				System.out.println("Please select a number between 1 and 4");
				break;
			}
		}
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void getAllPlayers() {
		try {
			Client client = Client.create();
			WebResource wr = client.resource(BASE_URI).path("getallplayers");
			ClientResponse response = wr.accept("application/json").get(ClientResponse.class);
			if (response.getStatus() != 200) {
				throw new RuntimeException(
						"Failed: " + response.getStatus() + ". Reason: " + response.getEntity(String.class));
			}

			final ObjectMapper mapper = new ObjectMapper();
			List<Player> players = mapper.readValue(response.getEntity(String.class),
					new TypeReference<List<Player>>() {
					});
			if (players.size() != 0) {
				System.out.println("This is the list of all players:");
				players.forEach(el -> System.out.println(el.getNickname()));
			} else
				System.out.println("There are no active players\n");

		} catch (RuntimeException re) {
			System.out.println(re.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void playGame(GameAdder ga, BufferedReader br) {

		if (ga.getCurrentGame().getName() != null) {
			try {
				
				System.out.println("\nHi " + ga.getPlayer().getNickname() + "! You are currently playing in game "
						+ ga.getCurrentGame().getName() + " and those are current active players: "); 
				ga.JoinRingProcedure();
				System.out.println("Insert a move: ");
				System.out.println("Moving " + br.readLine());
				// this yields a game
				ga.deletePlayerFromGame(ga.getCurrentGame().getName(), ga.getPlayer());
				ga.closeSrvSocket(ga.getSrvSocket());
				br.close();
				System.out.println("Server socket closed!");
				System.out.println("Leaving the game!");
			} catch (IOException ie) {
				ie.printStackTrace();
			}

		} else
			System.out.println("Problem joining game! Please restart the application!");
	}

	private Game addPlayerToGame(String gameName, Player player) {
		Game g = null;
		try {
			ClientConfig clientConfig = new DefaultClientConfig();
			clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
			Client client = Client.create(clientConfig);

			WebResource wr1 = client.resource(BASE_URI).path(r4).path(gameName);
			// PUT method
			ClientResponse response = wr1.accept("application/json").type("application/json").put(ClientResponse.class,
					player);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed: Conflict" + ". Reason: " + response.getEntity(String.class));
			}
			// check if the player has been added correctly
			g = response.getEntity(Game.class);

		} catch (RuntimeException re) {
			System.out.println(re.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return g;
	}

	private void deletePlayerFromGame(String gameName, Player player) {

		try {
			ClientConfig clientConfig = new DefaultClientConfig();
			clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
			Client client = Client.create(clientConfig);

			WebResource wr1 = client.resource(BASE_URI).path(r5).path(gameName);
			// DELETE method
			ClientResponse response = wr1.accept("application/json").type("application/json").put(ClientResponse.class,
					player);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed: Conflict" + ". Reason: " + response.getEntity(String.class));
			}
			System.out.println(response.getEntity(String.class));

		} catch (RuntimeException re) {
			System.out.println(re.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getGame(String name) {
		try {
			Client client = Client.create();
			WebResource wr = client.resource(BASE_URI).path(r3).path(name);
			ClientResponse response = wr.accept("application/json").get(ClientResponse.class);
			if (response.getStatus() != 200) {
				throw new RuntimeException(
						"Failed : " + response.getStatusInfo() + ". Reason: " + response.getEntity(String.class));
			}
			final ObjectMapper mapper = new ObjectMapper();
			String s = response.getEntity(String.class);
			Game game = mapper.readValue(s, Game.class);
			System.out.println(game + "\n");

		} catch (RuntimeException re) {
			System.out.println(re.getMessage());
		} catch (IOException jsonex) {
			jsonex.printStackTrace();
		}

	}

	private void getGames() {
		try {
			Client client = Client.create();
			WebResource wr = client.resource(BASE_URI).path(r2);
			ClientResponse response = wr.accept("application/json").get(ClientResponse.class);
			if (response.getStatus() != 200) {
				throw new RuntimeException(
						"Failed: " + response.getStatus() + ". Reason: " + response.getEntity(String.class));
			}

			final ObjectMapper mapper = new ObjectMapper();
			List<Game> games = mapper.readValue(response.getEntity(String.class), new TypeReference<List<Game>>() {
			});
			if (games.size() == 0)
				System.out.println("There are no active games at the moment!");
			else {
				for (Game g : games) {
					System.out.println("Name : " + g.getName());
				}
				System.out.println();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Game addGame(Game game) {
		Game addedGame = null;
		try {
			ClientConfig clientConfig = new DefaultClientConfig();
			clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
			Client client = Client.create(clientConfig);

			WebResource wr1 = client.resource(BASE_URI + r1);

			// POST method
			ClientResponse response = wr1.accept("application/json").type("application/json").post(ClientResponse.class,
					game);
			if (response.getStatus() != 200) {
				throw new RuntimeException(
						"Failed : " + response.getStatusInfo() + ". Reason: " + response.getEntity(String.class));
			}
			System.out.println("Game added correctly!");
			addedGame = response.getEntity(Game.class);
		} catch (RuntimeException re) {
			System.out.println(re.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return addedGame;

	}
	
	private void JoinRingProcedure(){
		
		Player myself = this.getPlayer();
		TreeMap<Integer, Player> gamePlayers = this.getCurrentGame().getPlayers().getUsersMap();
		Player nextPeer = this.getNextPeer(myself, gamePlayers);
		//if (nextPeer == null)
			//cannot be added to the game so exit to the previous menu
		// now send a message to his server socket
	}

	private Player getNextPeer(Player myself, TreeMap<Integer, Player> gamePlayers) {
		
		if(gamePlayers.size() > 1){
			Integer nextPlayerKey = findNextPlayerKey(myself.getId(), gamePlayers);
			return gamePlayers.get(nextPlayerKey);
		}
		else {
			return null; // no more players to ask, only me in the map
			
		}
	}

	private Integer findNextPlayerKey(Integer key, TreeMap<Integer, Player> gamePlayers) {
		Integer nextKey = null;
		if ((nextKey = gamePlayers.higherKey(key)) == null)
			return gamePlayers.firstKey();
		else
			return nextKey;		
	}

}
