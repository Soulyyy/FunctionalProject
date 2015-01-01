package controllers;

import java.time.Instant;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import game.Game;
import game.ScalaClass;
import model.ResponseHolder;


/**
 * Created by Hans on 1.01.2015.
 */
public class GameServlet {

	@Path("/game")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGame() {

		Game game = new Game();
		return Response.ok(new ResponseHolder()).build();
	}
}
