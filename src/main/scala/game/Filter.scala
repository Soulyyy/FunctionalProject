package game

import model.Util

abstract class Filter

object Filter {
	val typeRegex = "\\(?Type \\S*?\\)?".r
	val notRegex = "\\(?Not \\[(.*)\\]\\)?".r
	val anyRegex = "\\(?Any \\[(.*)\\]\\)?".r
	
	val test = "Any [(Not [(AnyCreature), (AnyHero)]), (Self)]"
	
	def unapply(str:String) : Option[Filter] = str match {
		case Util.literalRegex("AnyCreature") => Some(AnyCreature())
		case Util.literalRegex("AnyHero") => Some(AnyHero())
		case Util.literalRegex("AnyFriendly") => Some(AnyFriendly())
		case typeRegex(minionType) => Some(Type(minionType))
		case Util.literalRegex("Self") => Some(Self())
		case notRegex(filter) => {
				val filters = filter.split(Util.arraySplit).map(Filter.unapply).filter(_.isDefined).map(_.get)
				Some(Not(filters))
		}
		case anyRegex(filter) => {
				val filters = filter.split(Util.arraySplit).map(Filter.unapply).filter(_.isDefined).map(_.get)
				Some(Any(filters))
		}
		case _ => None
	}
}

case class AnyCreature() extends Filter

case class AnyHero() extends Filter

case class AnyFriendly() extends Filter

case class Type(minionType:String) extends Filter

case class Self() extends Filter

case class Not(filters:Seq[Filter]) extends Filter

case class Any(filters:Seq[Filter]) extends Filter