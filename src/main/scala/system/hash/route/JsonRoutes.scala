package system.hash.route

import akka.actor.ActorRef
import akka.http.scaladsl.server.Route
import system.hash.actor.MetricController.{IncJsonHashOk, IncJsonMsisdnOk}
import system.hash.auth.BasicAuthIp
import system.hash.model.{Responses, Validation}
import system.hash.repo.HashRepo

trait JsonRoutes extends HashRepo with BasicAuthIp with Validation with Responses {

  def metric: ActorRef

  def jsonRoutes: Route = get {

    pathPrefix("api") {

      path("msisdn" / Segment) { hash =>
        withBasicAuthIp {

          withHashValidation(hash) {

            case false => JsonResp(error = IncorrectHash).resp
            case true =>
              getMsisdn(hash) match {
                case None => JsonResp(error = DataNotFound).resp
                case Some(m) =>
                  metric ! IncJsonMsisdnOk
                  JsonResp(m.toString, Ok).resp
              }
          }
        }

      } ~
        path("hash" / Segment) { msisdn =>
          withBasicAuthIp {
            withMsisdnValidation(msisdn) {
              case false =>
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