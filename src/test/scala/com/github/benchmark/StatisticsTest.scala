package com.github.benchmark

import org.junit.Test
import org.junit.Assert._
import org.hamcrest.CoreMatchers._

class StatisticsTest {
  val stat = Statistics(Seq(32L, 44L, 323L, 32L, 629L)) + 344L + 3L

  @Test
  def countTest() {
    assertThat(stat.count, is(7))
  }

  @Test
  def averageTest() {
    val expected = (32 + 44 + 323 + 32 + 629 + 344 + 3) / 7
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
  def addTest() {
    val stat2 = Statistics(Seq(32L, 44L, 323L)) + Statistics(Seq(32L, 629L, 344L, 3L))

    assertThat(stat2.count, is(7))
    val expected = (32 + 44 + 323 + 32 + 629 + 344 + 3) / 7
    assertThat(stat2.average, is(expected.toLong))
    assertThat(stat2.max, is(629L))
    assertThat(stat2.min, is(3L))
  }
}