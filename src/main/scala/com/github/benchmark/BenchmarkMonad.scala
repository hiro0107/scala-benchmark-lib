package com.github.benchmark

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

/**
 * A monad for benchmarking.
 *
 * {{{
 *  import com.github.benchmark.BenchmarkMonad._
 *  val result: BenchmarkMonad[(String, String)] =
 *    for(a: String <- run(logic());
 *        b: String <- run(logic2())) yield {
 *       (a, b)
 *    }
 *  
 * }}}
 */
case class BenchmarkMonad[A, STAT <: StatisticsLike[STAT]](value: A, stat: STAT) {
  def flatMap[B](f: (A) => BenchmarkMonad[B, STAT]): BenchmarkMonad[B, STAT] = {
    val bench = f(value)
    val stat2: STAT = bench.stat
    BenchmarkMonad(bench.value, stat.add(stat2))
  }
  def map[B](f: (A) => B): BenchmarkMonad[B, STAT] = BenchmarkMonad(f(value), stat)
}

/**
 * A monad for benchmarking.
 */
object BenchmarkMonad {
  def run[T](f: => T)(implicit tag: Tag): BenchmarkMonad[T, StatisticsByTag] = {
    val time = System.currentTimeMillis
    val value = f
    BenchmarkMonad(value, StatisticsByTag(Map(tag -> Statistics(Seq(System.currentTimeMillis - time)))))
  }

  def runFuture[T](f: => Future[T])(implicit tag: Tag, ec: ExecutionContext): Future[BenchmarkMonad[T, StatisticsByTag]] = {
    val time = System.currentTimeMillis
    for(value <- f) yield {
      BenchmarkMonad(value, StatisticsByTag(Map(tag -> Statistics(Seq(System.currentTimeMillis - time)))))
    }
  }
}

/**
 * A monad transformer for BenchmarkMonad
 */
case class BenchmarkMonadT[F[_], A](run: F[BenchmarkMonad[A, StatisticsByTag]]) {
  self =>

  import scalaz.{Functor, Monad}

  def map[B](f: A => B)(implicit F: Functor[F]): BenchmarkMonadT[F, B] = new BenchmarkMonadT[F, B](mapO(_ map f))

  def flatMap[B](f: A => BenchmarkMonadT[F, B])(implicit F: Monad[F]): BenchmarkMonadT[F, B] = new BenchmarkMonadT[F, B](
    F.bind(self.run){ v => f(v.value).run }
  )
  private def mapO[B](f: BenchmarkMonad[A, StatisticsByTag] => B)(implicit F: Functor[F]) = F.map(run)(f)
}
