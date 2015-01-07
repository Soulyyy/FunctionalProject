package model;

import game.*;
import java.util.ArrayList;
import java.util.List;
import scala.collection.JavaConversions;

public class GameDisplay {

	String[] hand;
	int lifeTotal;
	int opponentLife;
	int opponentHandSize;

	public GameDisplay(Game game) {
		List<String> cards = new ArrayList<>();
		for(Card c : JavaConversions.asJavaIterable(game.currentPlayer().hand().values())) {
			cards.add(c.toString());
		}
		hand = cards.toArray(new String[0]);
		lifeTotal = game.currentPlayer().getHealth();
		opponentLife = game.opponent().getHealth();
		opponentHandSize = game.opponent().hand().size();
	}

	public String[] getHand() {
		return hand;
	}

	public int getLifeTotal() {
		return lifeTotal;
	}

	public int getOpponentLife() {
		return opponentLife;
	}

	public int getOpponentHandSize() {
		return opponentHandSize;
	}
}
