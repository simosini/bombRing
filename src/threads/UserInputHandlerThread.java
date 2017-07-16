package threads;

import java.io.BufferedReader;

import beans.Player;
import messages.BombTossedMessage;
import messages.ExitMessage;
import messages.Message;
import messages.Packets;
import messages.PositionMessage;
import peer.Bomb;
import peer.Cell;
import peer.Cell.DIR;
import singletons.BombQueue;
import singletons.OutQueue;
import singletons.Peer;

/**
 * As the name says this thread handles move and bomb tossing requests from the
 * user. It builds packets to put in the outQueue and must wait until the
 * requested message has been processed by the messageHandler. Access to the
 * priority queue must be sync. If the player is alone in the game this thread
 * creates a specific thread to handle his packets and join with it.
 */

public class UserInputHandlerThread implements Runnable {

	private BufferedReader userInput; 
	private final int gridDimension = Peer.getInstance().getCurrentGame().getSideLength();

	public UserInputHandlerThread(final BufferedReader br) {
		this.setUserInput(br);
	}

	public BufferedReader getUserInput() {
		return userInput;
	}

	private void setUserInput(BufferedReader userInput) {
		this.userInput = userInput;
	}
	/**
	 * handles incoming requests from the user
	 */
	@Override
	public void run() {
		final OutQueue outQueue = OutQueue.getInstance();
		final Peer peer = Peer.getInstance();
		
		while (true) {
			try {
				
				// If I'm dead no option is available 
				if (peer.isAlive()){
					
					Packets nextPacket = this.getUserMove(); 
					
					if (nextPacket != null) {
						synchronized (outQueue) {
							
							/*
							 * if i'm alone there is no token so create one player thread.
							 * no need to put packet in the queue just pass it to the thread 
							 */
							if (peer.getNumberOfPlayers() == 1) {
								Thread t = new Thread(new OnePlayerHandlerThread(nextPacket));
								t.start();
								t.join(); 
								System.out.println("Done!");
							}
							else {
								
								// handler will wake me up when is done with my packet
								outQueue.add(nextPacket);
								outQueue.wait(); 
								System.out.println("Done!");
							}
						}
					}
				}
				
				// gets here only if the player is not playing anymore. So exit the thread 
				else break;
					
			} catch (InterruptedException e) {
				System.out.println("The game is over. Goodbye!");
				break;
			}

		}

	}
	
	/**
	 * get the move selected by the user
	 * @return a packet with the move chosen by the user or null if the request is not a new move
	 * @throws InterruptedException when an error occurs
	 */
	private Packets getUserMove() throws InterruptedException {

		try {
			final Peer peer = Peer.getInstance();
			
			// print current position, score and bombs available 
			System.out.println("\n#################### GAME MENU ####################");
			final Cell currentPos = peer.getCurrentPosition();
			final String colorZone = this.computeZone(currentPos.getPosition());
			final Player currentPlayer = peer.getCurrentPlayer();
			System.out.println(colorZone + currentPos);
			System.out.println("Your current score is: " + peer.getCurrentScore());
			System.out.println(this.showBomb());

			System.out.println("Select a move:\n" + "U - move up;\n" + "D - move down;\n" + "L - move left;\n"
					+ "R - move right;\n" + "B - toss a bomb if available;\nE - exit game;\nV - view game details;\n");
			String choice = this.userInput.readLine();
			switch (choice.toLowerCase()) {
				case "u":
					if (!isMovementAllowed(Cell.DIR.UP)) {
						System.out.println("You can't move up from your position. Please choose another move!");
						return null;
					}
					final int[] upPosition = currentPos.move(Cell.DIR.UP);
					final Message upm = new PositionMessage(upPosition[0], upPosition[1], currentPlayer);
					System.out.print("Waiting to move up...");
					return new Packets(upm, null);
	
				case "d":
					if (!isMovementAllowed(Cell.DIR.DOWN)) {
						System.out.println("You can't move down from your position. Please choose another move!");
						return null;
					}
					final int[] downPosition = currentPos.move(Cell.DIR.DOWN);
					final Message dpm = new PositionMessage(downPosition[0], downPosition[1], currentPlayer);
					System.out.print("Waiting to move down...");
					return new Packets(dpm, null);
	
			 	case "l":
					if (!isMovementAllowed(Cell.DIR.LEFT)) {
						System.out.println("You can't move left from your position. Please choose another move!");
						return null;
					}
					final int[] leftPosition = currentPos.move(Cell.DIR.LEFT);
					final Message lpm = new PositionMessage(leftPosition[0], leftPosition[1], currentPlayer);
					System.out.print("Waiting to move left...");
					return new Packets(lpm, null);
	
				case "r":
					if (!isMovementAllowed(Cell.DIR.RIGHT)) {
						System.out.println("You can't move right from your position. Please choose another move!");
						return null;
					}
					final int[] rightPosition = currentPos.move(Cell.DIR.RIGHT);
					final Message rpm = new PositionMessage(rightPosition[0], rightPosition[1], currentPlayer);
					System.out.print("Waiting to move right...");
					return new Packets(rpm, null);
	
				
				 case "b": 
					 final Bomb tossedBomb = BombQueue.getInstance().removeBomb();
					 if (tossedBomb != null) {
						 System.out.print("Tossing a " +  tossedBomb.getColor() + " bomb...");
						 return new Packets(new BombTossedMessage(tossedBomb.getColor()), null);
					 }
					 System.out.println("No bombs available yet!");
					 return null;
				  
				 case "e": 					 
					 System.out.print("Waiting to close the game...");
					 return new Packets(new ExitMessage(), null);
					 
				 case "v":
					 System.out.println(peer.getCurrentGame());
					 return null;
			
	
				default:
					System.out.println("Choice not valid! Please try again!");
					return null;
			}
		} catch (Exception e) {
			throw new InterruptedException(e.getMessage());
		}

	}
	
	/**
	 * @return  a string describing the next available bomb  
	 */
	private String showBomb() {
		final Bomb availableBomb = BombQueue.getInstance().peekBomb();
		if (availableBomb == null)
			return "No bombs available at the moment";
		return "Next bomb available: " + availableBomb.getColor();
	}
	
	/**
	 * compute the zone color according to current peer position
	 * @param peer's current position on the logical grid
	 * @return a string describing the zone color of the peer.
	 */
	private String computeZone(int[] pos) {
		final StringBuilder sb = new StringBuilder("You are in the ");
		String color = null;
		if (this.gridDimension / (pos[0] + 1) >= 2 && this.gridDimension / (pos[1] +1) >= 2){
			color = "green";
		}
		else if (this.gridDimension / (pos[0] + 1) < 2 && this.gridDimension / (pos[1] +1) < 2){
			color = "yellow";
		}
		else if (pos[0] > pos[1]){
			color = "blue";
		}
		else
			color = "red";
		sb.append(color);
		sb.append(" zone. ");
		Peer.getInstance().getCurrentPosition().setColorZone(color);
		return sb.toString();
	}
	
	/**
	 * Checks if the movement requested by user is allowed.
	 * @param movement selected by the user
	 * @return true if that movement is allowed
	 */
	private boolean isMovementAllowed(DIR direction) {
		final int[] newPosition = Peer.getInstance().getCurrentPosition().move(direction);
		if (newPosition[0] < 0 | newPosition[1] < 0 | newPosition[0] >= this.gridDimension
				| newPosition[1] >= this.gridDimension)
			return false;
		return true;
	}

}
