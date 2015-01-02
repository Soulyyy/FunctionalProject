package game

import model._

sealed trait CardType {
  def effects: Seq[Effect]
}

case class Card(name: String, cost: Int, cardType: CardType) {
  var movesLeft = 0
}

object Card {
  val cardRegex = "\\(\\\"(.+)\\\"\\s+,\\s\\(?(-?\\d+)\\)?\\s+,\\s(.*)\\)".r

  val test = """("Gnomish Inventor"
, 4
, MinionCard [OnPlay [DrawCard]] 2 4 False Nothing)"""

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
      val effects = Util.parseArray(effect).map(Effect.unapply).filter(_.isDefined).map(_.get)
      Some(SpellCard(effects))
    }
    case minionCardRegex(effect, AsInt(health), AsInt(attack), AsBoolean(taunt), _, minionType) => {
      val effects = Util.parseArray(effect).map(Effect.unapply).filter(_.isDefined).map(_.get)
      val mt = minionType match { case "Nothing" => ""; case typeStr => typeStr }
      Some(MinionCard(effects, health, attack, taunt, mt))
    }
    case _ => None
  }
}

sealed trait Change
case class Killed() extends Change
case class Damaged() extends Change

case class MinionCard(effects: Seq[Effect], private var health: Int, private var attack: Int, var taunt: Boolean, minionType: String) extends CardType {
  private var dynHealth = 0
  private var dynAttack = 0

  def relativeHp(change: Int): Option[Change] = {
    //Killed only on first change to negative, avoid multiple deathreattle
    if (health + dynHealth > 0 && health + dynHealth + change > 0) {
      health += change
      if (change < 0) {
        Some(Damaged())
      } else {
        None
      }
    } else {
      health += change
      Some(Killed())
    }
  }

  def relativeAtt(change: Int): Unit = {
    attack += math.max(change, 0)
  }

  //Do not update aoe buff on absolute change, ie 2/2 + stormwind + equality = 3/2
  def setHp(hp: Int): Unit = {
    if (hp < 0) throw new IllegalArgumentException("Absolute change must be positive")
    health = hp
  }

  def setAtt(att: Int): Unit = {
    if (att < 0) throw new IllegalArgumentException("Absolute change must be positive")
    attack = att
  }

  def getHealth(): Int = health + dynHealth
  def getAttack(): Int = attack + dynAttack
}

case class SpellCard(effects: Seq[Effect]) extends CardType
