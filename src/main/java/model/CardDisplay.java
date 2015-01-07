package model;

import game.Card;

public class CardDisplay {

	String name;
	int manaCost;
	int attack;
	int health;

	public CardDisplay(Card card) {
		name = card.name();
		manaCost = card.cost();
		attack = 0;
		health = 0;
	}

	public String getName() {
		return name;
	}

	public int getManaCost() {
		return manaCost;
	}

	public int getAttack() {
		return attack;
	}

	public int getHealth() {
		return health;
	}
}
