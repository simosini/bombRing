package peer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.TreeMap;

import beans.Game;
import beans.Player;
import messages.Message;
import messages.TokenMessage;

public class RingExample {

	public static void main(String[] args) throws IOException {
		Player p1 = new Player("simo", "sini", "simosini", 0);
		Player p2 = new Player("luca", "galli", "lugi", 0);
		Player p3 = new Player("marco", "gigi", "luccas", 0);
		Player p4 = new Player("maria", "rossi", "marassi", 0);

		ServerSocket s1 = new ServerSocket(0);
		ServerSocket s2 = new ServerSocket(0);
		ServerSocket s3 = new ServerSocket(0);
		ServerSocket s4 = new ServerSocket(0);

		p1.setPort(s1.getLocalPort());
		p2.setPort(s2.getLocalPort());
		p3.setPort(s3.getLocalPort());
		p4.setPort(s4.getLocalPort());

		Game game = new Game("trivial", 6, 3);
		game.addPlayerToGame(p1);
		game.addPlayerToGame(p2);
		game.addPlayerToGame(p3);
		game.addPlayerToGame(p4);
		System.out.println(game);
		
		/** start the token */
		Socket temp = new Socket("localhost", p1.getPort());
		
		RingExample re = new RingExample();
		
		HandleToken h1 = re.new HandleToken(s1, game, p1);
		HandleToken h2 = re.new HandleToken(s2, game, p2);
		HandleToken h3 = re.new HandleToken(s3, game, p3);
		HandleToken h4 = re.new HandleToken(s4, game, p4);
		
		new Thread(h1).start();
		new Thread(h2).start();
		new Thread(h3).start();
		new Thread(h4).start();

		ObjectOutputStream os = new ObjectOutputStream(temp.getOutputStream());
		os.writeObject(new TokenMessage());
		temp.close();

	}

	public class HandleToken  implements Runnable {

		private ServerSocket srv;
		private Game game;
		private Player myself;

		public HandleToken(ServerSocket s, Game g, Player p) {
			// TODO Auto-generated constructor stub
			this.setSrv(s);
			this.setGame(g);
			this.setPlayer(p);
			
		}

		@Override
		public void run() {
			while(true){
				try {
					Socket cli = srv.accept(); //my client socket to pass the token
					
					ObjectInputStream br = new ObjectInputStream(cli.getInputStream());
					Message m = (Message)br.readObject();
					cli.close(); // i received the message i can close it
					System.out.println(this.getPlayer().getNickname() + " received token" );
					Thread.sleep(2000);
					
					Player nextPeer = getNextPeer(myself, this.getGame().getPlayers().getUsersMap());
					Socket toPeer = new Socket("localhost", nextPeer.getPort());
					// get the output stream to send the token				
					ObjectOutputStream out = new ObjectOutputStream(toPeer.getOutputStream());
					//send the token
					out.writeObject(m);
					toPeer.close();
					System.out.println(this.getPlayer().getNickname() + " sent token" );					
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		private Player getNextPeer(Player p, TreeMap<Integer, Player> gamePlayers) {
			
			if(gamePlayers.size() > 1){
				Integer nextPlayerKey = findNextPlayerKey(p.getId(), gamePlayers);
				return gamePlayers.get(nextPlayerKey);
			}
			else {
				return null; // no more players to ask, only me in the map
				
			}
		}

		private Integer findNextPlayerKey(Integer key, TreeMap<Integer, Player> gamePlayers) {
			Integer nextKey = null;
			if ((nextKey = gamePlayers.higherKey(key)) == null)
				return gamePlayers.firstKey();
			else
				return nextKey;		
		}

		public ServerSocket getSrv() {
			return srv;
		}

		public void setSrv(ServerSocket srv) {
			this.srv = srv;
		}

		public Game getGame() {
			return game;
		}

		public void setGame(Game game) {
			this.game = game;
		}
		
		public Player getPlayer(){
			return myself;
		}
		
		public void setPlayer(Player p){
			this.myself = p;
		}

	}

}
