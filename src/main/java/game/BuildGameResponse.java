package game;

import java.util.List;
import scala.collection.JavaConversions;

/**
 * Created by Hans on 2.01.2015.
 *
 * Class for building JSON Strings to represent the board
 */
public class BuildGameResponse {

	public String buildhand(Hand hand) {
		//Hand hand = new Hand();
		for(Card s : JavaConversions.asJavaIterable(hand.getHand())) {

		}

	}

	public String buildCard(){

	}

	public String buildBoard() {

	}

	public String buildGame(){

	}
}
