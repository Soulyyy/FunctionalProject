package game

import scala.collection.mutable.ListBuffer

class Hand {
  var hand : ListBuffer[Card] = new ListBuffer[Card]
  //var ListBuffer hand = new ListBuffer[Card]

  def getHand: ListBuffer[Card] = {
    hand
  }

  def validateHand: Boolean = {
    if (hand.size > 10) false
    true
  }

  def addCard(card: Card) = {
    if (validateHand) hand += card
  }

  def playCard(card : Card) = {
    hand-=card
  }

  def getSize : Int = {
    hand.size
  }
}