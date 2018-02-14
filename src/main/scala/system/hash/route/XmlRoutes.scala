package system.hash.route

import akka.actor.ActorRef
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import com.typesafe.scalalogging.LazyLogging
import system.hash.actor.MetricController._
import system.hash.auth.Auth
import system.hash.model.{Responses, Validation, _}
import system.hash.repo.HashRepo

trait XmlRoutes extends HashRepo with Auth with Validation with Responses with LazyLogging {

  def metric: ActorRef

  def xmlRoutes: Route = handleExceptions(xmlExHandler) {

    get {

      pathPrefix("anonym") {

        path("getMsisdn") {
          withAuth(ClientRole) {
            parameters('hash) { hash =>

              withHashValidation(hash) {

                case false =>
                  metric ! IncXmlMsisdnError(IncorrectHash)
                  XmlMsisdnResp(error = IncorrectHash).resp
                case true =>
                  getMsisdn(hash) match {
                    case None =>
                      metric ! IncXmlMsisdnError(DataNotFound)
                      XmlMsisdnResp(error = DataNotFound).resp
                    case Some(m) =>
                      metric ! IncXmlMsisdnOk
                      XmlMsisdnResp(m.toString, Ok).resp
                  }
              }
            }
          }

        } ~
          path("getHash") {
            withAuth(ClientRole) {
              parameters('msisdn) { msisdn =>

                withMsisdnValidation(msisdn) {
                  case false =>
                    metric ! IncXmlHashError(IncorrectMsisdn)
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
        metric ! IncXmlError(InternalHashSysError)
        XmlMsisdnResp(error = InternalHashSysError).resp
      }
  }
}