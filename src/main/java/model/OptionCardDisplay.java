package model;

import game.Card;

public class OptionCardDisplay extends CardDisplay {

	int id;

	public OptionCardDisplay(int id, Card card) {
		super(card);
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
