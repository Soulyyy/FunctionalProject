package game;

import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import scala.collection.JavaConversions;

/**
 * Created by Hans on 2.01.2015.
 * <p/>
 * Class for building JSON Strings to represent the board
 */
public class BuildGameResponse {
	/*
	 public static JSONArray buildHand(Hand hand) throws JSONException {
	 //Hand hand = new Hand();
	 JSONArray list = new JSONArray();
	 for(Card card : JavaConversions.asJavaIterable(hand.getHand())) {
	 list.put(buildCard(card));

	 }
	 return list;

	 }
	 */

	public static JSONObject buildCard(Card card) throws JSONException {
		JSONObject resp = new JSONObject();
		resp.put("name", card.name());
		resp.put("cost", card.cost());
		resp.put("type", card.cardType());
		return resp;

	}

	public String buildBoard() {
		return "tere";
	}

	public static JSONObject buildGame(Game game) throws JSONException {
		JSONObject resp = new JSONObject();
//		resp.put("hand", buildHand(game.currentPlayer().hand()));
		resp.put("lifeTotal", game.currentPlayer().getHealth());
		resp.put("opponentLife", game.opponent().getHealth());
		resp.put("opponentHandSize", game.opponent().hand().size());
		//resp.put("playerBoard",game.currentPlayer().)
		return resp;
	}
}
