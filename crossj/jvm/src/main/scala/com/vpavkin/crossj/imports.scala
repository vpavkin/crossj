package com.vpavkin.crossj

import com.vpavkin.crossj.jawn.JawnParser

object imports extends Auto with Facade {

  implicit val coproductHandler = CoproductHandler.default

  def parser: Parser = new JawnParser
  def renderer: Renderer = Renderer
}
