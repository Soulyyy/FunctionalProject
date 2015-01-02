package controllers;

import java.time.Instant;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import game.Deck;
import game.Game;
import game.Player;
import model.ResponseHolder;
import model.GameDisplay;

/**
 * Created by Hans on 1.01.2015.
 */
@Path("/")
public class GameServlet {

	@Path("/game")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGame() {
		Instant begin = Instant.now();
		//Deck deck = new Deck(Deck.unapply(Deck.fromFile("deck.txt")));
		Player p1 = new Player("p1", Deck.fromFile("deck.txt").get());
		Player p2 = new Player("p2", Deck.fromFile("deck.txt").get());
		Game game = new Game(p1, p2);

		game.currentPlayer().startTurn();
		game.opponent().startTurn();
		game.currentPlayer().startTurn();
		game.opponent().startTurn();
		game.currentPlayer().startTurn();
		game.opponent().startTurn();
		game.currentPlayer().startTurn();
		game.currentPlayer().playCard(1);

		return Response.ok(new ResponseHolder(begin, new GameDisplay(game))).build();
	}

	@Path("/move")
	@POST
	public void makeMove() {

	}
}
