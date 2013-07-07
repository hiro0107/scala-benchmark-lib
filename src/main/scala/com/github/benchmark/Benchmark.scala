package com.github.benchmark

import scala.concurrent.{Future, ExecutionContext}

object Benchmark {
  def run[T](f: => T): Long = {
    val time = System.currentTimeMillis
    f
    System.currentTimeMillis - time
  }

  def run[T](times: Int)(f: => T): Statistics = {
    (Statistics() /: (0 until times)) { (stat, _) =>
      val time = run(f)
      stat + time
    }
  }
  def runFuture[T](f: => Future[T])(implicit ec: ExecutionContext): Future[Long] = {
    val time = System.currentTimeMillis
    for(_ <- f) yield {
      System.currentTimeMillis - time
    }
  }

  def runFuture[T](times: Int)(f: => Future[T])(implicit ec: ExecutionContext): Future[Statistics] = {
    if(times <= 0) Future(Statistics())
    else
      for(value <- runFuture(f);
          stat <- runFuture(times - 1)(f)) yield {
         Statistics(Seq(value)) + stat
      }
  }
}