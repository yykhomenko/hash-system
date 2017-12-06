import java.util.UUID

import Repo._
import akka.http.scaladsl.server.{Directives, Route}

object Routes extends Directives {

  def routes(): Route =

    get {
      path("/") {
        complete(UUID.randomUUID().toString)
      } ~
        path("anonym" / "getMsisdn") {
          extractClientIP { ip =>
            parameters('hash) { hash =>
              complete(getMsisdn(UUID.fromString(hash)).toString)
            }
          }
        } ~
        path("anonym" / "getHash") {
          extractClientIP { ip =>
            parameters('msisdn.as[Int]) { msisdn =>
              complete(getHash(msisdn).toString)
            }
          }
        }
    }
}