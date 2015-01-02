package controllers;

import java.time.Instant;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import game.Deck;
import game.Game;
import game.Player;
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
		//Deck deck = new Deck(Deck.unapply(Deck.fromFile("deck.txt")));
		Player p1 = new Player("p1", Deck.fromFile("deck.txt").get());
		Player p2 = new Player("p2", Deck.fromFile("deck.txt").get());
		Game game = new Game(p1,p2);
		return Response.ok(new ResponseHolder()).build();
	}
}
