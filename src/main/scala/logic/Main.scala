/*
package logic

import game.{BuildGameResponse, Deck, Game, Player}
;

/**
 * Created by Hans Peeter Tulmin on 22.11.2014.
 */
object Main {

  var state : String =""

  def main(args: Array[String]): Unit = {
    val p1: Player = new Player("p1", Deck.fromFile("deck.txt").get)
    val p2: Player = new Player("p2", Deck.fromFile("deck.txt").get)
    val game: Game = new Game(p1, p2)
    state = BuildGameResponse.buildGame(game).toString
    println(state)
    while(true) {
      game.currentPlayer.startTurn()
      var input : Int = 999
      while(input != 700) {
        try{
        input  = scala.io.StdIn.readInt()

        } catch {
          case e :NumberFormatException => {
            println("Sisestasite mitte arvu. Valige arv. Kui soovite käiku lõpetada, valige 700")
            input = 999
          }
        }
        println(state)


      }
    }

  }
}
*/