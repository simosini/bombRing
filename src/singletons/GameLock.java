package singletons;

public class GameLock {

	private static GameLock instance = null;
	
	private GameLock() {}
	
	public static synchronized GameLock getInstance() {
		if (instance == null)
			instance = new GameLock();
		return instance;
	}
	

}
