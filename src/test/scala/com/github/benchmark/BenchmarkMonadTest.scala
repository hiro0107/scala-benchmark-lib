package com.github.benchmark

import org.junit.Test
import org.junit.Assert._
import org.hamcrest.CoreMatchers._

class BenchmarkMonadTest {
  import BenchmarkMonad._
  def sleep[A](msTime: Long, value: A): A = {
    Thread.sleep(msTime)
    value
  }

  @Test
  def runTest() {
    val result =
      for(a <- run(sleep(100, 'a'));
          b <- run(sleep(200, "b"))) yield {
        (a, b)
      }
    assertThat( ('a', "b"), is(result.value))
    assertThat(true, is(result.stat.average >= 150L && result.stat.average < 180L))
  }
  @Test
  def runFutureTest() {
    import scala.concurrent.duration.Duration
    import scala.concurrent.Future
    import scala.concurrent.Await
    import scala.concurrent.ExecutionContext.Implicits.global
    val resultFuture =
      for(bmA <- runFuture(Future(sleep(100, 'a')));
          bmB <- runFuture(Future(sleep(200, "b")))) yield {
        for(a <- bmA;
            b <- bmB) yield (a, b)
      }
    val result = Await.result(resultFuture, Duration.Inf)
    assertThat( ('a', "b"), is(result.value))
    assertThat(true, is(result.stat.average >= 150L && result.stat.average < 18000L))
  }
}
