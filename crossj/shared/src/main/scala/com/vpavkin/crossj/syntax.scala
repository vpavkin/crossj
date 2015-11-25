package com.vpavkin.crossj

object syntax {
  implicit class WriterOps[T: Writer](o: T) {
    def write: JSON = implicitly[Writer[T]].write(o)
  }

  implicit class ReaderOps(o: JSON) {
    def read[T: Reader]: ReadResult[T] = implicitly[Reader[T]].read(o)
  }
}
