package com.vpavkin.crossj

import shapeless._
import labelled._

trait GenericWriter extends Writers with GenericWriter1 {

  implicit val writeHNil: Writer[HNil] = new Writer[HNil] {
    def write(x: HNil): JSON = json.Undefined
  }

  implicit def writeHConsOption[K <: Symbol, V, T <: HList]
  (implicit
   K: Witness.Aux[K],
   V: Lazy[Writer[V]],
   T: Lazy[Writer[T]]
  ): Writer[FieldType[K, V] :: T] = new Writer[FieldType[K, V] :: T] {

    def write(x: FieldType[K, V] :: T): JSON = {
      val head: Map[String, JSON] = V.value.write(x.head) match {
        case json.Undefined | json.Null => Map.empty
        case any => Map(K.value.name -> any)
      }
      val tail: Map[String, JSON] = T.value.write(x.tail) match {
        case json.Object(map) => map
        case _ => Map.empty
      }
      json.Object(head ++ tail)
    }
  }

  implicit def writeCNil: Writer[CNil] = new Writer[CNil] {
    def write(x: CNil): JSON = json.Object(Map.empty)
  }

  implicit def writeCCons[K <: Symbol, V, T <: Coproduct]
  (implicit
   K: Witness.Aux[K],
   V: Lazy[Writer[V]],
   T: Lazy[Writer[T]],
   cpc: CoproductHandler
  ): Writer[FieldType[K, V] :+: T] = new Writer[FieldType[K, V] :+: T] {
    def write(x: FieldType[K, V] :+: T): JSON =
      x match {
        case Inl(l) => cpc(K.value.name, V.value.write(l))
        case Inr(r) => T.value.write(r)
      }
  }

}

trait GenericWriter1 {
  implicit def writesGeneric[A, R](implicit gen: LabelledGeneric.Aux[A, R], wRepr: Lazy[Writer[R]]): Writer[A] =
    new Writer[A] {
      override def write(o: A): JSON = wRepr.value.write(gen.to(o))
    }
}
