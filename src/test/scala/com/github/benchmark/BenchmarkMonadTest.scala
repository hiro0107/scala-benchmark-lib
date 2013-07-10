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
  @Test
  def runBenchmarkMonadTTest() {
    import scala.concurrent.duration.Duration
    import scala.concurrent.Future
    import scala.concurrent.Await
    import scala.concurrent.ExecutionContext.Implicits.global
    import scalaz.{Functor, Monad}

    implicit val functor = new Functor[Future] {
      def map[A, B](fa: Future[A])(f: A => B): Future[B] = fa.map(f)
    }
    implicit val monad = new Monad[Future] {
      def point[A](a: => A): Future[A] = Future(a)

      def bind[A, B](fa: Future[A])(f: A => Future[B]): Future[B] = fa.flatMap(f)
    }

    val resultFuture =
      for(a <- BenchmarkMonadT(runFuture(Future(sleep(100, 'a'))));
          b <- BenchmarkMonadT(runFuture(Future(sleep(200, "b")))))
        yield (a, b)
    val result = Await.result(resultFuture.run, Duration.Inf)
    assertThat( ('a', "b"), is(result.value))
    assertThat(true, is(result.stat.average >= 150L && result.stat.average < 18000L))
  }
}
