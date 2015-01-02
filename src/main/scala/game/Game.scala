package game

class Game(player1: Player, player2: Player) {

  var currentPlayer: Player = player1
  var opponent: Player = player2
  //val hand = player1.hand
  val board = player1.board

  def switchPriority = {
    val tmp: Player = currentPlayer
    currentPlayer = opponent
    opponent = tmp
  }

  def currentHand(currentPlayer: Player): String = {
    var hand = currentPlayer.hand

    "tere"
  }

  def opponentHandSize(opponent: Player): Int = {
    val handSize = opponent.hand.getSize
    handSize
  }

  def currentBoard: String = {
    "tere"
  }

/*  def play = {
    while(true) {
      currentPlayer.drawCard()
      var input : Int =scala.io.StdIn.readInt()
      while(input != 700) {
        input  = scala.io.StdIn.readInt()
        println(state)


      }
    }
  }*/


}

//For testing
object Game {
  val player1 = new Player("Player1", Deck.fromFile("deck.txt").get)
  val player2 = new Player("Player2", Deck.fromFile("deck.txt").get)
}
