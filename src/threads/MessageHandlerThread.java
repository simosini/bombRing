package threads;

import messages.Message;
import messages.Packets;
import singletons.InQueue;

/**
 * This thread (the handler) is in charge of moving incoming messages received
 * on the server socket from the inQueue to the outQueue.
 * The protocol consists on the following rules: 
 * 1. the handler waits on the inQueue until a message arrives on the server. 
 * 2. When a message arrives a server put it on the inQueue and notify the handler.
 * 3. If the handler was not waiting on the queue is either because was putting 
 * 	  a message on the other queue or was answering to simple messages. (see 4) 
 * 	  The notify won't have any effect but this is not a problem
 * 	  because the handler when is done on the outQueue or with sending messages
 *    always checks the inQueue is empty before calling wait().
 * 4. The message is taken from the inQueue and if the
 *    handling is a simple operation such as ACK, the handler sends it and
 *    waits, otherwise it creates a new message to be put on the outQueue. 
 * 5. If the message is the Token then the handler notifies the thread in charge of
 * 	  handling the outQueue. Notice that this notification always has success cause
 * 	  the Token can only arrive when the Thread is in wait cause when is running means
 * 	  he has the token so it cannot arrive on the server socket.
 */

public class MessageHandlerThread implements Runnable {

	private volatile boolean stop = false; // to stop the thread when the game
											// is finished

	public MessageHandlerThread() { }


	public synchronized void stopThread() {
		this.stop = true;
	}

	private synchronized boolean stop() {
		return this.stop;
	}

	@Override
	public void run() {
		InQueue inQueue = InQueue.INSTANCE;
	
		while (!stop()) {
			synchronized (inQueue) {
				
				while (inQueue.isEmpty()) {
					System.out.println("The queue is empty. Waiting!");
					try {
						inQueue.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					/** check if the player is the only one in the game */
					System.out.println("Im awake");
				}
				
				/** consumes all items before releasing the lock on inQueue */
				while (!inQueue.isEmpty()) {
					System.out.println("Consuming inQueue");
					Packets inPacket = inQueue.poll();
					Message inMessage = inPacket.getMessage();
					System.out.println("-----HANDLING-----");
					System.out.println(inMessage);
					/** handling could be an ack or a new msg to put on the outQ 
					 *  in most of the cases will be the token */
					if (!inMessage.handleInMessage(inPacket.getSendingClient())){
						System.out.println("Something went wrong handling messages. Exiting the game!");
						System.exit(1);
					}
				}
				System.out.println("No more msg in the inQueue, releasing lock");
			}

		}
	}

}
