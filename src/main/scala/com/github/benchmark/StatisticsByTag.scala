package com.github.benchmark

/**
 * Statistics for benchmarking
 *
 * @constructor Create a statistics.
 */
case class StatisticsByTag(tagValueMap: Map[Tag, Statistics] = Map()) extends StatisticsLike[StatisticsByTag] {
  def this(value: Long)(implicit tag: Tag) = this(Map(tag -> Statistics(Seq(value))))

  /** Add a value to this statistics */
  def add(value: Long)(implicit tag: Tag): StatisticsByTag = {
    val tuple = (tag, tagValueMap.getOrElse(tag, Statistics()) + value)
    StatisticsByTag(tagValueMap + tuple)
  }

  /** Add a value to this statistics */
  def + (value: Long)(implicit tag: Tag): StatisticsByTag = add(value)

  /** Add another statistics */
  def add(stat: StatisticsByTag): StatisticsByTag =
    StatisticsByTag(stat.tagValueMap.foldLeft(tagValueMap) { (_, _) match {
      case (tagValueMap, (tag, stat)) =>
        val tuple = (tag, tagValueMap.getOrElse(tag, Statistics()) + stat)
        tagValueMap + tuple
    } })

  /** Add another statistics */
  def + (stat: StatisticsByTag): StatisticsByTag = add(stat)

  def tags(): Set[Tag] = tagValueMap.keySet

  /** Caluculate the count of this statistics */  
  def count(): Int =
    (for(stat: Statistics <- tagValueMap.values)
       yield(stat.count)
    ).sum

  def countByTag()(implicit tag: Tag): Option[Int] =
    for(stat <- tagValueMap.get(tag))
      yield(stat.count)


  /** Caluculate the average value of this statistics */  
  def average(): Long =
    (for(stat: Statistics <- tagValueMap.values)
       yield(stat.values.sum)
    ).sum / count()

  def averageByTag()(implicit tag: Tag): Option[Long] =
    for(stat <- tagValueMap.get(tag))
      yield(stat.average)

  /** Caluculate the maximum value of this statistics */  
  def max(): Long =
    (for(stat: Statistics <- tagValueMap.values)
       yield(stat.max)
    ).max

  def maxByTag()(implicit tag: Tag): Option[Long] =
    for(stat <- tagValueMap.get(tag))
      yield(stat.max)

  /** Caluculate the minimum value of this statistics */  
  def min(): Long =
    (for(stat: Statistics <- tagValueMap.values)
       yield(stat.min)
    ).min

  def minByTag()(implicit tag: Tag): Option[Long] =
    for(stat <- tagValueMap.get(tag))
      yield(stat.min)

  override def toString() =
    "avg = %d, count = %d, max = %d, min = %d".format(average, count, max, min)
}

object StatisticsByTag {
  /** Create a statistics. */
  def stat() = Statistics()

  def apply(value: Long)(implicit tag: Tag): StatisticsByTag = {
    StatisticsByTag(Map(tag -> Statistics(Seq(value))))
  }
}