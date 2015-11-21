package com.vpavkin.crossj

@scala.annotation.implicitNotFound("Can not find coproduct handler in scope")
trait CoproductHandler {
  def apply(typeName: String, data: JSON): JSON
  def unapply(js: JSON): Option[(String, JSON)]
}

object CoproductHandler {

  val typeKey = "$type"

  def default: CoproductHandler = new CoproductHandler {

    def apply(typeName: String, obj: JSON): JSON =
      json.Object(obj.asInstanceOf[json.Object].elements + (typeKey -> json.String(typeName)))

    def unapply(js: JSON): Option[(String, JSON)] = {
      val m = json.asInstanceOf[json.Object].elements
      m.get(typeKey).flatMap(t =>
        try {
          Some(t.asInstanceOf[String])
        } catch {
          case e: Throwable => None
        }
      ).map(s => (s, json.Object(m - typeKey)))
    }
  }

  object implicits {
    implicit val default: CoproductHandler = CoproductHandler.default
  }
}
