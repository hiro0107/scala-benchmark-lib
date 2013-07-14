package com.github.benchmark

import org.junit.Test
import org.junit.Assert._
import org.hamcrest.CoreMatchers._

class StatisticsByTagTest {
  val stat =
     StatisticsByTag(Map[Tag, Statistics](EmptyTag -> Statistics(Seq(32L, 44L, 323L)))) +
     StatisticsByTag(Map[Tag, Statistics](LabeledTag("a") -> Statistics(Seq(32L, 629L, 344L, 3L)))) +
     StatisticsByTag(Map[Tag, Statistics](LabeledTag(1) -> Statistics(Seq(217L, 38L))))

  @Test
  def countTest() {
    assertThat(stat.count, is(9))
  }

  @Test
  def averageTest() {
    val expected = (32 + 44 + 323 + 32 + 629 + 344 + 3 + 217 + 38) / 9
    assertThat(stat.average, is(expected.toLong))
  }

  @Test
  def maxTest() {
    assertThat(stat.max, is(629L))
  }

  @Test
  def minTest() {
    assertThat(stat.min, is(3L))
  }

  @Test
  def tagsTest() {
    assertThat(stat.tags, is(Set[Tag](EmptyTag, LabeledTag("a"), LabeledTag(1))))
  }

  @Test
  def countByTag() {
    assertThat(
      Seq[Option[Int]](Some(3), Some(4), Some(2), None),
      is(Seq(EmptyTag, LabeledTag("a"), LabeledTag(1), LabeledTag('b')).map(stat.countByTag()(_))))
  }

  @Test
  def averageByTag() {
    assertThat(
      Seq[Option[Long]](
        Some((32L + 44L + 323L) / 3),
        Some((32L + 629L + 344L + 3L) / 4),
        Some((217L + 38L) / 2),
        None),
      is(Seq(EmptyTag, LabeledTag("a"), LabeledTag(1), LabeledTag('b')).map(stat.averageByTag()(_))))
  }
  @Test
  def maxByTag() {
    assertThat(
      Seq[Option[Long]](Some(323L), Some(629L), Some(217L), None),
      is(Seq(EmptyTag, LabeledTag("a"), LabeledTag(1), LabeledTag('b')).map(stat.maxByTag()(_))))
  }
  @Test
  def minByTag() {
    assertThat(
      Seq[Option[Long]](Some(32L), Some(3L), Some(38L), None),
      is(Seq(EmptyTag, LabeledTag("a"), LabeledTag(1), LabeledTag('b')).map(stat.minByTag()(_))))
  }
}