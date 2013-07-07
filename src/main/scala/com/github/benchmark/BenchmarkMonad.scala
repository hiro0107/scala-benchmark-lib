package com.github.benchmark

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

case class BenchmarkMonad[A](value: A, stat: Statistics) {
  def flatMap[B](f: (A) => BenchmarkMonad[B]): BenchmarkMonad[B] = {
    val bench = f(value)
    BenchmarkMonad(bench.value, stat + bench.stat)
  }
  def map[B](f: (A) => B): BenchmarkMonad[B] = BenchmarkMonad(f(value), stat)
}
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
