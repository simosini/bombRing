package messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TryMessages {

	private static final int PORT = 4445;

	public static void main(String[] args) {
		TryMessages tr = new TryMessages();
		
		if (Integer.parseInt(args[0]) == 0){
			try {
				
				ServerSocket srv = new ServerSocket(PORT);
				System.out.println("Server started");
				Socket cli = srv.accept();
				ObjectInputStream inStream = new ObjectInputStream(cli.getInputStream());
	
				InMessage msg = (InMessage) inStream.readObject();
				msg.handleMessage();
				cli.close();
				srv.close();

			} 
			catch (IOException e) {
				e.printStackTrace();
			} 
			catch (ClassNotFoundException cn) {
				cn.printStackTrace();
			}
		}
		
		else {

			Message m1 = new PositionMessage(1, 1);
			tr.communicate(m1);
		}
			

	}

	public void communicate(Message m) {

		try {
			Socket socket = new Socket("localhost", PORT);
			ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("Object to be written = " + m);
			outputStream.writeObject(m);
			socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
