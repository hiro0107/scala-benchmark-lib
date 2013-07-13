A benchmark library for Scala
=============================

* It includes basic functions for benchmarking,
* And a monad for the benchmarking.

Example:
```
import com.github.benchmark.BenchmarkMonad._

val result: BenchmarkMonad[(String, String)] =
  for(a: String <- run(logic());
      b: String <- run(logic2())) yield {
          (a, b)
  }
```

