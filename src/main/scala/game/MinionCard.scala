package game

class MinionCard(effect: Array[Effect], health: Int, attack: Int, taunt: Boolean, minionType: String) extends CardType {
  var this.health = health
  var this.attack = attack
  var dynHealth: Int = 0
  var dynAttack: Int = 0
  var this.taunt = taunt
  val this.minionType = minionType

}
