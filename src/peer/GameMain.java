package peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Random;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;

import beans.Game;
import beans.Player;
import messages.JoinRingMessage;
import messages.Message;
import services.ServiceRequester;
import simulator.MeasureBuffer;
import singletons.GameLock;
import singletons.OutQueue;
import singletons.Peer;
import threads.AccelerometerSimulator;
import threads.MessageHandlerThread;
import threads.SensorDataAnalyzer;
import threads.ServerSocketHandler;
import threads.UserInputHandlerThread;

/** 
 * This is the main of the game: everything starts and ends here! 
 */
public class GameMain {
	
	private static final int DEFAULT_PORT = 0;

	public static void main(String[] args) {
		try {
			
			// initialize basic structures and socket for the game 
			final ServerSocket srvSocket = new ServerSocket(DEFAULT_PORT);
			Peer.getInstance().setServerSocket(srvSocket);
			final ServiceRequester service = new ServiceRequester();
			
			// stream for user input
			final BufferedReader readInput = new BufferedReader(new InputStreamReader(System.in));
			
			// set current player 
			final Player newPlayer = getPlayerInfo(readInput);
			newPlayer.setPort(srvSocket.getLocalPort());
			Peer.getInstance().addPlayer(newPlayer);

			// start REST server communication 
			printGameMenu(readInput, service);

			// start threads 
			final MessageHandlerThread mht = new MessageHandlerThread();
			Thread handler = new Thread(mht);
			handler.start();
			
			new Thread(new ServerSocketHandler(srvSocket)).start();

			// the game is now started so start user input thread
			new Thread(new UserInputHandlerThread(readInput)).start();
			
			// start simulator for bombs 
			final AccelerometerSimulator as = new AccelerometerSimulator(MeasureBuffer.getInstance()); 
			final SensorDataAnalyzer sda = new SensorDataAnalyzer();
			Thread simulator = new Thread(as);
			Thread analyzer = new Thread(sda);
			simulator.start();
			analyzer.start();
			
			// wait for the game to be finished
			final GameLock lock = GameLock.getInstance();
			synchronized (lock) {
				lock.wait();
			}
			
			// stop  threads gracefully 
			exitGameGracefully(mht, sda, as);
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * This stops every thread and then exit the game	 
	 * @param message handler thread to be stopped
	 * @param the sensor data thread to be stopped
	 * @param the accelerometer simulator to be stopped
	 */
	private static void exitGameGracefully(MessageHandlerThread mht, SensorDataAnalyzer sda, AccelerometerSimulator as) {
		mht.stopThread();
		sda.stopAnalyzer();
		as.stopMeGently();
		System.out.println("The game is over. Goodbye");
		System.exit(0);
		
	}

	/**
	 * Handle the first menu before entering a game.
	 * From this menu a new player can join or create a game.
	 * @param the bufferedReader to communicate with the user
	 * @param the service requester to communicate with the REST server
	 */
	private static void printGameMenu(BufferedReader br, ServiceRequester service) {
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
			case 1: // print names of current active games
				try {
					service.retrieveGames();
				} catch (RuntimeException e) {
					System.err.println(e.getMessage());
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case 2: // creates a new game and add it on the REST server
				try {
					Game currentGame = null;
					final Peer peer = Peer.getInstance();
					System.out.println("Select a name for the game: ");
					final String name = br.readLine();
					int length;
					do {
						System.out.println("Select the length for the grid (must be an even number > 2): ");
						length = Integer.parseInt(br.readLine());  
					} while(length%2 != 0);
					System.out.println("Select the number of points to win: ");
					final int points = Integer.parseInt(br.readLine());
					
					// ask the REST server to add a new game 
					currentGame = service
							.addGame(new Game(name, length, points, peer.getCurrentPlayer()));
					if (currentGame == null) {
						System.out.println("Error creating game");
						break;
					}

					System.out.println("Game added correctly on the server!");
					System.out.print("Waiting to be connected to it...");
					
					peer.setCurrentGame(currentGame);
					
					// choose a random position and start playing 
					final Random random = new Random();
					Cell newCell = new Cell(random.nextInt(length), random.nextInt(length));
					peer.setCurrentPosition(newCell);
					peer.setAlive(true);
					peer.setClientConnections(new HashMap<>());
					exit = true;
					System.out.println("Done");

				} catch (IOException | NumberFormatException e) {
					System.out.println("Please insert correct values!");
				}
				break;

			case 3: // save game returned by the server when you choose it 
				Game currentGame = null;
				String gameName = null;
				final Peer peer = Peer.getInstance();
				try {
					System.out.println("Enter game name: ");
					gameName = br.readLine();
					
					// ask the REST server to add me to the game I chose
					currentGame = service.addPlayerToGame(gameName, peer.getCurrentPlayer());
					
					// currentGame == null if the game did not exist or player was already in the game
					if (currentGame == null)
						break;
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
				System.out.println("Player added correctly to the game on the server!");
				System.out.println("Waiting to be inserted to the ring...");
				peer.setCurrentGame(currentGame);
				peer.setClientConnections(new HashMap<>());
				
				// try to actually join the game selected
				if(startJoiningRingProcedure()){
					peer.setAlive(true);
					exit = true;
					System.out.println("done!");
				}
				else {
					
					// ask REST server to delete me from the map 
					service.deletePlayerFromGame(gameName, peer.getCurrentPlayer());
				}
				
				break;

			case 4: // print active games details
				try {
					System.out.println("Enter game name: ");
					final String gamename = br.readLine();
					service.retrieveGameInfo(gamename);
					break;

				} catch (Exception e) {
					e.printStackTrace();
					break;
				}

			case 5: // exit the game
				System.out.println("Leaving the game. Goodbye!");
				System.exit(0);

			default: // choice not recognized
				System.out.println("Please select a number between 1 and 5");
				break;
			}
		}

	}
	 
	/**
	 
	 */
	/**
	 * This is the procedure to join the user selected game 
	 * @return true if the operation has been handled correctly.
	 */
	private static boolean startJoiningRingProcedure() {
		try {
			final OutQueue outQueue = OutQueue.getInstance();
			final Peer peer = Peer.getInstance();
			
			// create message and handle it 
			final Message joinRing = new JoinRingMessage(peer.getCurrentPlayer());
			
			// needed to avoid Token message overlapping 
			synchronized (outQueue) {
				return joinRing.handleOutMessage(null);
				
			}

		} catch(Exception e){
			System.err.println("Error joining ring!");
			e.printStackTrace();
		}
		return false;
	}
		
	/**
	 * retrieve player's info from standard in
	 * @param the buffered reader to communicate with the user
	 * @return the current player of the game
	 */
	private static Player getPlayerInfo(BufferedReader br) {
		final Emoji emoji = EmojiManager.getForAlias("bomb");
		System.out.println(emoji.getUnicode() + emoji.getUnicode() + " WELCOME TO BOMB RING " + emoji.getUnicode()
				+ emoji.getUnicode());

		final Player p = new Player();
 
		try {
			System.out.println("Please insert your name");
			final String name = br.readLine();
			System.out.println("Please insert your surname");
			final String surname = br.readLine();
			System.out.println("Please pick a nickname");
			final String nickname = br.readLine();
			p.setName(name);
			p.setSurname(surname);
			p.setNickname(nickname);
			p.setId(p.hashCode());
		} catch (IOException e) {
			System.err.println("Error retrieving player info");
		}

		return p;

	}

}
