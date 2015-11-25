package com.vpavkin.crossj

import java.util.UUID

import com.vpavkin.crossj.syntax._

import scala.collection.generic
import scala.util.Try

trait Reader[T] {
  def read(x: JSON): ReadResult[T]
}

trait Readers {

  private def failure(expected: String, got: json.Any) = ReadFailure(s"Expected string, got $got")

  implicit object stringReader extends Reader[String] {
    def read(x: JSON): ReadResult[String] = x match {
      case json.String(value) => ReadSuccess(value)
      case other => failure("string", other)
    }
  }

  implicit object intReader extends Reader[Int] {
    def read(x: JSON): ReadResult[Int] = x match {
      case json.Number(value) if value.isValidInt => ReadSuccess(value.toInt)
      case other => failure("int", other)
    }
  }

  implicit object booleanReader extends Reader[Boolean] {
    def read(x: JSON): ReadResult[Boolean] = x match {
      case json.Boolean(value) => ReadSuccess(value)
      case other => failure("boolean", other)
    }
  }

  implicit object doubleReader extends Reader[Double] {
    def read(x: JSON): ReadResult[Double] = x match {
      case json.Number(value) => ReadSuccess(value)
      case other => failure("double", other)
    }
  }

  implicit object longReader extends Reader[Long] {
    def read(x: JSON): ReadResult[Long] = x match {
      case s@json.String(value) => Try(ReadSuccess(value.toLong)).getOrElse(failure("string repr of long", s))
      case other => failure("string", other)
    }
  }

  implicit object uuidReader extends Reader[UUID] {
    def read(x: JSON): ReadResult[UUID] = x match {
      case s@json.String(value) =>
        Try(ReadSuccess(UUID.fromString(value)))
          .getOrElse(failure("uuid", s))
      case other => failure("uuid", other)
    }
  }

  implicit def optionReader[T: Reader]: Reader[Option[T]] = new Reader[Option[T]] {
    def read(x: JSON): ReadResult[Option[T]] = x match {
      case json.Null | json.Undefined => ReadSuccess(None)
      case other => x.read[T].map(Some(_))
    }
  }

  implicit def traversableReader[F[_], T](implicit bf: generic.CanBuildFrom[F[_], T, F[T]], ra: Reader[T]) = new Reader[F[T]] {
    def read(x: JSON): ReadResult[F[T]] = x match {
      case json.Array(elements) =>
        ReadResult.sequence[F, T](elements.map(_.read[T]))
      case other => failure("array", other)
    }
  }

  implicit def mapReader[V: Reader]: Reader[Map[String, V]] = new Reader[Map[String, V]] {
    def read(x: JSON): ReadResult[Map[String, V]] = x match {
      case a: json.Object =>
        ReadResult.sequence[Iterable, V](a.elements.values.map(_.read[V]))
          .map(a.elements.keys.zip(_).toMap)
      case other => failure("object", other)
    }
  }
}

object Reader extends Readers {
  def apply[T](f: JSON => ReadResult[T]) = new Reader[T] {
    def read(x: JSON) = f(x)
  }
}
