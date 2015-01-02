package game

//For testing
object Game {
  val player1 = new Player("Player1", Deck.fromFile("deck.txt").get)
  val player2 = new Player("Player2", Deck.fromFile("deck.txt").get)
}

class Game(player1: Player, player2: Player)
