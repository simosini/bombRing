package services;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.NotFoundException;

import beans.Game;
import beans.Games;
import beans.Player;
import exceptions.AppException;

/**
 * This is the class that exposes the available REST services to clients. 
 */
@Path("/services")
public class UserService {

	/**
	 * @return a response with the list of all players of all games
	 * @throws AppException if no player is actually active in any game
	 */
	@Path("/getallplayers")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFullPlayersList() throws AppException {
		List<Player> allplayers = getAllPlayers(Games.getInstance().getGamesList());
		if (allplayers == null)
			throw new AppException("There are no current active players!");
		return Response.ok().entity(allplayers).build();
	}
	
	/**
	 * @param the list of all games
	 * @return the list of all players for each game
	 */
	private List<Player> getAllPlayers(List<Game> gamesList) {
		List<Player> players = new ArrayList<>();
		gamesList.forEach(el -> players.addAll(el.retrieveGamePlayers()));
		return players;
	}

	/**
	 * @param the name of the game where to retrieve players
	 * @return a response with the list of all players of the given game
	 * @throws AppException when the game does not exist
	 */
	@Path("/getplayers/{gameName}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPlayersList(@PathParam("gameName") String gameName) throws AppException {
		Game g = Games.getInstance().getByName(gameName);
		if (g == null)
			throw new AppException("The game selected does not exist!");
		return Response.ok().entity(g.retrieveGamePlayers()).build();

	}

	/**
	 * Insert a player to the selected game
	 * @param the name of a game passed through the URI
	 * @param a player to be added
	 * @return a response with a copy of the updated game 
	 * @throws AppException if the adding operation could not be performed
	 */
	@Path("/addplayer/{gameName}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addPlayer(@PathParam("gameName") String gameName, Player p) throws AppException {
		String name = gameName;
		try {
			Games.getInstance().addPlayer(name, p);
		} catch (IllegalArgumentException iae) {
			throw new AppException(iae.getMessage());
		}

		return Response.ok().entity(Games.getInstance().getByName(name)).build();
	}

	/**
	 * @return a response with a copy of the list of all active games
	 */
	@Path("/getgames")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGames() {

		return Response.ok().entity(Games.getInstance().getGamesList()).build();

	}

	/**
	 * Add a new game to the list on the REST server
	 * @param the game to be added
	 * @return a response with a copy of the added game
	 * @throws AppException if a game with the same name already existed
	 */
	@Path("/addgame")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addGame(Game g) throws AppException {
		try {
			Games.getInstance().addGame(g);
		} catch (IllegalArgumentException e) {
			throw new AppException(e.getMessage());
		}

		return Response.ok().entity(Games.getInstance().getByName(g.getName())).build();
	}

	/**
	 * Yield a copy of the game specified by the name if present
	 * @param the name of the game
	 * @return a response with a copy of the requested game if present or else null
	 * @throws AppException if the game selected does not exist
	 */
	@Path("/getgame/{name}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGameByName(@PathParam("name") String name) throws AppException {
		Game g = Games.getInstance().getByName(name);
		if (g != null)
			return Response.ok().entity(g).build();
		else
			throw new AppException("The game selected does not exist!");
	}

	/**
	 * Delete a player from the selected game. It does not delete a resource but
	 * just update the current one. That's why a PUT method is used.
	 * @param the name of the game to update through the URI
	 * @param the player to delete
	 * @return an OK response in case the operation concluded gracefully
	 * @throws AppException if player or games could not be deleted 
	 */
	
	@Path("/deleteplayer/{gameName}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deletePlayer(@PathParam("gameName") String gameName, Player p) throws AppException {
		
		// delete the given player and if the game has no more players left also delete the game
		try {
			Games.getInstance().deletePlayer(gameName, p);
			if (Games.getInstance().getByName(gameName).retrievePlayersNumber() == 0) 
				Games.getInstance().deleteGame(gameName);
		} catch (NotFoundException e) {
			throw new AppException(e.getMessage());
		}

		return Response.ok().build();
	}

}
