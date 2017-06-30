package services;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import messages.NackMessage;

public class ClientSock {

	public static void main(String[] args) {
		try {
			Socket cli = new Socket("localhost", 33379);
			ObjectOutputStream out = new ObjectOutputStream(cli.getOutputStream());
			out.writeObject(new NackMessage());
			ObjectInputStream in = new ObjectInputStream(cli.getInputStream());
			System.out.println(in.readObject());
			cli.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
