package game

import scala.collection.mutable.ListBuffer

class Hand {
  ListBuffer[Card] hand;

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
}