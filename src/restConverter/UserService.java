package restConverter;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.NotFoundException;

import exceptions.AppException;

@Path("/players")
public class UserService {

	/** yields a list of all players of all games. N.B does not work!!!!! */

	@Path("/getallplayers")
	@GET
	@Produces("application/json")
	public Response getFullPlayersList() throws AppException {
		List<Player> allplayers = getAllPlayers(Games.getInstance().getGamesList());
		if (allplayers == null)
			throw new AppException("There are no current active players!");
		return Response.ok().entity(allplayers).build();
	}

	private List<Player> getAllPlayers(List<Game> gamesList) {
		List<Player> players = new ArrayList<>();
		gamesList.forEach(el -> players.addAll(el.retrieveGamePlayers()));
		return players;
	}

	// yields a list of all players for the given game
	@Path("/getplayers/{gameName}")
	@GET
	@Produces("application/json")
	public Response getPlayersList(@PathParam("gameName") String gameName) throws AppException {
		Game g = Games.getInstance().getByName(gameName);
		if (g == null)
			throw new AppException("The game selected does not exist!");
		return Response.ok().entity(g.retrieveGamePlayers()).build();

	}

	// insert a player to the selected game
	@Path("/addplayer/{gameName}")
	@PUT
	@Consumes("application/json")
	@Produces("application/json")
	public Response addPlayer(@PathParam("gameName") String gameName, Player p) throws AppException {
		String name = gameName;
		try {
			Games.getInstance().addPlayer(name, p);
		} catch (IllegalArgumentException iae) {
			throw new AppException(iae.getMessage());
		}

		return Response.ok().entity(Games.getInstance().getByName(name)).build();
	}

	// yields games list
	@Path("/getgames")
	@GET
	@Produces("application/json")
	public Response getGames() {

		return Response.ok().entity(Games.getInstance().getGamesList()).build();

	}

	// add a game to the list
	@Path("/addgame")
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response addGame(Game g) throws AppException {
		try {
			Games.getInstance().addGame(g);
		} catch (IllegalArgumentException e) {
			throw new AppException(e.getMessage());
		}

		return Response.ok().entity(Games.getInstance().getByName(g.getName())).build();
	}

	// permette di prelevare con un determinato nome
	@Path("/getgame/{name}")
	@GET
	@Produces("application/json")
	public Response getGameByName(@PathParam("name") String name) throws AppException {
		Game g = Games.getInstance().getByName(name);
		if (g != null)
			return Response.ok(g).build();
		else
			throw new AppException("The game selected does not exist!");
	}
	
	// delete a player from the selected game
		@Path("/deleteplayer/{gameName}")
		@DELETE
		@Consumes("application/json")
		public Response deletePlayer(@PathParam("gameName") String gameName, Player p) throws AppException {
			
			try {
				Games.getInstance().deletePlayer(gameName, p);
			} catch (NotFoundException e) {
				throw new AppException(e.getMessage());
			}

			return Response.ok().entity("Player deleted correctly!").build();
		}

}
