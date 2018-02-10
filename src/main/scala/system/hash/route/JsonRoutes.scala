package system.hash.route

import akka.http.scaladsl.server.Route
import system.hash.auth.BasicAuthIp
import system.hash.model.{Responses, Validation}
import system.hash.repo.HashRepo

trait JsonRoutes extends HashRepo with BasicAuthIp with Validation with Responses {

  def jsonRoutes: Route = get {

    pathPrefix("api") {

      path("msisdn" / Segment) { hash =>
        withBasicAuthIp {

          withHashValidation(hash) {

            case false => JsonResp(error = IncorrectHash).resp
            case true =>
              getMsisdn(hash) match {
                case None => JsonResp(error = DataNotFound).resp
                case Some(m) => JsonResp(m.toString, Ok).resp
              }
          }
        }

      } ~
        path("hash" / Segment) { msisdn =>
          withBasicAuthIp {
            withMsisdnValidation(msisdn) {
              case false => JsonResp(error = IncorrectMsisdn).resp
              case true => JsonResp(getHash(msisdn), Ok).resp
            }
          }
        }
    }
  }
}