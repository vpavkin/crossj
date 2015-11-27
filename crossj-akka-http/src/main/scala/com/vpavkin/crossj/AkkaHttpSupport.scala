package com.vpavkin.crossj

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.{MediaTypes, HttpCharsets, ContentTypes}
import akka.http.scaladsl.unmarshalling.{Unmarshaller, FromEntityUnmarshaller}
import com.vpavkin.crossj.imports._

object AkkaHttpSupport extends AkkaHttpSupport

trait AkkaHttpSupport {

  implicit def crossjUnmarshallerConverter[A](reader: Reader[A]): FromEntityUnmarshaller[A] =
    crossjUnmarshaller(reader)

  implicit def crossjUnmarshaller[A](implicit reader: Reader[A]): FromEntityUnmarshaller[A] =
    crossjJsValueUnmarshaller.map(read[A]).map {
      case ReadSuccess(v) => v
      case ReadFailure(message) => throw new IllegalArgumentException(s"Invalid JSON entity: $message")
    }

  implicit def crossjJsValueUnmarshaller: FromEntityUnmarshaller[json.Any] =
    Unmarshaller.byteStringUnmarshaller
      .forContentTypes(MediaTypes.`application/json`)
      .mapWithCharset { (data, charset) =>
        val input = if (charset == HttpCharsets.`UTF-8`) data.utf8String else data.decodeString(charset.nioCharset.name)
        parse(input) match {
          case ReadSuccess(v) => v
          case ReadFailure(message) => throw new IllegalArgumentException(s"Invalid JSON: $message")
        }
      }


  implicit def crossjMarshallerConverter[A](writer: Writer[A]): ToEntityMarshaller[A] =
    crossjMarshaller[A](writer)

  implicit def crossjMarshaller[A](implicit writer: Writer[A]): ToEntityMarshaller[A] =
    crossjJsValueMarshaller.compose(write[A])

  implicit def crossjJsValueMarshaller: ToEntityMarshaller[json.Any] =
    Marshaller.StringMarshaller.wrap(ContentTypes.`application/json`)(renderer.render)
}
