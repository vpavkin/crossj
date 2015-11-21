package com.vpavkin.crossj

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.ContentTypes
import com.vpavkin.crossj.imports._

object AkkaHttpSupport extends AkkaHttpSupport

trait AkkaHttpSupport {

  implicit def upickleMarshallerConverter[A](writer: Writer[A]): ToEntityMarshaller[A] =
    upickleMarshaller[A](writer)

  implicit def upickleMarshaller[A](implicit writer: Writer[A]): ToEntityMarshaller[A] =
    upickleJsValueMarshaller.compose(write[A])

  implicit def upickleJsValueMarshaller: ToEntityMarshaller[json.Any] =
    Marshaller.StringMarshaller.wrap(ContentTypes.`application/json`)(renderer.render)
}
