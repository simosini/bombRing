package threads;

import java.io.BufferedReader;

import messages.ExitMessage;
import messages.Message;
import messages.Packets;
import messages.PositionMessage;
import peer.Cell;
import peer.Cell.DIR;
import singletons.OutQueue;
import singletons.Peer;

/**
 * As the name says this thread handles move and bomb tossing requests from the
 * user. It builds packets to put in the outQueue and must wait until the
 * requested message has been processed by the messageHandler. Access to the
 * priority queue must be sync. If the player is alone in the game this thread
 * create an handling out packet thread to handle his packets and join with it.
 */

public class UserInputHandlerThread implements Runnable {

	private BufferedReader userInput; // from the main thread
	private final int gridDimension = Peer.INSTANCE.getCurrentGame().getSideLength();// to check movement allowed
	// private Queue<Bomb> bombs;

	public UserInputHandlerThread(BufferedReader br) {
		this.setUserInput(br);
	}

	public BufferedReader getUserInput() {
		return userInput;
	}

	private void setUserInput(BufferedReader userInput) {
		this.userInput = userInput;
	}

	@Override
	public void run() {
		OutQueue outQueue = OutQueue.INSTANCE;
		
		while (true) {
			try {
				Packets nextPacket = this.getUserMove(); // can only be a bomb
															//  new position or exit
				if (nextPacket != null) {
					synchronized (outQueue) {
					
						/** 
						 * if i'm alone there is no token so create thread
						 * no need to put packet in the queue. Pass it to the handler 
						 * */
						if (Peer.INSTANCE.getNumberOfPlayers() == 1) {
							Thread t = new Thread(new OnePlayerHandlerThread(nextPacket));
							t.start();
							t.join();
						}
						else {
							// handler will wake me up when is done with my packet
							outQueue.add(nextPacket);
							System.out.println("User Thread put outQueue packet");
							outQueue.wait(); 
							System.out.println("User thread packet handled!");
						}
					}
				}
			} catch (InterruptedException e) {
				System.out.println("The game finished!");
				break;
			}

		}

	}

	private Packets getUserMove() throws InterruptedException {

		try {
			/** print current position */
			Cell currentPos = Peer.INSTANCE.getCurrentPosition();
			System.out.println(currentPos);

			System.out.println("Select a move:\n" + "U - move up;\n" + "D - move down;\n" + "L - move left;\n"
					+ "R - move right;\n" + "B - toss a bomb if available;\nE - exit game;\n");
			String choice = this.userInput.readLine();
			switch (choice.toLowerCase()) {
				case "u":
					if (!isMovementAllowed(Cell.DIR.UP)) {
						System.out.println("You can't move up from your position. Please choose another move!");
						return null;
					}
					int[] upPosition = currentPos.move(Cell.DIR.UP);
					Message upm = new PositionMessage(upPosition[0], upPosition[1]);
					upm.setInput(false);
					System.out.print("Waiting to move up...");
					return new Packets(upm, null);
	
				case "d":
					if (!isMovementAllowed(Cell.DIR.DOWN)) {
						System.out.println("You can't move down from your position. Please choose another move!");
						return null;
					}
					int[] downPosition = currentPos.move(Cell.DIR.DOWN);
					Message dpm = new PositionMessage(downPosition[0], downPosition[1]);
					dpm.setInput(false);
					System.out.print("Waiting to move down...");
					return new Packets(dpm, null);
	
				case "l":
					if (!isMovementAllowed(Cell.DIR.LEFT)) {
						System.out.println("You can't move left from your position. Please choose another move!");
						return null;
					}
					int[] leftPosition = currentPos.move(Cell.DIR.LEFT);
					Message lpm = new PositionMessage(leftPosition[0], leftPosition[1]);
					lpm.setInput(false);
					System.out.print("Waiting to move left...");
					return new Packets(lpm, null);
	
				case "r":
					if (!isMovementAllowed(Cell.DIR.RIGHT)) {
						System.out.println("You can't move right from your position. Please choose another move!");
						return null;
					}
					int[] rightPosition = currentPos.move(Cell.DIR.RIGHT);
					Message rpm = new PositionMessage(rightPosition[0], rightPosition[1]);
					rpm.setInput(false);
					System.out.print("Waiting to move right...");
					return new Packets(rpm, null);
	
				/*
				 * case "b": takes first bomb from the queue break;
				 */ 
				 case "e": 
					 
					 ExitMessage em = new ExitMessage();
					 em.setInput(false);
					 System.out.println("Waiting to close the game...done!");
					 // return new Packets(em, null);
					 System.exit(0); //later will be changed
			
	
				default:
					System.out.println("Choice not valid! Please try again!");
					return null;
			}
		} catch (Exception e) {
			throw new InterruptedException(e.getMessage());
		}

	}

	private boolean isMovementAllowed(DIR direction) {
		int[] newPosition = Peer.INSTANCE.getCurrentPosition().move(direction);
		if (newPosition[0] < 0 | newPosition[1] < 0 | newPosition[0] >= this.gridDimension
				| newPosition[1] >= this.gridDimension)
			return false;
		return true;
	}

}
