package com.vpavkin.crossj.scalajs

import com.vpavkin.crossj._
import scala.scalajs.js
import scala.util.{Success, Failure, Try}

class ScalajsParser extends Parser {

  private def toAST(native: Any): json.Any = native match {
    case v: String => json.String(v)
    case v: Double => json.Number(v)
    case v: Boolean => json.Boolean(v)
    case null => json.Null
    case v: js.Array[_] => json.Array(v.map(toAST).toList)
    case v: js.Object => json.Object(v.asInstanceOf[js.Dictionary[_]].mapValues(toAST).toMap)
  }

  def parse(s: String): ReadResult[JSON] = Try(js.JSON.parse(s)) match {
    case Failure(exception) => ReadFailure(exception.getMessage)
    case Success(value) => ReadSuccess(toAST(value))
  }
}
