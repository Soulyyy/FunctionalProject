package game

import model._

abstract class Effect

abstract class EventEffect

object EventEffect {
  val allRegex = "\\(?All \\[(.*?)\\] \\[(.*?)\\]\\)?".r
  val chooseRegex = "\\(?Choose \\[(.*?)\\] \\[(.*?)\\]\\)?".r
  val randomRegex = "\\(?Random \\[(.*?)\\] \\[(.*?)\\]\\)?".r
  val drawCardRegex = "\\(?DrawCard\\)?".r

  val test = "All [] [(Health Relative (-1)), (Attack Relative (-1))]"
  val testSplit = "\\s*\\d*\\(\\)"

  def unapplySeq(str: String): Option[Seq[EventEffect]] = {
    None
  }

  def unapply(str: String): Option[EventEffect] = str match {
    case allRegex(filter, creatureEffect) => {
      println("Almost")
      println("Filter: " + filter)
      println("CreatureEffect: " + creatureEffect)
      None
    }
    case _ => {
      println("Nope")
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
    case "Relative" => Some(Relative())
    case "Absolute" => Some(Absolute())
    case _ => None
  }
}

case class Relative() extends ChangeType

case class Absolute() extends ChangeType