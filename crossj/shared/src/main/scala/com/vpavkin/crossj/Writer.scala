package com.vpavkin.crossj

import java.util.UUID
import syntax._

trait Writer[T] {
  def write(x: T): JSON
}

trait Writers {

  implicit object stringJSON extends Writer[String] {
    def write(x: String): JSON = json.String(x)
  }

  implicit object intJSON extends Writer[Int] {
    def write(x: Int): JSON = json.Number(x.toDouble)
  }

  implicit object longJSON extends Writer[Long] {
    def write(x: Long): JSON = json.String(x.toString)
  }

  implicit object booleanJSON extends Writer[Boolean] {
    def write(x: Boolean): JSON = json.Boolean(x)
  }

  implicit object doubleJSON extends Writer[Double] {
    def write(x: Double): JSON = json.Number(x)
  }

  implicit object uuidJSON extends Writer[UUID] {
    def write(x: UUID): JSON = json.String(x.toString)
  }

  implicit def optionJSON[T: Writer]: Writer[Option[T]] = new Writer[Option[T]] {
    override def write(x: Option[T]): JSON = x match {
      case Some(a) => a.write
      case None => json.Undefined
    }
  }

  implicit def listJSON[T: Writer]: Writer[List[T]] = new Writer[List[T]] {
    def write(x: List[T]): JSON = json.Array(x.map(_.write))
  }

  implicit def mapJSON[V: Writer]: Writer[Map[String, V]] = new Writer[Map[String, V]] {
    def write(x: Map[String, V]): JSON =
      json.Object(x.mapValues(_.write))
  }
}

object Writer extends Writers {
  def apply[T](f: T => JSON) = new Writer[T] {
    def write(x: T): JSON = f(x)
  }
}
