package system.hash.route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import system.hash.auth.BasicAuthIp
import system.hash.model.{Responses, Validation}
import system.hash.repo.HashRepo

trait UserRoutes extends HashRepo with BasicAuthIp with Validation with Responses {

  def userRoutes: Route =

    get {

      handleExceptions(xmlExHandler) {

        path("anonym" / "getMsisdn") {
          withBasicAuthIp {
            parameters('hash) { hash =>

              withHashValidation(hash) {

                case false => XmlMsisdnResp(error = IncorrectHash).resp
                case true =>
                  getMsisdn(hash) match {
                    case None => XmlMsisdnResp(error = DataNotFound).resp
                    case Some(m) => XmlMsisdnResp(m.toString, Ok).resp
                  }
              }
            }
          }

        } ~
          path("anonym" / "getHash") {
            withBasicAuthIp {
              parameters('msisdn) { msisdn =>

                withMsisdnValidation(msisdn) {
                  case false => XmlHashResp(error = IncorrectMsisdn).resp
                  case true => XmlHashResp(getHash(msisdn), Ok).resp
                }
              }
            }
          }
      }
    }
}