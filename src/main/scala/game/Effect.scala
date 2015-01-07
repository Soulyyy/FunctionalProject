package game

import model._

sealed trait Effect {
  def effects: Seq[EventEffect]
}

object Effect {
  val effectRegex = "\\(?(\\S+) \\[(.*?)\\]\\)?".r
  def unapply(str: String): Option[Effect] = str match {
    case effectRegex(ident, eventEffect) => {
      val effects = Util.parseArray(eventEffect).map(EventEffect.unapply).filter(_.isDefined).map(_.get)
      ident match {
        case "OnPlay" => Some(OnPlay(effects))
        case "UntilDeath" => Some(UntilDeath(effects))
        case "OnDamage" => Some(OnDamage(effects))
        case "OnDeath" => Some(OnDeath(effects))
        case _ => None
    }
      }
    case _ => None
  }
}

case class OnPlay(effects: Seq[EventEffect]) extends Effect
case class UntilDeath(effects: Seq[EventEffect]) extends Effect
case class OnDamage(effects: Seq[EventEffect]) extends Effect
case class OnDeath(effects: Seq[EventEffect]) extends Effect

sealed trait EventEffect {

  //Apply and return method that reverses
  def applyOn(source: Card): () => Unit = this match {
    case DrawCard() => ???
    case All(filters, effects) => {
      val (own, enemy) = Filter.filter(filters, source, Game().currentPlayer, Game().opponent)
      val all = own ++ enemy
      all.foreach(minion => effects.foreach(_.applyOn(minion)()))
      () => all.foreach(minion => effects.foreach(_.unApplyOn(minion)()))
    }

    case Choose(filters, effects) => {
      val (own, enemy) = Filter.filter(filters, source, Game().currentPlayer, Game().opponent)
      println
      val selection = Util.playerConsoleInput("Vali sihtmärk: ", Seq(), own, enemy)
      if (selection != None) {
        println("Sihtmärgiks valiti: " + selection.get)
        effects.foreach(_.applyOn(selection.get.cardType.asInstanceOf[MinionCard])())
        () => effects.foreach(_.unApplyOn(selection.get.cardType.asInstanceOf[MinionCard])())
      } else {
        ???
      }
    }
    case Random(filters, effects) => {
      val (own, enemy) = Filter.filter(filters, source, Game().currentPlayer, Game().opponent)
      val selection = Util.randomInput(Seq(), own, enemy)
      if (selection != None) {
        println("Sihtmärgiks valiti: " + selection.get)
        effects.foreach(_.applyOn(selection.get.cardType.asInstanceOf[MinionCard])())
        () => effects.foreach(_.unApplyOn(selection.get.cardType.asInstanceOf[MinionCard])())
      } else {
        ???
      }
    }
  }
}

object EventEffect {
  val effectRegex = "\\(?(\\S+) \\[(.*?)\\] \\[(.*?)\\]\\)?".r

  /**
   * Expected output
   * Some(All(WrappedArray(Any(WrappedArray(Not(WrappedArray(AnyCreature(), AnyHero())), Self()))),WrappedArray(Health(Relative(),-1), Attack(Relative(),-1))))
   */
  val test = "All [Any [(Not [(AnyCreature), (AnyHero)]), (Self)]] [(Health Relative (-1)), (Attack Relative (-1))]"

  def unapply(str: String): Option[EventEffect] = {
    str match {
      case Util.literalRegex("DrawCard") => Some(DrawCard())
      case effectRegex(ident, filter, creatureEffect) => {
        val filters = Util.parseArray(filter).map(Filter.unapply).filter(_.isDefined).map(_.get)
        val effects = Util.parseArray(creatureEffect).map(CreatureEffect.unapply).filter(_.isDefined).map(_.get)
        ident match {
          case "All" => Some(All(filters, effects))
          case "Choose" => Some(Choose(filters, effects))
          case "Random" => Some(Random(filters, effects))
          case _ => None
      }
        }
      case _ => {
        None
      }
    }
  }
}

case class All(filter: Seq[Filter], effect: Seq[CreatureEffect]) extends EventEffect

case class Choose(filter: Seq[Filter], effect: Seq[CreatureEffect]) extends EventEffect

case class Random(filter: Seq[Filter], effect: Seq[CreatureEffect]) extends EventEffect

case class DrawCard() extends EventEffect

sealed trait CreatureEffect {
  def applyOn(minion: MinionCard): (() => Unit) = {
    this match {
      case Health(changeType: ChangeType, change: Int) => {
        changeType match {
          case Relative() => (() => minion.relativeHp(change))
          case Absolute() => (() => minion.setHp(change))
      }
        }
      case Attack(changeType: ChangeType, change: Int) => {
        changeType match {
          case Relative() => (() => minion.relativeAtt(change))
          case Absolute() => (() => minion.setAtt(change))
      }
        }
      case Taunt(taunt: Boolean) => {
        (() => minion.dynTaunt = taunt)
      }
    }
  }

  def unApplyOn(minion: MinionCard): (() => Unit) = {
    this match {
      case Health(changeType: ChangeType, change: Int) => {
        changeType match {
          case Relative() => (() => minion.buffHp(-change))
          case Absolute() => (() => throw new UnsupportedOperationException("Can't undo absolute hp change"))
      }
        }
      case Attack(changeType: ChangeType, change: Int) => {
        changeType match {
          case Relative() => (() => minion.buffHp(-change))
          case Absolute() => (() => throw new UnsupportedOperationException("Can't undo absolute att change"))
      }
        }
      case Taunt(taunt: Boolean) => {
        (() => minion.dynTaunt = minion.taunt)
      }
    }
  }
}

object CreatureEffect {
  val healthRegex = "\\(?Health (\\S*?) \\(?(-?\\d+)\\)?\\)?".r
  val attackRegex = "\\(?Attack (\\S*?) \\(?(-?\\d+)\\)?\\)?".r
  val tauntRegex = "\\(?Taunt (True|False)\\)?".r

  def unapply(str: String): Option[CreatureEffect] = str match {
    case healthRegex(ChangeType(changeType), AsInt(health)) => Some(Health(changeType, health))
    case attackRegex(ChangeType(changeType), AsInt(attack)) => Some(Attack(changeType, attack))
    case tauntRegex(AsBoolean(taunt)) => Some(Taunt(taunt))
    case _ => None
  }
}

case class Health(changeType: ChangeType, health: Int) extends CreatureEffect

case class Attack(changeType: ChangeType, attack: Int) extends CreatureEffect

case class Taunt(taunt: Boolean) extends CreatureEffect

abstract class ChangeType

object ChangeType {
  def unapply(str: String): Option[ChangeType] = str match {
    case Util.literalRegex("Relative") => Some(Relative())
    case Util.literalRegex("Absolute") => Some(Absolute())
    case _ => None
  }
}

case class Relative() extends ChangeType

case class Absolute() extends ChangeType