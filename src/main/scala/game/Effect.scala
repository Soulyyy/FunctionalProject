package game

import model.AsInt

abstract class Effect {
  val effectType: String
  val eventEffect: EventEffect

}

abstract class EventEffect

class All(filter: Filter, effect: CreatureEffect) extends EventEffect {
  val this.filter = filter
  val this.effect = effect
}

class Choose(filter: Filter, effect: CreatureEffect) extends EventEffect {
  val this.filter = filter
  val this.effect = effect
}

class Random(filter: Filter, effect: CreatureEffect) extends EventEffect {
  val this.filter = filter
  val this.effect = effect
}

class DrawCard extends EventEffect

abstract class CreatureEffect

object CreatureEffect {
  val healthRegex = "\\(?Health (\\S*?) \\(?(-?\\d+)\\)?\\)?".r
  val attackRegex = "\\(?Attack (\\S*?) \\(?(-?\\d+)\\)?\\)?".r
  val tauntRegex = "\\(?Taunt (True|False)\\)?".r
  def unapply(str: String): Option[CreatureEffect] = str match {
    case healthRegex(ChangeType(changeType), AsInt(health)) => Some(Health(changeType, health))
    case _ => None
  }
}

case class Health(changeType: ChangeType, health: Int) extends CreatureEffect

case class Attack(changeType: ChangeType, attack: Int) extends CreatureEffect

case class Taunt(taunt: Boolean) extends CreatureEffect

abstract class ChangeType

object ChangeType {
  def unapply(str: String): Option[ChangeType] = str match {
    case "Relative" => Some(Relative())
    case "Absolute" => Some(Absolute())
    case _ => None
  }
}

case class Relative() extends ChangeType

case class Absolute() extends ChangeType