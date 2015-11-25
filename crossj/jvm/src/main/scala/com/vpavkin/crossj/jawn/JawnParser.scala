package com.vpavkin.crossj.jawn

import com.vpavkin.crossj._

import scala.util.{Failure, Success}

class JawnParser extends Parser {

  implicit val jawnFacade = jawn.facade

  def parse(s: String): ReadResult[JSON] =
    _root_.jawn.Parser.parseFromString(s) match {
      case Failure(exception) => ReadFailure(exception.getMessage)
      case Success(value) => ReadSuccess(value)
    }

}
