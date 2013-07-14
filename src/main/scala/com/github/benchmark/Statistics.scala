package com.github.benchmark

trait StatisticsLike[STAT <: StatisticsLike[STAT]] {
  /** Add another statistics */
  def add(stat: STAT): STAT

  /** Add another statistics */
  def + (stat: STAT): STAT

  /** Caluculate the count of this statistics */  
  def count(): Int

  /** Caluculate the average value of this statistics */  
  def average(): Long

  /** Caluculate the maximum value of this statistics */  
  def max(): Long

  /** Caluculate the minimum value of this statistics */  
  def min(): Long
}

/**
 * Statistics for benchmarking
 *
 * @constructor Create a statistics.
 */
case class Statistics(values: Seq[Long] = Seq()) extends StatisticsLike[Statistics] {
  def this(value: Long) = this(Seq(value))

  /** Add a value to this statistics */
  def add(value: Long): Statistics = Statistics(values :+ value)

  /** Add a value to this statistics */
  def + (value: Long): Statistics = Statistics(values :+ value)

  /** Add another statistics */
  def add(stat: Statistics): Statistics = Statistics(values ++ stat.values)

  /** Add another statistics */
  def + (stat: Statistics): Statistics = Statistics(values ++ stat.values)

  /** Caluculate the count of this statistics */  
  def count(): Int = values.size

  /** Caluculate the average value of this statistics */  
  def average(): Long = values.sum / count()

  /** Caluculate the maximum value of this statistics */  
  def max(): Long = values.max

  /** Caluculate the minimum value of this statistics */  
  def min(): Long = values.min

  override def toString() =
    "avg = %d, count = %d, max = %d, min = %d".format(average, count, max, min)
}

object Statistics {
  /** Create a statistics. */
  def stat() = Statistics()
}