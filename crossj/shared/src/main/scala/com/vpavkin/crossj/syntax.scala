package com.vpavkin.crossj

object syntax {
  implicit class WriterOps[T: Writer](o: T) {
    def write: JSON = implicitly[Writer[T]].write(o)
  }
}
