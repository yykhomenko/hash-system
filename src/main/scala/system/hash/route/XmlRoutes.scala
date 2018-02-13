package system.hash.route

import akka.actor.ActorRef
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import com.typesafe.scalalogging.LazyLogging
import system.hash.actor.MetricController.{IncXmlHashOk, IncXmlMsisdnOk}
import system.hash.auth.BasicAuthIp
import system.hash.model.{Responses, Validation}
import system.hash.repo.HashRepo

trait XmlRoutes extends HashRepo with BasicAuthIp with Validation with Responses with LazyLogging {

  def metric: ActorRef

  def xmlRoutes: Route = handleExceptions(xmlExHandler) {

    get {

      pathPrefix("anonym") {

        path("getMsisdn") {
          withBasicAuthIp {
            parameters('hash) { hash =>

              withHashValidation(hash) {

                case false => XmlMsisdnResp(error = IncorrectHash).resp
                case true =>
                  getMsisdn(hash) match {
                    case None => XmlMsisdnResp(error = DataNotFound).resp
                    case Some(m) =>
                      metric ! IncXmlMsisdnOk
                      XmlMsisdnResp(m.toString, Ok).resp
                  }
              }
            }
          }

        } ~
          path("getHash") {
            withBasicAuthIp {
              parameters('msisdn) { msisdn =>

                withMsisdnValidation(msisdn) {
                  case false =>
                    XmlHashResp(error = IncorrectMsisdn).resp
                  case true =>
                    metric ! IncXmlHashOk
                    XmlHashResp(getHash(msisdn), Ok).resp
                }
              }
            }
          }
      }
    }
  }

  private def xmlExHandler = ExceptionHandler {
    case t: Throwable =>
      extractUri { uri =>
        logger.error(s"Request to $uri could not be handled normally", t)
        XmlMsisdnResp(error = InternalError).resp
      }
  }
}