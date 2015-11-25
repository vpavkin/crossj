package com.vpavkin.crossj

import shapeless.Lazy

trait Auto extends GenericWriter with GenericReader {
  def writer[A](implicit w: Lazy[Writer[A]]): Writer[A] = w.value
  def reader[A](implicit r: Lazy[Reader[A]]): Reader[A] = r.value
}
