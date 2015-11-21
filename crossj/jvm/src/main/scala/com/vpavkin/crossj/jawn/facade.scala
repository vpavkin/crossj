package com.vpavkin.crossj.jawn

import com.vpavkin.crossj.{JSON, json}
import jawn.SimpleFacade

object facade extends SimpleFacade[JSON] {

  def jarray(vs: List[JSON]): JSON = json.Array(vs)
  def jobject(vs: Map[String, JSON]): JSON = json.Object(vs)
  def jint(s: String): JSON = json.Number(s.toDouble)
  def jfalse(): JSON = json.Boolean(false)
  def jnum(s: String): JSON = json.Number(s.toDouble)
  def jnull(): JSON = json.Null
  def jtrue(): JSON = json.Boolean(true)
  def jstring(s: String): JSON = json.String(s)
}
