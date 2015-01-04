package model

import game.Card
import scala.collection.mutable.MutableList
import scala.util.Random

object Util {
  val literalRegex = "\\(?([^)]+)\\)?".r

  def parseArray(str: String): Seq[String] = {
    val res = new MutableList[String]
    var bracketCount = 0
    val cur = new StringBuilder
    var trim = false
    for (c <- str) {
      c match {
        case '[' =>
          bracketCount += 1; cur += c
        case '(' =>
          bracketCount += 1; cur += c
        case ')' =>
          bracketCount -= 1; cur += c
        case ']' =>
          bracketCount -= 1; cur += c
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

  def playerInput(str: String, hand: Seq[Card], ownMinions: Seq[Card], enemyMinions: Seq[Card]): Option[Card] = {
    val all = hand ++ ownMinions ++ enemyMinions
    if (all.isEmpty) {
      None
    }

    if (!enemyMinions.isEmpty) {
      println("Enemy Board:\n" + enemyMinions.map(_.toString).mkString("\n"))
    }
    if (!ownMinions.isEmpty) {
      println("Own Board:\n" + ownMinions.map(_.toString).mkString("\n"))
    }
    if (!hand.isEmpty) {
      println("Hand:\n" + hand.map(_.toString).mkString("\n"))
    }

    print(str)
    val choice = io.StdIn.readInt

    val card = all.filter(_.id == choice)

    if (!card.exists(_.id == choice)) {
      println("Invalid Choice")
      None
    } else {
      Some(card(0))
    }
  }

  def randomInput(hand: Seq[Card], ownMinions: Seq[Card], enemyMinions: Seq[Card]): Option[Card] = {
    val all = hand ++ ownMinions ++ enemyMinions
    if (all.isEmpty) {
      None
    }

    val shuffled = Random.shuffle(all)
    Some(shuffled(0))
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
