package game

class Game(player1: Player, player2: Player) {
  //val hand = player1.hand
  val board = player1.board

  def currentHand(currentPlayer : Player) : String = {
    var hand = currentPlayer.hand

    return "tere"
  }
  def opopnentHandSize(opponent : Player) : String = {
    val handSize = opponent.hand.size
    handSize
  }

  def currentBoard() : String => {

  }
}


