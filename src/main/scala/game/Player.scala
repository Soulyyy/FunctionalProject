package game

import model.Util
import scala.collection.mutable._

class Player(name: String, deck: Deck) {
  var maxMana: Int = 0
  var mana: Int = 0
  var overload: Int = 0
  var fatigue: Int = 1

  val cards = util.Random.shuffle(deck.cards).to[ArrayStack]
  val hand = new HashMap[Int, Card]
  val board = new HashMap[Int, Card]
  val deathQueue = new Queue[Card]

  val hero = new Card(name, 0, new MinionCard(Seq(), 30, 0, false, "Hero"))
  board += hero.id -> hero

  def getHealth(): Int = {
    hero.cardType.asInstanceOf[MinionCard].getHealth
  }

  def popDeathQueue(): Unit = {
    deathQueue.foreach(card => {
      val minion = card.cardType.asInstanceOf[MinionCard]
      if (minion.minionType == "Hero") {
        println(name + " lost")
        Game().end
      } else if (!minion.effects.filter(f => f.isInstanceOf[OnDeath] || f.isInstanceOf[UntilDeath]).isEmpty) {
        println("Deathrattle: " + minion.effects.filter(_.isInstanceOf[OnDeath]))
        println("End of aura: " + minion.effects.filter(_.isInstanceOf[UntilDeath]))
      }
    })
    deathQueue.foreach(minion => board -= minion.id)
    deathQueue.clear
  }

  def drawCard(): Unit = {
    if (cards.isEmpty) {
      println("Fatigue " + fatigue)
      val change = hero.cardType.asInstanceOf[MinionCard].relativeHp(-fatigue)

      //Do not pop death queue in this method
      change match {
        case Some(Killed()) => deathQueue += board(0)
        case _ =>
      }

      fatigue += 1
    } else {
      val drawCard = cards.pop
      println("Card draw: " + drawCard)
      hand += drawCard.id -> drawCard
    }
  }

  def playCard(index: Int): Unit = {
    val card = hand(index)

    if (card.cost > mana) {
      println("Not enough mana")
      return
    }
    hand -= index
    mana -= card.cost

    card.cardType.effects.filter(f => f.isInstanceOf[OnPlay] || f.isInstanceOf[UntilDeath]).flatMap(_.effects).foreach(f => {
      println("Battlecry: " + f)
      applyEffect(f, card)
    })
    if (card.cardType.isInstanceOf[MinionCard]) {
      board += card.id -> card
    }
    popDeathQueue
  }

  def endTurn: Boolean = {
    true
  }

  def attack(source: Card): Unit = {
    val minion = source.cardType.asInstanceOf[MinionCard];
    if (minion.getAttack == 0) {
      println("Ründepunktideta kaart ei saa rünnata")
      return
    }
    if (source.movesLeft == 0) {
      println("Sellel kaardil pole ründamiseks käike")
      return
    }
    val taunt = Game().opponent.board.values.find(_.cardType.asInstanceOf[MinionCard].taunt) != None
    var targets = Game().opponent.board.values
    if (taunt) {
      targets = targets.filter(_.cardType.asInstanceOf[MinionCard].taunt)
    }

    val selection = Util.playerInput("Vali sihtmärk: ", Seq(), Seq(), targets.toSeq)
    if (selection != None) {
      val targetMinion = selection.get.cardType.asInstanceOf[MinionCard]
      //TODO: add damage/death callbacks -> add to damaged/death queue, resolve queues after move
      minion.relativeHp(-targetMinion.getAttack)
      targetMinion.relativeHp(-minion.getAttack)
    }
  }

  def applyEffect(effect: EventEffect, source: Card): Boolean = effect match {
    case DrawCard() =>
      drawCard; true
    case All(filters, effects) => {
      println("Effect all creatures")
      val (own, enemy) = Filter.filter(filters, source, Game().currentPlayer, Game().opponent)
      val all = own ++ enemy
      all.foreach(minion => effects.foreach(_.applyOn(minion)))
      true
    }
    case Choose(filters, effects) => {
      val (own, enemy) = Filter.filter(filters, source, Game().currentPlayer, Game().opponent)
      println
      val selection = Util.playerInput("Vali sihtmärk: ", Seq(), own, enemy)
      if (selection == None) false
      println("Apply effect to " + selection.get)
      effects.foreach(_.applyOn(selection.get))
      true
    }
    case Random(filters, effects) => {
      val (own, enemy) = Filter.filter(filters, source, Game().currentPlayer, Game().opponent)
      val selection = Util.randomInput(Seq(), own, enemy)
      if (selection != None) {
        println("Apply effect to " + selection.get)
        effects.foreach(_.applyOn(selection.get))
      }
      true
    }
  }

  def startTurn(): Unit = {
    if (maxMana <= 10) maxMana += 1
    mana = maxMana - overload
    overload = 0

    println
    println("Player " + name + " turn")

    drawCard
    popDeathQueue

    //Reset move counters
    board.values.foreach(_.movesLeft = 1)

    //Loop player moves
    var endTurn = false
    while (!endTurn) {
      println
      println("Mana: " + mana)
      val selection = Util.playerInput("Vali kaart mida mängida: ", hand.values.toSeq, board.values.toSeq, Seq())
      selection match {
        case Some(card: Card) => {
          if (hand.contains(card.id)) {
            playCard(card.id)
          } else {
            attack(card)
          }
        }
        case _ => endTurn = true
      }
      popDeathQueue
    }
  }
}
