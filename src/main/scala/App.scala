import java.util.UUID

import akka.http.scaladsl.model.{ContentTypes, HttpCharsets, HttpEntity, MediaTypes}
import akka.http.scaladsl.server.{HttpApp, Route}
import helper.CommLineHelper
import model.{HashRepo, Responses}

object App extends HttpApp with Responses with CommLineHelper {

  // todo add ip security,
  // todo add basic auth,
  // todo add error answers
  // todo add tests

  override protected def routes: Route = get {

    path("anonym" / "getMsisdn") {
      extractClientIP { ip =>
        parameters('hash) { hash =>
          headerValueByName("Accept") { accept =>

            val msisdn = HashRepo.getMsisdn(UUID.fromString(hash))

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

              val hash = HashRepo.getHash(msisdn)

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

  def main(args: Array[String]): Unit = {

    val (mode, fileName) = extractArgs(args)

    mode match {

      case "generate" =>
        withTimer("start write new hash file: " + fileName, HashRepo.writeTo(fileName))

      case "server" =>
        withTimer("start read hashes file: " + fileName, HashRepo.readFrom(fileName))
        startServer("0.0.0.0", 8080)
    }
  }
}