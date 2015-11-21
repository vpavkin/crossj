package com.vpavkin.crossj

trait Serializer {

  def renderer: Renderer

  def write[T: Writer](o: T): JSON = implicitly[Writer[T]].write(o)
  def stringify[T: Writer](o: T): String = renderer.render(write(o))
}
