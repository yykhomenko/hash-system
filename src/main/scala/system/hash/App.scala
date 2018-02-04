package system.hash

import akka.http.scaladsl.model.{HttpCharsets, HttpEntity, MediaTypes}
import akka.http.scaladsl.server.{HttpApp, Route}
import system.hash.helper.CommLineHelper._
import system.hash.model.Responses

object App extends HttpApp with Responses {

  // todo add ip security,
  // todo add basic auth,
  // todo add error answers
  // todo add tests

  override protected def routes: Route = get {

    path("anonym" / "getMsisdn") {
      extractClientIP { ip =>
        parameters('hash) { hash =>

          val response = HashRepoMem.getMsisdn(hash) match {
            case 0 => XmlMsisdnResponse(0, DataNotFound).toXml
            case msisdn => XmlMsisdnResponse(msisdn, Ok).toXml
          }

          complete(HttpEntity(MediaTypes.`application/xml`.toContentType(HttpCharsets.`UTF-8`), response))
        }
      }

    } ~
      path("anonym" / "getHash") {
        extractClientIP { ip =>
          parameters('msisdn) { msisdn =>
            val hash = HashRepoMem.getHash(msisdn)
            val response = XmlHashResponse(hash, Ok).toXml
            complete(HttpEntity(MediaTypes.`application/xml`.toContentType(HttpCharsets.`UTF-8`), response))
          }
        }
      }
  }

  def main(args: Array[String]): Unit = {

    val (mode, fileName) = extractArgs(args)

    mode match {

      case "server" =>
        withTimer("start load hashes", HashRepoMem.loadHashes())
        startServer("0.0.0.0", 8080)
    }
  }
}