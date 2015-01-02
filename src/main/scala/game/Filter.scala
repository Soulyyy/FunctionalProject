package game

import model.Util

sealed trait Filter

object Filter {
	val typeRegex = "\\(?Type \\S*?\\)?".r
	val notRegex = "\\(?Not \\[(.*)\\]\\)?".r
	val anyRegex = "\\(?Any \\[(.*)\\]\\)?".r
	
	val test = "[Any [(Not [(AnyCreature), (AnyHero)]), (Self)]]"
	
	def unapply(str:String) : Option[Filter] = str match {
		case Util.literalRegex("AnyCreature") => Some(AnyCreature())
		case Util.literalRegex("AnyHero") => Some(AnyHero())
		case Util.literalRegex("AnyFriendly") => Some(AnyFriendly())
		case typeRegex(minionType) => Some(Type(minionType))
		case Util.literalRegex("Self") => Some(Self())
		case notRegex(filter) => {
				val filters = Util.parseArray(filter).map(Filter.unapply).filter(_.isDefined).map(_.get)
				Some(Not(filters))
		}
		case anyRegex(filter) => {
				val filters = Util.parseArray(filter).map(Filter.unapply).filter(_.isDefined).map(_.get)
				Some(Any(filters))
		}
		case _ => None
	}
  
  def applyFilters(filters:Seq[Filter], source:Card, target:Card, self:Player, other:Player, own:Boolean): Seq[Boolean] = {
    filters.map(_ match {
        case AnyCreature() => target.cardType.asInstanceOf[MinionCard].minionType != "Hero"
        case AnyHero() => target.cardType.asInstanceOf[MinionCard].minionType == "Hero"
        case AnyFriendly() => own
        case Type(t) => target.cardType.asInstanceOf[MinionCard].minionType == t
        case Self() => source == target //Does this work correctly?
        case Not(fs) => !(applyFilters(fs, source, target, self, other, own).find(!_) == None)
        case Any(fs) => applyFilters(fs, source, target, self, other, own).exists(_ == true)
      })
  }
  
  def filter(filters:Seq[Filter], source:Card, self:Player, other:Player): (Seq[Card], Seq[Card]) = {
    (self.board.filter(minion => {
      applyFilters(filters, source, minion, self, other, true).find(!_) == None
    }),
    other.board.filter(minion => {
      applyFilters(filters, source, minion, self, other, false).find(!_) == None
    }))
  }
}

case class AnyCreature() extends Filter

case class AnyHero() extends Filter

case class AnyFriendly() extends Filter

case class Type(minionType:String) extends Filter

case class Self() extends Filter

case class Not(filters:Seq[Filter]) extends Filter

case class Any(filters:Seq[Filter]) extends Filter