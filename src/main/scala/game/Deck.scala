package game

import model.Util

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
}