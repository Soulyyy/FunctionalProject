package model

object Util {
  //val arraySplit = ",\\s+(?=\\([^\\[\\]]*(?:\\[.*\\])*\\))"
  val arraySplit = ",\\s+(?=\\(([^\\(\\)]*(\\(.*\\))*)+\\))"
  val literalRegex = "\\(?([^)]+)\\)?".r
}

object AsInt {
  def unapply(s: String) = try { Some(s.toInt) } catch {
    case e: NumberFormatException => None
  }
}

object AsBoolean {
  def unapply(s: String) = try { Some(s.toBoolean) } catch {
    case e: IllegalArgumentException => None
  }
}