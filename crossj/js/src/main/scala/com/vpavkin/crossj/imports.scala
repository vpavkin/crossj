package com.vpavkin.crossj

import com.vpavkin.crossj.scalajs.ScalajsParser

object imports extends Auto with Facade {

  implicit val coproductHandler = CoproductHandler.default

  def parser: Parser = new ScalajsParser
  def renderer: Renderer = Renderer
}
