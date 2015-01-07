package game

import model.Util
import scala.collection.mutable._

class Player(val name: String, deck: Deck) {
  var maxMana: Int = 0
  var mana: Int = 0
  var overload: Int = 0
  var fatigue: Int = 1

  val cards = util.Random.shuffle(deck.cards).to[ArrayStack]
  val hand = new HashMap[Int, Card]
  val board = new HashMap[Int, MinionCard]

  val hero = new MinionCard(Seq(), 30, 0, false, "Hero")
  hero.owner = Some(this)
  new Card(name, 0, hero) //Generate unique id for hero
  board += hero.id -> hero

  def getHealth(): Int = {
    hero.getHealth
  }

  def drawCard(): Unit = {
    if (cards.isEmpty) {
      println("Pakk on tühi, kangelane kaotab " + fatigue + " elupunkti")
      hero.relativeHp(-fatigue)

      fatigue += 1
    } else {
      val drawCard = cards.pop
      println("Pakist võeti: " + drawCard)
      hand += drawCard.id -> drawCard
    }
  }

  def playCard(index: Int): Unit = {
    val card = hand(index)

    if (card.cost > mana) {
      println("Pole piisavalt tegutsemispunkte")
      return
    }

    hand -= index
    mana -= card.cost

    if (card.cardType.isInstanceOf[MinionCard]) {
      val minion = card.cardType.asInstanceOf[MinionCard]
      minion.owner = Some(this)
      board += card.id -> minion

      println("Effects: " + minion.effects)

      minion.effects.filter(_.isInstanceOf[OnDamage]).flatMap(_.effects).foreach(f => {
        minion.damageEffects += (() => applyEffect(f, card))
      })

      minion.effects.filter(_.isInstanceOf[OnDeath]).flatMap(_.effects).foreach(f => {
        minion.deathEffects += (() => applyEffect(f, card))
      })

      minion.effects.filter(_.isInstanceOf[UntilDeath]).flatMap(_.effects).foreach(f => {
        minion.deathEffects += f.applyOn(card) //Apply and return reverse operation
      })
    }

    card.cardType.effects.filter(f => f.isInstanceOf[OnPlay]).flatMap(_.effects).foreach(f => {
      applyEffect(f, card)
      Game().popDamageQueue
    })
  }

  def endTurn: Boolean = {
    true
  }

  def attack(minion: MinionCard): Unit = {
    if (minion.getAttack == 0) {
      println("Ründepunktideta kaart ei saa rünnata")
      return
    }
    if (minion.movesLeft == 0) {
      println("Sellel kaardil pole ründamiseks käike")
      return
    }
    val taunt = Game().opponent.board.values.find(_.taunt) != None
    var targets = Game().opponent.board.values
    if (taunt) {
      targets = targets.filter(_.taunt)
    }

    val selection = Util.playerConsoleInput("Vali sihtmärk: ", Seq(), Seq(), targets.toSeq)
    if (selection != None) {
      val targetMinion = selection.get.cardType.asInstanceOf[MinionCard]
      minion.relativeHp(-targetMinion.getAttack)
      targetMinion.relativeHp(-minion.getAttack)
      minion.movesLeft -= 1
    }
  }

  def applyEffect(effect: EventEffect, source: Card) = effect match {
    case DrawCard() =>
      drawCard
    case All(filters, effects) => {
      val (own, enemy) = Filter.filter(filters, source, Game().currentPlayer, Game().opponent)
      val all = own ++ enemy
      all.foreach(minion => effects.foreach(_.applyOn(minion)()))
    }
    case Choose(filters, effects) => {
      val (own, enemy) = Filter.filter(filters, source, Game().currentPlayer, Game().opponent)
      println
      val selection = Util.playerConsoleInput("Vali sihtmärk: ", Seq(), own, enemy)
      if (selection != None) {
        println("Sihtmärgiks valiti: " + selection.get)
        effects.foreach(_.applyOn(selection.get.cardType.asInstanceOf[MinionCard])())
      }
    }
    case Random(filters, effects) => {
      val (own, enemy) = Filter.filter(filters, source, Game().currentPlayer, Game().opponent)
      val selection = Util.randomInput(Seq(), own, enemy)
      if (selection != None) {
        println("Sihtmärgiks valiti: " + selection.get)
        effects.foreach(_.applyOn(selection.get.cardType.asInstanceOf[MinionCard])())
      }
    }
  }

  def startTurn(): Unit = {
    if (maxMana <= 10) maxMana += 1
    mana = maxMana

    println
    println("---------------------------------------")
    println("Mängija " + name + " kord")

    drawCard
    Game().popDamageQueue
    Game().popDeathQueue

    //Reset move counters
    board.values.foreach(_.movesLeft = 1)

    //Loop player moves
    var endTurn = false
    while (!endTurn) {
      println
      println("Tegutsemispunkte: " + mana)
      val selection = Util.playerConsoleInput("Vali kaart mida mängida, väära valiku puhul käik lõpetatakse: ", hand.values.toSeq, board.values.toSeq, Game().opponentOf(this).board.values.toSeq)
      selection match {
        case Some(card: Card) => {
          if (hand.contains(card.id)) {
            playCard(card.id)
          } else if (board.contains(card.id)) {
            attack(card.cardType.asInstanceOf[MinionCard])
          } else {
            println("Vastase kaartidega ei saa mängida")
          }
        }
        case _ => endTurn = true
      }
      Game().popDamageQueue
      Game().popDeathQueue

      if (Game().opponentOf(this).getHealth <= 0) {
        if (getHealth > 0) {
          Game().end(Some(this))
        } else {
          None
        }
        endTurn = true
      }
    }
  }
}
