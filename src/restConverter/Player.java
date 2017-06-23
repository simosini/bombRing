package restConverter;

import java.io.Serializable;

public class Player implements Serializable{

	private static final long serialVersionUID = -6157801865035828060L;
	private String name;
	private String surname;
	private String nickname;
	private int id;
	private int port;

	public Player() {
	}

	public Player(String name, String surname, String nickname, int port) {
		this.setName(name);
		this.setSurname(surname);
		this.setNickname(nickname);
		this.setPort(port);
		this.setId(this.hashCode());
	}

	public Player(Player player) {
		this.setName(player.getName());
		this.setSurname(player.getSurname());
		this.setNickname(player.getNickname());
		this.setPort(player.getPort());
		this.setId(player.getId());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getId() {
		return id;
	}

	public int getPort() {
		return port;
	}
	
	@Override
	public int hashCode() {
		int result = 17;
        result = 31 * result + this.name.hashCode();
        result = 31 * result + this.port;
        result = 31 * result + this.surname.hashCode();
        result = 31 * result + this.nickname.hashCode();
        return Math.abs(result);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\nName: " + this.getName() + "\n");
		sb.append("Surname: " + this.getSurname() + "\n");
		sb.append("Nickname: " + this.getNickname() + "\n");
		sb.append("Port: " + this.getPort() + "\n");
		sb.append("Id: " + this.getId() + "\n");
		return sb.toString();
	}

}
