package model

import scala.collection.mutable.MutableList

object Util {
  //val arraySplit = ",\\s+(?=\\([^\\[\\]]*(?:\\[.*\\])*\\))"
  val arraySplit = ",\\s+(?=\\(([^\\(\\)]*(\\(.*\\))*)+\\))"
  val literalRegex = "\\(?([^)]+)\\)?".r

  def parseArray(str: String): Seq[String] = {
    val res = new MutableList[String]
    var bracketCount = 0
    val cur = new StringBuilder
    var trim = false
    for (c <- str) {
      c match {
        case '[' => bracketCount += 1; cur += c
        case '(' => bracketCount += 1; cur += c
        case ')' => bracketCount -= 1; cur += c
        case ']' => bracketCount -= 1; cur += c
        case ',' => {
          if (bracketCount == 0) {
            res += cur.mkString
            cur.clear
            trim = true
          } else {
            cur += c
          }
        }
        case ' ' | '\n' | '\t' | '\r' => {
          if (!trim) {
            cur += c
          }
        }
        case _ => trim = false; cur += c
      }
    }
    res += cur.mkString
    res.toList
  }

  def choose(hand : Seq) : Unit = {
    var i=0
    for(i <- 0 to hand.length) {
      println(i+ " "+ hand(i))
    }
  }
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