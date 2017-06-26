package peer;

public enum Uri {
	BASE_URI("http://localhost:8080/restConverter/rest/services/"),
	ADD_GAME("addgame"),
	ADD_PLAYER("addplayer/"),
	DELETE_PLAYER("deleteplayer/"),
	GET_ALL_PLAYERS("getallplayers"),	
	GET_PLAYERS("getplayers/"),
	GET_GAME("getgame/"),
	GET_GAMES("getgames");
	
	
	
	private String path;
	
	private Uri(String path){
		this.setPath(path);
	}

	private void setPath(String path) {
		this.path = path;
		
	}

	public String getPath() {
		return path;
	}
	
}
