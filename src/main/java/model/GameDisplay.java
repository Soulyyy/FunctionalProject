package model;

import game.*;
import java.util.ArrayList;
import java.util.List;
import scala.collection.JavaConversions;

public class GameDisplay {

	CardDisplay[] hand;
	int lifeTotal;
	int opponentLife;
	int opponentHandSize;

	public GameDisplay(Game game) {
		List<CardDisplay> cards = new ArrayList<>();
		for(Card c : JavaConversions.asJavaIterable(game.currentPlayer().hand().hand())) {
			cards.add(new CardDisplay(c));
		}
		hand = cards.toArray(new CardDisplay[0]);
		lifeTotal = game.currentPlayer().getHealth();
		opponentLife = game.opponent().getHealth();
		opponentHandSize = game.opponent().hand().getSize();
	}

	public CardDisplay[] getHand() {
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
