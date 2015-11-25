package com.vpavkin.crossj

import scala.collection.{Traversable, generic}

sealed trait ReadResult[+T] {
  def isSuccess: Boolean
  def map[A](f: T => A): ReadResult[A]
  def flatMap[A](f: T => ReadResult[A]): ReadResult[A]
}

case class ReadSuccess[+T](v: T) extends ReadResult[T] {
  def isSuccess: Boolean = true
  def map[A](f: T => A): ReadResult[A] = ReadSuccess(f(v))
  def flatMap[A](f: T => ReadResult[A]): ReadResult[A] = f(v)
}

case class ReadFailure(message: String) extends ReadResult[Nothing] {
  def isSuccess: Boolean = true
  def map[A](f: (Nothing) => A): ReadResult[A] = this
  def flatMap[A](f: (Nothing) => ReadResult[A]): ReadResult[A] = this
}

object ReadResult {
  def sequence[F[_], T](results: Traversable[ReadResult[T]])(implicit bf: generic.CanBuildFrom[F[_], T, F[T]], ra: Reader[T]): ReadResult[F[T]] =
    results.foldLeft(ReadSuccess(Vector.empty): ReadResult[Vector[T]]) {
      case (acc, elRes) => acc.flatMap(list => elRes match {
        case ReadSuccess(v) => ReadSuccess(list :+ v)
        case r@ReadFailure(message) => r
      })
    }.map(res => {
      val builder = bf()
      res.foreach(builder.+=)
      builder.result
    })
}
