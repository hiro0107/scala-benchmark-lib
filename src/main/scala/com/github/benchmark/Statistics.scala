package com.github.benchmark

case class Statistics(values: Seq[Long] = Seq()) {
  def add(value: Long): Statistics = Statistics(values :+ value)
  def + (value: Long): Statistics = Statistics(values :+ value)
  def add(stat: Statistics): Statistics = Statistics(values ++ stat.values)
  def + (stat: Statistics): Statistics = Statistics(values ++ stat.values)
  def count(): Int = values.size
  def average(): Long = values.sum / count()
  def max(): Long = values.max
  def min(): Long = values.min
  override def toString() =
    "avg = %d, count = %d, max = %d, min = %d".format(average, count, max, min)
}

object Statistics {
  def stat() = Statistics()
}