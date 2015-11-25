package com.vpavkin.crossj

import shapeless._
import shapeless.labelled._

trait GenericReader extends Readers with GenericReader1 with GenericReader2 {

  implicit val readsHNil: Reader[HNil] = new Reader[HNil] {
    def read(x: JSON): ReadResult[HNil] = ReadSuccess(HNil)
  }

  implicit def readsCNil: Reader[CNil] = new Reader[CNil] {
    def read(x: JSON): ReadResult[CNil] = ReadFailure("Unknown type: " + x.asInstanceOf[json.Object].elements.get(CoproductHandler.typeKey))
  }

  implicit def readsCCons[K <: Symbol, V, T <: Coproduct]
  (implicit
   K: Witness.Aux[K],
   V: Lazy[Reader[V]],
   T: Lazy[Reader[T]],
   cpc: CoproductHandler
  ): Reader[FieldType[K, V] :+: T] = new Reader[FieldType[K, V] :+: T] {
    def read(x: JSON): ReadResult[FieldType[K, V] :+: T] = x match {
      case cpc(K.value.name, data) =>
        V.value.read(data).map(v => Inl(field[K](v)))
      case other =>
        T.value.read(other).map(Inr(_))
    }
  }

  implicit def readsHConsOption[K <: Symbol, V, T <: HList]
  (implicit
   K: Witness.Aux[K],
   V: Lazy[Reader[V]],
   T: Lazy[Reader[T]]
  ): Reader[FieldType[K, Option[V]] :: T] = new Reader[FieldType[K, Option[V]] :: T] {
    def read(x: JSON): ReadResult[FieldType[K, Option[V]] :: T] = x match {
      case json.Object(kv) =>
        val head = kv.get(K.value.name).map(v => V.value.read(v).map(Some(_)))
          .getOrElse(ReadSuccess(None))
        val tail = T.value.read(json.Object(kv - K.value.name))
        head.flatMap(h => tail.map(t => field[K](h) :: t))
      case other => ReadFailure(s"Expected object, got $other")
    }

  }
}

trait GenericReader1 {
  implicit def readsGeneric[A, R](implicit gen: LabelledGeneric.Aux[A, R], repr: Lazy[Reader[R]]): Reader[A] = new Reader[A] {
    def read(x: JSON): ReadResult[A] = repr.value.read(x).map(gen.from)
  }
}

trait GenericReader2 {

  implicit def readsHCons[K <: Symbol, V, T <: HList]
  (implicit
   K: Witness.Aux[K],
   V: Lazy[Reader[V]],
   T: Lazy[Reader[T]]
  ): Reader[FieldType[K, V] :: T] = new Reader[FieldType[K, V] :: T] {
    def read(x: JSON): ReadResult[FieldType[K, V] :: T] = x match {
      case json.Object(kv) =>
        val head = kv.get(K.value.name).map(V.value.read)
          .getOrElse(ReadFailure(s"Field ${K.value.name} is missing in $x"))
        val tail = T.value.read(json.Object(kv - K.value.name))
        head.flatMap(h => tail.map(t => field[K](h) :: t))
      case other => ReadFailure(s"Expected object, got $other")
    }
  }

}
