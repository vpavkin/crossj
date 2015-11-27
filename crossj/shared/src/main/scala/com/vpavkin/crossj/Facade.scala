package com.vpavkin.crossj

trait Facade {

  def renderer: Renderer
  def parser: Parser

  def write[T: Writer](o: T): JSON = implicitly[Writer[T]].write(o)
  def stringify[T: Writer](o: T): String = renderer.render(write(o))

  def read[T: Reader](o: String): ReadResult[T] = parser.parse(o).flatMap(read(_))
  def read[T: Reader](o: JSON): ReadResult[T] = implicitly[Reader[T]].read(o)

  def parse(o: String): ReadResult[JSON] = parser.parse(o)

}
