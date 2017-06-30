package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;

import beans.Game;
import beans.Player;
import beans.Players;
import messages.JoinRingMessage;
import messages.Message;
import messages.NackMessage;
import peer.Cell;
import services.ServiceRequester;
import singletons.Peer;
import threads.MessageHandlerThread;
import threads.ServerSocketHandler;
import threads.UserInputHandlerThread;

/** this is the main of the game: everything starts here */
public class GameMain {

	public static void main(String[] args) {
		try {
			/** init basic structures and socket for the game */
			ServerSocket srvSocket = new ServerSocket(0);
			ServiceRequester service = new ServiceRequester();

			BufferedReader readInput = new BufferedReader(new InputStreamReader(System.in));

			Player newPlayer = getPlayerInfo(readInput);
			newPlayer.setPort(srvSocket.getLocalPort());
			Peer.INSTANCE.addPlayer(newPlayer);

			/** start server communication */
			printGameMenu(readInput, service);

			/** start threads */
			MessageHandlerThread mht = new MessageHandlerThread();
			Thread handler = new Thread(mht);
			handler.start();
			new Thread(new ServerSocketHandler(srvSocket, handler)).start();

			/** the game is now started */
			new Thread(
					new UserInputHandlerThread(readInput)).start();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	/** Handle the first menu before entering a game */
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
			case 1:
				System.out.println("This is the list of current active games:");
				try {
					service.retrieveGames();
				} catch (RuntimeException e) {
					System.out.println(e.getMessage());
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case 2:
				;
				try {
					Game currentGame = null;
					System.out.println("Select a name for the game: ");
					String name = br.readLine();
					System.out.println("Select the length for the grid: ");
					int length = Integer.parseInt(br.readLine());
					System.out.println("Select the number of points to win: ");
					int points = Integer.parseInt(br.readLine());
					
					currentGame = service
							.addGame(new Game(name, length, points, Peer.INSTANCE.getCurrentPlayer()));
					if (currentGame == null) {
						System.out.println("Error creating game");
						break;
					}

					System.out.println("Game added correctly on the server!");
					System.out.print("Waiting to be connected to it...");
					
					Peer.INSTANCE.setCurrentGame(currentGame);
					/** choose a random position */
					Random random = new Random();
					Cell newCell = new Cell(random.nextInt(length), random.nextInt(length));
					newCell.setZoneColor("red");
					Peer.INSTANCE.setCurrentPosition(newCell);
					Peer.INSTANCE.setAlive(true);
					exit = true;
					System.out.println("Done");

				} catch (IOException | NumberFormatException e) {
					System.out.println("Please insert correct values!");
				}
				break;

			case 3: /** save game returned by the server when you choose it */
				Game currentGame = null;
				String gameName = null;
				try {
					System.out.println("Enter game name: ");
					gameName = br.readLine();

					currentGame = service.addPlayerToGame(gameName, Peer.INSTANCE.getCurrentPlayer());
					if (currentGame == null)
						break;
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
				System.out.println("Player added correctly to the game on the server!");
				System.out.println("Waiting to be inserted to the ring...");
				startJoiningRingProcedure(currentGame);
				if (!Peer.INSTANCE.isAlive()){ //not in the game
					System.out.println("It was not possible to join the game");
					service.deletePlayerFromGame(gameName, Peer.INSTANCE.getCurrentPlayer());
					break;
				}
				Peer.INSTANCE.setCurrentGame(currentGame);

				exit = true;
				break;

			case 4:
				try {
					System.out.println("Enter game name: ");
					String gamename = br.readLine();
					service.retrieveGameInfo(gamename);
					break;

				} catch (Exception e) {
					e.printStackTrace();
					break;
				}

			case 5:
				System.out.println("Leaving the game. Goodbye!");
				exit = true;
				break;

			default:
				System.out.println("Please select a number between 1 and 4");
				break;
			}
		}

	}

	private static void startJoiningRingProcedure(Game gameToAdd) {
		try {
			/**create message */
			System.out.println("Creating addPlayer message");
			Message joinRing = new JoinRingMessage(Peer.INSTANCE.getCurrentPlayer());
			Players players = gameToAdd.getPlayers();
			System.out.println("Players : " + players);
			Player nextPeer = joinRing.getNextPeer(players);
			System.out.println("This is next Peer: " + nextPeer);
			while(nextPeer != null){
				Socket cli = new Socket("localhost", nextPeer.getPort());
				System.out.println("Connected correctly");
				ObjectOutputStream out = new ObjectOutputStream(cli.getOutputStream());
				out.writeObject(joinRing);
				System.out.println("Message written");
				ObjectInputStream in = new ObjectInputStream(cli.getInputStream());
				Message m = (Message) in.readObject();
				System.out.println("Answer received");
				if(m instanceof NackMessage) { /** try another one */
					System.out.println("Trying another peer to join the ring");
					players.deletePlayer(nextPeer);					
				}
				else {/** i'm in the ring */
					System.out.println("I'm in the ring");
					Peer.INSTANCE.setAlive(true);
					break;
				}
				cli.close();
			}
			
		}
		catch(IOException | ClassNotFoundException e){
			System.out.println("Error joining the ring");
		}
		
	}

	/** retrieve player's info */
	private static Player getPlayerInfo(BufferedReader br) {
		Emoji emoji = EmojiManager.getForAlias("bomb");
		System.out.println(emoji.getUnicode() + emoji.getUnicode() + " Welcome to Bomb Ring " + emoji.getUnicode()
				+ emoji.getUnicode());

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
		} catch (IOException e) {
			System.out.println("Error retrieving player info");
		}

		return p;

	}

}
