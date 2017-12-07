import java.util.UUID

import Repo._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.{Directives, Route}
import model.Model

object Routes extends Directives with Model {

  // todo add ip security,
  // todo add basic auth,
  // todo add error answers
  // todo add tests

  def routes(): Route =

    get {
      path("anonym" / "getMsisdn") {
        extractClientIP { ip =>
          parameters('hash) { hash =>
            headerValueByName("Accept") { accept =>

              val msisdn = getMsisdn(UUID.fromString(hash))

              accept match {

                case "application/json" =>
                  complete(HttpEntity(ContentTypes.`application/json`, Response(msisdn.toString, OK).toJson))

                case _ =>
                  complete(HttpEntity(MediaTypes.`application/xml`.toContentType(HttpCharsets.`UTF-8`),
                    XmlMsisdnResponse(msisdn, OK).toXml))
              }
            }
          }
        }
      } ~
        path("anonym" / "getHash") {
          extractClientIP { ip =>
            parameters('msisdn.as[Long]) { msisdn =>

              headerValueByName("Accept") { accept =>

                val hash = getHash(msisdn)

                accept match {

                  case "application/json" =>
                    complete(HttpEntity(ContentTypes.`application/json`, Response(hash.toString, OK).toJson))

                  case _ =>
                    complete(HttpEntity(MediaTypes.`application/xml`.toContentType(HttpCharsets.`UTF-8`),
                      XmlHashResponse(hash, OK).toXml))
                }
              }
            }
          }
        }
    }
}