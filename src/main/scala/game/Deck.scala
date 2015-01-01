package game

import java.io.FileNotFoundException
import model.Util
import scala.io.Source

//Kirjelduses File
case class Deck(cards: Seq[Card])

object Deck {
  val arrayRegex = "\\[\\s*([\\s\\S]*)\\]$".r
  val test = """[ ("Boulderfist Ogre"
, 6
, MinionCard [] 6 7 False Nothing),

("Elven Archer"
, 4
, MinionCard [OnPlay [Choose [] [Health Relative (-1)]]] 4 5 False Nothing),

("Gnomish Inventor"
, 4
, MinionCard [OnPlay [DrawCard]] 2 4 False Nothing)]"""

  def unapply(str: String): Option[Deck] = str match {
    case arrayRegex(deck) => {
      val cards = deck.split(Util.arraySplit).map(Card.unapply).filter(_.isDefined).map(_.get)
      Some(Deck(cards))
    }
    case _ => None
  }

  def fromFile(fileName: String): Option[Deck] = {
    try {
      val file = Source.fromFile(fileName)
      val deck = unapply(file.mkString)
      file.close
      deck
    } catch {
      case ex: FileNotFoundException => {
        println("File " + fileName + " not found")
        None
      }
    }
  }
}