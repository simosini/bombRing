package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;

import beans.Game;
import beans.Player;
import messages.Packets;
import peer.Cell;
import peer.Peer;
import threads.MessageHandlerThread;
import threads.ServerSocketHandler;
import threads.UserInputHandlerThread;

/** this is the  main of the game: everything starts here */
public class GameMain {

	public static void main(String[] args) {
		try {
			/** init basic structures and socket for the game */  
			Queue<Packets> inQueue = new LinkedList<>();
			PriorityQueue<Packets> outQueue = new PriorityQueue<>();
			ServerSocket srvSocket = new ServerSocket(0);
			Peer peer = new Peer(); /** will contain all game info */
			
			BufferedReader readInput = new BufferedReader(new InputStreamReader(System.in));
			
			Player newPlayer = getPlayerInfo(readInput);
			newPlayer.setPort(srvSocket.getLocalPort());
			peer.setCurrentPlayer(newPlayer);
			
			/** start server communication */
			printGameMenu(peer, readInput);
			
			
			/** start threads */
			Thread handler = new Thread(new MessageHandlerThread(inQueue, outQueue, peer)); 
			handler.start();
			new Thread(new ServerSocketHandler(srvSocket, inQueue, handler)).start();
			
			
			/** the game is now started */
			new Thread(new UserInputHandlerThread(outQueue, readInput, inQueue, peer.getCurrentPosition(), peer.getCurrentGame().getSideLength())).start();
			
			
			
			
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}

	}
	
	/** Handle the first menu before entering a game */
	private static void printGameMenu(Peer peer, BufferedReader br) {
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
					peer.retrieveGames();
					peer.retrieveAllPlayers();
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
					currentGame = peer.addGame(new Game(name, length, points, peer.getCurrentPlayer()));
					if (currentGame == null)
						break;
					System.out.println("Game added correctly on the server!");
					System.out.println("Waiting to be connected to it...");
					peer.setCurrentGame(currentGame);
					/** choose a random position */
					Random random = new Random();
					Cell newCell = new Cell(random.nextInt(length), random.nextInt(length));
					newCell.setZoneColor("red");
					peer.setCurrentPosition(newCell);
					peer.setAlive(true);
					exit = true;

				} catch (IOException | NumberFormatException e) {
					System.out.println("Please insert correct values!");
				}
				break;

			case 3: /** save game returned by the server when you choose it */
				Game currentGame = null;
				try {
					System.out.println("Enter game name: ");
					String gameName = br.readLine();

					currentGame = peer.addPlayerToGame(gameName, peer.getCurrentPlayer());
					if (currentGame == null)
						break;
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
				System.out.println("Player added correctly to the game on the server!");
				System.out.println("Waiting to be inserted to the ring...");
				peer.setCurrentGame(currentGame);
				
				exit = true;
				break;
				
			case 4:
				try {
					System.out.println("Enter game name: ");
					String gameName = br.readLine();
					peer.retrieveGameInfo(gameName);
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
		} 
		catch (IOException e) {
			System.out.println("Error retrieving player info");
		}

		return p;
		
	}

}
