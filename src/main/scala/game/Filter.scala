package game

abstract class Filter

class AnyCreature extends Filter

class AnyHero extends Filter

class AnyFriendly extends Filter

class Type(minionType:String) extends Filter

class Self extends Filter

class Not(filters:Array[Filter]) extends Filter

class Any(filters:Array[Filter]) extends Filter