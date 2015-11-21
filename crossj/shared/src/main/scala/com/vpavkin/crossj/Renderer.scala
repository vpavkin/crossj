package com.vpavkin.crossj

import scala.annotation.switch

trait Renderer {
  def render(jv: JSON): String = {
    val sb = new StringBuilder
    render(sb, 0, jv)
    sb.toString
  }

  def render(sb: StringBuilder, depth: Int, jv: JSON): Unit = {
    jv match {
      case json.Undefined => sb.append("null")
      case json.Null => sb.append("null")
      case json.Boolean(true) => sb.append("true")
      case json.Boolean(false) => sb.append("false")
      case json.Number(n) => sb.append(if (n == n.toInt) n.toInt.toString else n.toString)
      case json.String(s) => renderString(sb, s)
      case json.Array(vs) => renderArray(sb, depth, vs.toArray)
      case json.Object(vs) => renderObject(sb, depth, canonicalizeObject(vs))
    }
  }

  def canonicalizeObject(vs: Map[String, JSON]): Iterator[(String, JSON)] =
    vs.iterator

  def renderString(sb: StringBuilder, s: String): Unit =
    escape(sb, s, unicode = false)

  def renderArray(sb: StringBuilder, depth: Int, vs: Array[JSON]): Unit = {
    if (vs.isEmpty) sb.append("[]")
    else {
      sb.append("[")
      render(sb, depth + 1, vs(0))
      var i = 1
      while (i < vs.length) {
        sb.append(",")
        render(sb, depth + 1, vs(i))
        i += 1
      }
      sb.append("]")
    }
  }

  def renderObject(sb: StringBuilder, depth: Int, it: Iterator[(String, JSON)]): Unit = {
    if (!it.hasNext)
      sb.append("{}")
    else {
      val (k0, v0) = it.next
      sb.append("{")
      renderString(sb, k0)
      sb.append(":")
      render(sb, depth + 1, v0)
      while (it.hasNext) {
        val (k, v) = it.next
        sb.append(",")
        renderString(sb, k)
        sb.append(":")
        render(sb, depth + 1, v)
      }
      sb.append("}")
    }
  }

  def escape(sb: StringBuilder, s: String, unicode: Boolean): Unit = {
    sb.append('"')
    var i = 0
    val len = s.length
    while (i < len) {
      (s.charAt(i): @switch) match {
        case '"' => sb.append("\\\"")
        case '\\' => sb.append("\\\\")
        case '\b' => sb.append("\\b")
        case '\f' => sb.append("\\f")
        case '\n' => sb.append("\\n")
        case '\r' => sb.append("\\r")
        case '\t' => sb.append("\\t")
        case c =>
          if (c < ' ' || (c > '~' && unicode)) sb.append("\\u%04x" format c.toInt)
          else sb.append(c)
      }
      i += 1
    }
    sb.append('"')
  }
}

object Renderer extends Renderer
