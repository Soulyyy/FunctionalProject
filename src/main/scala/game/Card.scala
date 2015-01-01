package game

import model._

abstract class CardType

case class Card(name: String, cost: Int, cardType: CardType)

object Card {
  val cardRegex = "\\(\\\"(.+)\\\"\\s,\\s\\(?(-?\\d+)\\)?\\s,\\s(.*)\\)".r

  val test = """("Boulderfist Ogre"
, 6
, MinionCard [] 6 7 False Nothing)"""

  def unapply(str: String): Option[Card] = str match {
    case cardRegex(name, AsInt(cost), CardType(cardType)) => Some(Card(name, cost, cardType))
    case _ => None
  }
}

object CardType {
  val spellCardRegex = "\\(?SpellCard \\[(.*?)\\]\\)?$".r
  val minionCardRegex = "\\(?MinionCard \\[(.*?)\\] \\(?(-?\\d+)\\)? \\(?(-?\\d+)\\)? \\(?(True|False)\\)? \\(?(?:Just (\\S+)|(Nothing))\\)?\\)?$".r

  val test = "MinionCard [] 6 7 False Nothing"

  def unapply(str: String): Option[CardType] = str match {
    case spellCardRegex(effect) => {
      println("Kurwa")
      val effects = effect.split(Util.arraySplit).map(Effect.unapply).filter(_.isDefined).map(_.get)
      Some(SpellCard(effects))
    }
    case minionCardRegex(effect, AsInt(health), AsInt(attack), AsBoolean(taunt), _, minionType) => {
      println("Almost")
      val effects = effect.split(Util.arraySplit).map(Effect.unapply).filter(_.isDefined).map(_.get)
      val mt = minionType match { case "Nothing" => ""; case typeStr => typeStr }
      println("minionType: " + mt)
      Some(MinionCard(effects, health, attack, taunt, mt))
    }
    case _ => None
  }
}

case class MinionCard(effects: Seq[Effect], health: Int, attack: Int, taunt: Boolean, minionType: String) extends CardType {
  var this.health = health
  var this.attack = attack
  var dynHealth: Int = 0
  var dynAttack: Int = 0
  var this.taunt = taunt

}

case class SpellCard(effects: Seq[Effect]) extends CardType
