package com.github.benchmark

import scala.concurrent.{Future, ExecutionContext}

object Benchmark {
  /** Obtain the benchmark */
  def run[T](f: => T)(implicit tag: Tag): StatisticsByTag = {
    val time = System.currentTimeMillis
    f
    StatisticsByTag(System.currentTimeMillis - time)
  }

  /** Obtain the benchmark of multiple runs */
  def run[T](times: Int)(f: => T)(implicit tag: Tag): StatisticsByTag = {
    (StatisticsByTag() /: (0 until times)) { (stat, _) =>
      val time = run(f)
      stat + time
    }
  }

  /** Obtain the benchmark in the future */
  def runFuture[T](f: => Future[T])(implicit tag: Tag, ec: ExecutionContext): Future[StatisticsByTag] = {
    val time = System.currentTimeMillis
    for(_ <- f) yield {
      StatisticsByTag(System.currentTimeMillis - time)
    }
  }

  /** Obtain the benchmark of multiple runs in the future */
  def runFuture[T](times: Int)(f: => Future[T])(implicit tag: Tag, ec: ExecutionContext): Future[StatisticsByTag] = {
    if(times <= 0) Future(StatisticsByTag())
    else
      for(stat1 <- runFuture(f);
          stat2 <- runFuture(times - 1)(f)) yield {
         stat1 + stat2
      }
  }
}