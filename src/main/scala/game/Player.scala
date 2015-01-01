package game

class Player(name: String, deck: Deck) {
  val this.name: String = name
  var health: Int = 30
  val this.deck = deck
  val hand: Hand
  val board: Board
}
