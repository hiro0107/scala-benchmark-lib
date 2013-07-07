package com.github.benchmark

import org.junit.Test
import org.junit.Assert._
import org.hamcrest.CoreMatchers._

class BenchmarkTest {
  import Benchmark._
  def sleep[A](msTime: Long, value: A = ()): A = {
    Thread.sleep(msTime)
    value
  }

  @Test
  def runTest() {
    val time = run(sleep(100))
    assertThat(true, is(time >= 100L && time < 120L))
  }
  @Test
  def runTest2() {
    val stat = run(3)(sleep(100))
    assertThat(true, is(stat.average >= 100L && stat.average < 120L))
    assertThat(3, is(stat.count))
  }
  @Test
  def runFutureTest() {
    import scala.concurrent.duration.Duration
    import scala.concurrent.Await
    import scala.concurrent.Future
    import scala.concurrent.ExecutionContext.Implicits.global
    val timeFuture = runFuture(Future(sleep(100)))
    val time = Await.result(timeFuture, Duration.Inf)
    assertThat(true, is(time >= 100L && time < 120L))
  }
  @Test
  def runFutureTest2() {
    import scala.concurrent.duration.Duration
    import scala.concurrent.Await
    import scala.concurrent.Future
    import scala.concurrent.ExecutionContext.Implicits.global
    val statFuture = runFuture(3)(Future(sleep(100)))
    val stat = Await.result(statFuture, Duration.Inf)
    assertThat(true, is(stat.average >= 100L && stat.average < 120L))
    assertThat(3, is(stat.count))
  }
}

