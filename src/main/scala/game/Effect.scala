package game

import model._

abstract class Effect

object Effect {
  val effectRegex = "\\(?(\\S+) \\[(.*?)\\]\\)?".r
  def unapply(str: String): Option[Effect] = str match {
    case effectRegex(ident, eventEffect) => {
      val effects = eventEffect.split(Util.arraySplit).map(EventEffect.unapply).filter(_.isDefined).map(_.get)
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

abstract class EventEffect

object EventEffect {
  val effectRegex = "\\(?(\\S+) \\[(.*?)\\] \\[(.*?)\\]\\)?".r

  /**
   * Expected output
   * Some(All(WrappedArray(Any(WrappedArray(Not(WrappedArray(AnyCreature(), AnyHero())), Self()))),WrappedArray(Health(Relative(),-1), Attack(Relative(),-1))))
   */
  val test = "All [Any [(Not [(AnyCreature), (AnyHero)]), (Self)]] [(Health Relative (-1)), (Attack Relative (-1))]"

  def unapply(str: String): Option[EventEffect] = str match {
    case Util.literalRegex("DrawCard") => Some(DrawCard())
    case effectRegex(ident, filter, creatureEffect) => {
      val filters = filter.split(Util.arraySplit).map(Filter.unapply).filter(_.isDefined).map(_.get)
      val effects = creatureEffect.split(Util.arraySplit).map(CreatureEffect.unapply).filter(_.isDefined).map(_.get)
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

case class All(filter: Seq[Filter], effect: Seq[CreatureEffect]) extends EventEffect

case class Choose(filter: Seq[Filter], effect: Seq[CreatureEffect]) extends EventEffect

case class Random(filter: Seq[Filter], effect: Seq[CreatureEffect]) extends EventEffect

case class DrawCard() extends EventEffect

abstract class CreatureEffect

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