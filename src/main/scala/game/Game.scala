package game

import scala.collection.mutable.HashMap
import scala.collection.mutable.Queue

class Game(player1: Player, player2: Player) {

  var currentPlayer = player1
  var opponent = player2
  var gameOver = false

  val deathQueue = new Queue[MinionCard]
  val damageQueue = new Queue[MinionCard]
  val untilDeathCreatures = new HashMap[Int, MinionCard]

  def popDeathQueue(): Unit = {
    if (!deathQueue.isEmpty) {
      println("Suremise järjekord: " + deathQueue)
    }
    deathQueue.foreach(_.onDeath)
    deathQueue.foreach(minion => minion.owner.get.board -= minion.id)
    deathQueue.clear
  }

  def popDamageQueue(): Unit = {
    if (!damageQueue.isEmpty) {
      println("Vigastuste järjekord: " + damageQueue)
    }
    damageQueue.foreach(_.onDamage)
    damageQueue.clear
  }

  def opponentOf(player: Player): Player = {
    if (player == player1) {
      player2
    } else {
      player1
    }
  }

  private def switchPriority = {
    val tmp = currentPlayer
    currentPlayer = opponent
    opponent = tmp
  }

  def registerInputHandler() = ???

  def opponentHandSize(): Int = {
    opponent.hand.size
  }

  def endTurn(): Unit = {
    switchPriority
    currentPlayer.startTurn
  }

  def start(): Unit = {
    currentPlayer.startTurn
    while (!gameOver) {
      switchPriority
      currentPlayer.startTurn
    }
  }

  def end(winner: Option[Player]): Unit = {
    if (winner == None) {
      println("Mäng lõppes viigiga")
    } else {
      println("Mängija " + winner.get.name + " võitis")
    }
    gameOver = true
  }
}

//For testing
object Game {
  val player1 = new Player("Player1", Deck.fromFile("deck.txt").get)
  val player2 = new Player("Player2", Deck.fromFile("deck.txt").get)

  val game = new Game(player1, player2)

  def apply(): Game = {
    game
  }

  def main(args: Array[String]) = {
    game.start
  }
}
