package game

import scala.collection.mutable._

class Player(name: String, deck: Deck) {
  var health: Int = 30
  var maxMana : Int = 0
  var mana: Int = 1
  var overload : Int =0
  val cards = deck.cards.to[ArrayStack]
  val hand = new Hand
  val board = new ListBuffer[Card]

  board += new Card(name, 0, new MinionCard(null, 30, 0, false, "Hero"))

  def drawCard(): Card = {
    val drawCard = cards.pop
    hand.addCard(drawCard)
    drawCard
  }

  def playCard(index: Int): Unit = {
    val card = hand(index)

    if (card.cost > mana) throw new IllegalArgumentException("Build more pylons")
    mana -= card.cost

    val effects = card.cardType match {
      case ct: MinionCard => ct.effects
      case ct: SpellCard => ct.effects
    }

    effects.filter(_.isInstanceOf[OnPlay]) //TODO: Apply

    board += hand.remove(index)
  }

  def startTurn(): Unit = {
    if(maxMana<=10) maxMana+=1
    mana = maxMana - overload
    overload = 0
    drawCard()

    //Reset move counters
    board.foreach(_.movesLeft = 1)
  }
}
