package game

import model._
import scala.collection.mutable.ArrayBuffer

sealed trait CardType {
  var _parent: Option[Card] = None

  def parent = _parent.get

  def id = parent.id
  def name = parent.name
  def cost = parent.cost

  def effects: Seq[Effect]
}

case class Card(name: String, cost: Int, cardType: CardType) {
  Card.idCounter += 1
  val id = Card.idCounter

  cardType._parent = Some(this)

  override def toString() = cardType.toString
}

object Card {
  val cardRegex = "\\(\\\"(.+)\\\"\\s+,\\s\\(?(-?\\d+)\\)?\\s+,\\s(.*)\\)".r

  var idCounter = 0

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

case class MinionCard(effects: Seq[Effect], private var health: Int, private var attack: Int, var taunt: Boolean, minionType: String) extends CardType {
  private var dynHealth = 0
  private var dynAttack = 0
  var dynTaunt = taunt
  var movesLeft = 0

  val damageEffects = new ArrayBuffer[() => Unit]
  val deathEffects = new ArrayBuffer[() => Unit]

  var owner: Option[Player] = None

  def relativeHp(change: Int): Unit = {
    if (owner == None) {
      throw new IllegalStateException("Card must have owner")
    }
    //Killed only on first change to negative, avoid multiple deathreattle
    if (change < 0) {
      Game().damageQueue += this
      if (getHealth > 0 && getHealth + change <= 0) {
        Game().deathQueue += this
      }
    }
    dynHealth += change
  }

  //Heal & buff removal
  def buffHp(change: Int): Unit = {
    if (owner == None) {
      throw new IllegalStateException("Card must have owner")
    }

    //If a buff such as stormwind expires, don't kill the minion
    if (getHealth + change > 0) {
      dynHealth += change
    }
  }

  def buffAtt(change: Int): Unit = {
    if (owner == None) {
      throw new IllegalStateException("Card must have owner")
    }

    if (getAttack + change >= 0) {
      dynAttack += change
    }
  }

  def relativeAtt(change: Int): Unit = {
    if (owner == None) {
      throw new IllegalStateException("Card must have owner")
    }
    dynAttack += change
  }

  //Do not update aoe buff on absolute change, ie 2/2 + stormwind + equality = 3/2
  def setHp(hp: Int): Unit = {
    if (hp < 0) throw new IllegalArgumentException("Absolute change must be positive")
    if (owner == None) {
      throw new IllegalStateException("Card must have owner")
    }
    health = hp
  }

  def setAtt(att: Int): Unit = {
    if (att < 0) throw new IllegalArgumentException("Absolute change must be positive")
    if (owner == None) {
      throw new IllegalStateException("Card must have owner")
    }
    attack = att
  }

  def getHealth(): Int = health + dynHealth
  def getAttack(): Int = math.max(attack + dynAttack, 0)
  def getTaunt(): Boolean = dynTaunt

  override def toString(): String = {
    val str = "(" + parent.id + "):(" + parent.name + ", Cost:" + parent.cost + ", Att:" + getAttack + ", Hp:" + getHealth
    if (getTaunt) {
      str + ", Taunt)"
    } else {
      str + ")"
    }
  }

  def onDamage(): Unit = {
    damageEffects.foreach(_())
  }

  def onDeath(): Unit = {
    deathEffects.foreach(f => { f(); println(f) })
  }
}

case class SpellCard(effects: Seq[Effect]) extends CardType {
  override def toString(): String = {
    "(" + parent.id + "):(" + parent.name + ", Cost:" + parent.cost + ")"
  }
}
