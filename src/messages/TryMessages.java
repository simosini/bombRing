package messages;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class TryMessages {

	private static final int PORT = 4445;

	
	public static void main(String[] args) {
		TryMessages tr = new TryMessages();
		
		if (Integer.parseInt(args[0]) == 0){
			try {
				ObjectMapper mapper = new ObjectMapper();
				ServerSocket srv = new ServerSocket(PORT);
				System.out.println("Server started");
				Socket cli = srv.accept();
				BufferedReader inStream = new BufferedReader(new InputStreamReader(cli.getInputStream()));
	
				Message m = mapper.readValue(inStream.readLine(), new TypeReference<Message>() { });
				System.out.println(m);
				cli.close();
				srv.close();

			} 
			catch (IOException e) {
				e.printStackTrace();
			} 
		}
		
		else {

			AckMessage m = new AckMessage();
			tr.communicate(m);
		}
			

	}

	public void communicate(Message m) {

		try {
			ObjectMapper mapper = new ObjectMapper();
			Socket socket = new Socket("localhost", PORT);
			DataOutputStream out  = new DataOutputStream(socket.getOutputStream());
			System.out.println("Object to be written = " + m);
			String message = mapper.writeValueAsString(m);
			out.writeBytes(message + "\n");
			socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
