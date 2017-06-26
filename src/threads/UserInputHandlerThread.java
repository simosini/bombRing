package threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import messages.Message;
import messages.Packets;
import messages.PositionMessage;
import peer.Cell;

/**
 * As the name says this thread handles move and bomb tossing requests from the
 * user. It builds packets to put in the outQueue and must wait until the
 * requested message has been processed by the messageHandler. Access to the
 * priority queue must be sync.
 */

public class UserInputHandlerThread implements Runnable {

	private PriorityQueue<Packets> outQueue; // concurrent access
	private BufferedReader userInput; // from the main thread
	private Cell currentCell; // concurrent access with handler
	// private Queue<Bomb> bombs;

	public UserInputHandlerThread(PriorityQueue<Packets> q, BufferedReader br) {
		this.setOutQueue(q);
		this.setUserInput(br);
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
		return currentCell;
		
	}

	// used only for initialization
	// the position is set by the handler otherwise
	public void setCurrentCell(Cell currentCell) {
			this.currentCell = currentCell;
		
	}
	
	public String interruptibleReadLine(BufferedReader reader)
	        throws InterruptedException, IOException {
	    Pattern line = Pattern.compile("^(.*)\\R");
	    Matcher matcher;
	    StringBuilder result = new StringBuilder();
	    int chr = -1;
	    
	    do {
	        if (reader.ready()) chr = reader.read();
	        if (chr > -1) result.append((char) chr);
	        matcher = line.matcher(result.toString());
	    } while (!matcher.matches());
	   
	    return (matcher.matches() ? matcher.group(1) : "");
	}

	@Override
	public void run() {
		while (true) {
			try {
				Packets nextPacket = this.getUserMove(); // can only be a bomb
															// or the
				if (nextPacket != null) { // new position
					synchronized (outQueue) {
						/*
						 * try { all comments for debugging purpose only
						 * Thread.sleep(2000); } catch (InterruptedException e)
						 * { e.printStackTrace(); }
						 */
						outQueue.add(nextPacket);
						outQueue.wait(); // handler will wake me up when is done
											// with this packet
					}
				}
			} catch (InterruptedException e) {
				System.out.println("The game is finished!");
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
			String choice = this.interruptibleReadLine(this.userInput); //this.userInput.readLine();
			switch (choice.toLowerCase()) {
			case "u":
				int[] upPosition = currentPos.move(Cell.DIR.UP);
				Message upm = new PositionMessage(upPosition[0], upPosition[1]);
				upm.setInput(false);
				return new Packets(upm, null);

			case "d":
				int[] downPosition = currentPos.move(Cell.DIR.DOWN);
				Message dpm = new PositionMessage(downPosition[0], downPosition[1]);
				dpm.setInput(false);
				return new Packets(dpm, null);

			case "l":
				int[] leftPosition = currentPos.move(Cell.DIR.LEFT);
				Message lpm = new PositionMessage(leftPosition[0], leftPosition[1]);
				lpm.setInput(false);
				return new Packets(lpm, null);

			case "r":
				int[] rightPosition = currentPos.move(Cell.DIR.RIGHT);
				Message rpm = new PositionMessage(rightPosition[0], rightPosition[1]);
				rpm.setInput(false);
				return new Packets(rpm, null);

			/*
			 * case "b": takes first bomb from the queue break;
			 */

			default:
				System.out.println("Choice not valid! Please try again!");
				return null;
			}
		} 
		catch (Exception e) {
			throw new InterruptedException(e.getMessage());
		}
		
	}

}
