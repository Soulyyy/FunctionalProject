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
  
  def applyFilters(filters:Seq[Filter], source:Card, target:MinionCard, self:Player, other:Player, own:Boolean): Seq[Boolean] = {
    filters.map(_ match {
        case AnyCreature() => target.minionType != "Hero"
        case AnyHero() => target.minionType == "Hero"
        case AnyFriendly() => own
        case Type(t) => target.minionType == t
        case Self() => source.id == target.id
        case Not(fs) => !(applyFilters(fs, source, target, self, other, own).find(!_) == None)
        case Any(fs) => applyFilters(fs, source, target, self, other, own).exists(_ == true)
      })
  }
  
  def filter(filters:Seq[Filter], source:Card, self:Player, other:Player): (Seq[MinionCard], Seq[MinionCard]) = {
    (self.board.values.toSeq.filter(minion => {
      applyFilters(filters, source, minion, self, other, true).find(!_) == None
    }),
    other.board.values.toSeq.filter(minion => {
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