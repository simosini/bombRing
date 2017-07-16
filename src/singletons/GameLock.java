package singletons;

/**
 * This is just a lock used to synchronize the main thread with all the others.
 * It allows it to close gracefully all other operating threads when the 
 * game is over.
 */
public class GameLock {

	private static GameLock instance = null;
	
	private GameLock() {}
	
	/**
	 * singleton
	 */
	public static synchronized GameLock getInstance() {
		if (instance == null)
			instance = new GameLock();
		return instance;
	}
	

}
