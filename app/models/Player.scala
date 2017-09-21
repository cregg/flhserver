package models

import models.StatType.StatType


/**
  * Created by cleclair on 2017-03-05.
  */
case class Player(id: String, name: String, rank: Int = -1, draftPos: Int = -1, stats: Seq[Stat[_]] = Seq[Stat[_]]()) {

  override def toString: String = s"Name: $name\nCurrent Rank: $rank \nDraft Position: $draftPos\nScore: ${draftPos - rank}"

}

object StatType extends Enumeration {
  type StatType = Value
  val StringType,
  IntType,
  DoubleType = Value
}

trait Stat[+TypeOfStat] {
  def name: String
  def statType: StatType
  def value: Option[TypeOfStat]
}

case class IntStat(override val name: String, override val value: Option[Int]) extends Stat[Int] {
  override val statType = StatType.IntType
}

case class DoubleStat(override val name: String, override val value: Option[Double]) extends Stat[Double] {
  override val statType = StatType.DoubleType
}
