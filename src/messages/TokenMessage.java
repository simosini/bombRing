package messages;

import peer.ConnectionData;

public class TokenMessage extends Message {
	
	private static final long serialVersionUID = -2487582210405295762L;
	private static final int TOKEN_PRIORITY = 5;
	
	public TokenMessage() {
		super(Type.TOKEN, TOKEN_PRIORITY);
	}
	
	/*public void handleInMessage(Socket sender){
		OutQueue outQueue = OutQueue.INSTANCE;
		Packets outPacket = null;
		// when received check the queue, handle message and pass the token 
		if(!outQueue.isEmpty()){
			synchronized (outQueue) {
				System.out.println("retrieving packet from outq");
				outPacket = outQueue.poll(); // only first packet 
			}
			if (outPacket != null) {
				System.out.println("Starting handling");
				Message outMessage = outPacket.getMessage();
				outMessage.handleInMessage(outPacket.getSendingSocket());
			}
		}
		passToken(this);
	}
	
	private void passToken(TokenMessage tokenMessage) {
		try {
			Player nextPeer = null;
			Players others = Peer.INSTANCE.getCurrentGame().getPlayers();
			if ((nextPeer = this.getNextPeer(others)) != null){ //i'm not alone
				Socket cli = new Socket("localhost", nextPeer.getPort());
				ObjectOutputStream out = new ObjectOutputStream(cli.getOutputStream());
				out.writeObject(tokenMessage);
				Thread.sleep(500);
				cli.close();
			}
		}
		catch(IOException | InterruptedException e) {
			System.out.println("Error passing token!");
		}
	}*/
	
	@Override
	public boolean handleInMessage(ConnectionData clientConnection) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString(){
		return "This is a Token message";
	}

	

}
