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
case class BenchmarkMonad[A](value: A, stat: Statistics) {
  def flatMap[B](f: (A) => BenchmarkMonad[B]): BenchmarkMonad[B] = {
    val bench = f(value)
    BenchmarkMonad(bench.value, stat + bench.stat)
  }
  def map[B](f: (A) => B): BenchmarkMonad[B] = BenchmarkMonad(f(value), stat)
}

/**
 * A monad for benchmarking.
 */
object BenchmarkMonad {
  def run[T](f: => T): BenchmarkMonad[T] = {
    val time = System.currentTimeMillis
    val value = f
    BenchmarkMonad(value, Statistics(Seq(System.currentTimeMillis - time)))
  }

  def runFuture[T](f: => Future[T])(implicit ec: ExecutionContext): Future[BenchmarkMonad[T]] = {
    val time = System.currentTimeMillis
    for(value <- f) yield {
      BenchmarkMonad(value, Statistics(Seq(System.currentTimeMillis - time)))
    }
  }
}

/**
 * A monad transformer for BenchmarkMonad
 */
case class BenchmarkMonadT[F[_], A](run: F[BenchmarkMonad[A]]) {
  self =>

  import scalaz.{Functor, Monad}

  def map[B](f: A => B)(implicit F: Functor[F]): BenchmarkMonadT[F, B] = new BenchmarkMonadT[F, B](mapO(_ map f))

  def flatMap[B](f: A => BenchmarkMonadT[F, B])(implicit F: Monad[F]): BenchmarkMonadT[F, B] = new BenchmarkMonadT[F, B](
    F.bind(self.run){ v => f(v.value).run }
  )
  private def mapO[B](f: BenchmarkMonad[A] => B)(implicit F: Functor[F]) = F.map(run)(f)
}
