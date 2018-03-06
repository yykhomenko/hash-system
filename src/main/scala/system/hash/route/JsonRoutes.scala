package system.hash.route

import akka.actor.ActorRef
import akka.http.scaladsl.server.Route
import system.hash.actor.MetricController.{IncJsonHashError, IncJsonHashOk, IncJsonMsisdnError, IncJsonMsisdnOk}
import system.hash.auth.Auth
import system.hash.model._
import system.hash.repo.HashRepo

trait JsonRoutes extends HashRepo with Auth with Validation with Responses {

  def metric: ActorRef

  def jsonRoutes: Route = get {

    pathPrefix("api" / "v1") {

      path("msisdn" / Segment) { hash =>
        withAuth(ClientRole) {

          withHashValidation(hash) {

            case false =>
              metric ! IncJsonMsisdnError(IncorrectHash)
              JsonResp(error = IncorrectHash).resp
            case true =>
              getMsisdn(hash) match {
                case None =>
                  metric ! IncJsonMsisdnError(DataNotFound)
                  JsonResp(error = DataNotFound).resp
                case Some(m) =>
                  metric ! IncJsonMsisdnOk
                  JsonResp(m.toString, Ok).resp
              }
          }
        }

      } ~
        path("hash" / Segment) { msisdn =>
          withAuth(ClientRole) {
            withMsisdnValidation(msisdn) {
              case false =>
                metric ! IncJsonHashError(IncorrectMsisdn)
                JsonResp(error = IncorrectMsisdn).resp
              case true =>
                metric ! IncJsonHashOk
                JsonResp(getHash(msisdn), Ok).resp
            }
          }
        }
    }
  }
}