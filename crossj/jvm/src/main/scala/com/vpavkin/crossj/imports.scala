package com.vpavkin.crossj

object imports extends Auto with Serializer {

  implicit val coproductHandler = CoproductHandler.default

  def renderer: Renderer = Renderer
}
