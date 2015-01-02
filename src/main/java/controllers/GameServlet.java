package controllers;

import java.time.Instant;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import logic.Main;
import model.ResponseHolder;


/**
 * Created by Hans on 1.01.2015.
 */

@Path("/")
public class GameServlet {

/*	@Path("/game")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGame() {
		Instant begin = Instant.now();
		//Deck deck = new Deck(Deck.unapply(Deck.fromFile("deck.txt")));
		Player p1 = new Player("p1", Deck.fromFile("deck.txt").get());
		Player p2 = new Player("p2", Deck.fromFile("deck.txt").get());
		Game game = new Game(p1,p2);
		String resp;
		try {
			resp = BuildGameResponse.buildGame(game).toString();
		} catch (JSONException e) {
			resp ="Failure";
		}
		System.out.println(resp);
		return Response.ok(new ResponseHolder(begin, resp)).build();
	}*/

	@Path("/move")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response postGame(@QueryParam("move") String move) {
		Instant begin = Instant.now();
		//Deck deck = new Deck(Deck.unapply(Deck.fromFile("deck.txt")));
/*		Player p1 = new Player("p1", Deck.fromFile("deck.txt").get());
		Player p2 = new Player("p2", Deck.fromFile("deck.txt").get());
		Game game = new Game(p1,p2);
		String resp;
		try {
			resp = BuildGameResponse.buildGame(game).toString();
		} catch (JSONException e) {
			resp ="Failure";
		}*/
		int moveNumber = Integer.parseInt(move);
		String resp = Main.state();
		System.out.println(resp);
		return Response.ok(new ResponseHolder(begin, resp)).build();
	}
}
