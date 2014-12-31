package game

abstract class CardType

class Card(name: String, cost: Int, cardType: CardType) {
  val this.cost = cost
  val this.name = name
}
