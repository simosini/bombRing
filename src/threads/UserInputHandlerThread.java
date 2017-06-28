package threads;

import java.io.BufferedReader;
import java.util.PriorityQueue;
import java.util.Queue;

import messages.Message;
import messages.Packets;
import messages.PositionMessage;
import peer.Cell;
import peer.Cell.DIR;

/**
 * As the name says this thread handles move and bomb tossing requests from the
 * user. It builds packets to put in the outQueue and must wait until the
 * requested message has been processed by the messageHandler. Access to the
 * priority queue must be sync.
 */

public class UserInputHandlerThread implements Runnable {

	private Queue<Packets> inQueue; // to notify the handler if needed
	private PriorityQueue<Packets> outQueue; // concurrent access
	private BufferedReader userInput; // from the main thread
	private Cell currentCell; // concurrent access with handler
	private int gridDimension; // to check movement allowed
	// private Queue<Bomb> bombs;

	public UserInputHandlerThread(PriorityQueue<Packets> q, BufferedReader br, Queue<Packets> inq, Cell c, int d) {
		this.setOutQueue(q);
		this.setUserInput(br);
		this.setCurrentCell(c);
		this.inQueue = inq;
		this.gridDimension = d;
	}

	public PriorityQueue<Packets> getOutQueue() {
		return outQueue;
	}

	public void setOutQueue(PriorityQueue<Packets> outQueue) {
		this.outQueue = outQueue;
	}

	public BufferedReader getUserInput() {
		return userInput;
	}

	public void setUserInput(BufferedReader userInput) {
		this.userInput = userInput;
	}

	// no need to sync cause the handler access this value only if i'm waiting
	public Cell getCurrentCell() {
		// yields a copy
		Cell copy = new Cell(currentCell);
		copy.setZoneColor(currentCell.getZoneColor());
		return copy;

	}

	// used only for initialization
	// the position is set by the handler otherwise
	public void setCurrentCell(Cell currentCell) {
		this.currentCell = currentCell;

	}

	@Override
	public void run() {
		while (true) {
			try {
				Packets nextPacket = this.getUserMove(); // can only be a bomb
															// or the new
															// position
				if (nextPacket != null) {
					synchronized (outQueue) {
						/*
						 * try { all comments for debugging purpose only
						 * Thread.sleep(2000); } catch (InterruptedException e)
						 * { e.printStackTrace(); }
						 */
						outQueue.add(nextPacket);
						System.out.println("Notifying the handler");
						synchronized (inQueue) {
							inQueue.notify(); // wake the handler up
						}

						outQueue.wait(); // handler will wake me up when is done
						System.out.println("done!"); // with this packet
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
			Cell currentPos = this.getCurrentCell();
			System.out.println(currentPos);

			System.out.println("Select a move:\n" + "U - move up;\n" + "D - move down;\n" + "L - move left;\n"
					+ "R - move right;\n" + "B - toss a bomb if available;\n");
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

			default:
				System.out.println("Choice not valid! Please try again!");
				return null;
			}
		} catch (Exception e) {
			throw new InterruptedException(e.getMessage());
		}

	}

	private boolean isMovementAllowed(DIR direction) {
		int[] newPosition = this.getCurrentCell().move(direction);
		if (newPosition[0] < 0 | newPosition[1] < 0 | newPosition[0] >= this.gridDimension
				| newPosition[1] >= this.gridDimension)
			return false;
		return true;
	}

}
