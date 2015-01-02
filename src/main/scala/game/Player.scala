package game

import scala.collection.mutable._

class Player(name: String, deck: Deck) {
  var health: Int = 30
  var maxMana : Int = 0
  var mana: Int = 1
  var overload : Int =0
  //var maxMana: Int = 1
  //var mana: Int = 1
  var fatigue: Int = 1
  val cards = deck.cards.to[ArrayStack]
  val hand = new Hand
  val board = new ListBuffer[Card]

  val deathQueue = new Queue[Card]

  def getHealth(): Int = {
    board(0).cardType.asInstanceOf[MinionCard].getHealth
  }

/*  def drawCard(): Card = {
    val drawCard = cards.pop
    hand.addCard(drawCard)
    drawCard
  }*/
    board += new Card(name, 0, new MinionCard(Seq(), 30, 0, false, "Hero"))

  def popDeathQueue(): Unit = {
    deathQueue.foreach(card => {
      val minion = card.cardType.asInstanceOf[MinionCard]
      if (minion.minionType == "Hero") {
        println(name + " lost")
      } else if (!minion.effects.filter(f => f.isInstanceOf[OnDeath] || f.isInstanceOf[UntilDeath]).isEmpty) {
        println("Deathrattle: " + minion.effects.filter(_.isInstanceOf[OnDeath]))
        println("End of aura: " + minion.effects.filter(_.isInstanceOf[UntilDeath]))
      }
    })
    deathQueue.foreach(minion => board -= minion)
    deathQueue.clear
  }

  def drawCard(): Unit = {
    if (cards.isEmpty) {
      println("Fatigue " + fatigue)
      val change = board(0).cardType.asInstanceOf[MinionCard].relativeHp(-fatigue)

      //Do not pop death queue in this method
      change match {
        case Some(Killed()) => deathQueue += board(0)
        case _ =>
      }

      fatigue += 1
    } else {
      val drawCard = cards.pop
      println("Card draw: " + drawCard)
      hand.getHand += drawCard
    }
  }

  def playCard(index: Int): Unit = {
    val card = hand.getHand(index)
    //val card = hand(index)

    if (card.cost > mana) throw new IllegalArgumentException("Not enough mana")
    hand.getHand.remove(index)
    mana -= card.cost

    card.cardType.effects.filter(f => f.isInstanceOf[OnPlay] || f.isInstanceOf[UntilDeath]).flatMap(_.effects).foreach(f => {
      println("Battlecry: " + f)
      applyEffect(f, card)
    })
    if (card.cardType.isInstanceOf[MinionCard]) {
      board += card
    }
    popDeathQueue
  }

  def endTurn : Boolean = {
    true
  }

  def applyEffect(effect: EventEffect, source: Card): Unit = effect match {
    case DrawCard() => drawCard
    case All(filters, effects) => {
      println("Effect all creatures")

    }
    case Choose(filters, effects) => {
      println("Effect chosen creature")
      val (own, enemy) = Filter.filter(filters, source, Game.player1, Game.player2)
      println("Target candidates:\nOwn: " + own.mkString + "\nEnemy:" + enemy.mkString)
    }
    case Random(filters, effects) => println("Effect random creature")
  }

  def startTurn(): Unit = {
    if(maxMana<=10) maxMana+=1
    mana = maxMana - overload
    overload = 0
    drawCard()
    popDeathQueue()
    println("Your turn")
    println("Mana: " + mana)

    //Reset move counters
    board.foreach(_.movesLeft = 1)

    //Loop player moves
  }
}
