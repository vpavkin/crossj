package com.vpavkin.crossj

import shapeless.Lazy

trait Auto extends GenericWriter {
  def format[A](implicit w: Lazy[Writer[A]]): Writer[A] = w.value
}
