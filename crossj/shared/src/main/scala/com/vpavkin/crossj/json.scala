package com.vpavkin.crossj

object json {
  sealed trait Any
  case class String(value: java.lang.String) extends Any
  case class Object(elements: Map[java.lang.String, Any]) extends Any
  case class Array(elements: List[Any]) extends Any
  case class Number(value: Double) extends Any
  case class Boolean(value: scala.Boolean) extends Any
  case object Null extends Any
  case object Undefined extends Any
}
