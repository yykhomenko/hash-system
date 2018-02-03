package system.hash

import java.util.UUID

import akka.http.scaladsl.model.{ContentTypes, HttpCharsets, HttpEntity, MediaTypes}
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
          parameters('msisdn) { msisdn =>

           // headerValueByName("Accept") { accept =>

//              val hash = HashRepo.getHashMD5(msisdn)
              val hash = HashRepo.getHash(msisdn.toLong)

            complete(HttpEntity(MediaTypes.`application/xml`.toContentType(HttpCharsets.`UTF-8`), XmlHashResponse(hash.toString, OK).toXml))
          }
        }
      } //~
//      path("anonym" / "getHash") { // json support
//        extractClientIP { ip =>
//          parameters('msisdn) { msisdn =>
//
//            // headerValueByName("Accept") { accept =>
//
//            //              val hash = HashRepo.getHashMD5(msisdn)
//            val hash = HashRepo.getHash(msisdn.toLong)
//
//            complete(HttpEntity(MediaTypes.`application/xml`.toContentType(HttpCharsets.`UTF-8`), XmlHashResponse(hash.toString, OK).toXml))
//          }
//        }
//      }
  }

  def main(args: Array[String]): Unit = {

    val (mode, fileName) = extractArgs(args)

    mode match {

      case "generator" =>
        withTimer("start write new hash file: " + fileName, HashRepo.storeNewHashes())

      case "server" =>
       // withTimer("start read hashes file: " + fileName, HashRepo.loadHashes())
        startServer("0.0.0.0", 8080)
    }
  }
}