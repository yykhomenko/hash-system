package system.hash.route

import akka.actor.ActorRef
import akka.http.scaladsl.server.Route
import akka.pattern._
import akka.util.Timeout
import system.hash.actor.MetricController.{MetricsReq, MetricsResp}
import system.hash.auth.Auth
import system.hash.model.Responses
import scala.concurrent.duration._

trait MetricRoutes extends Auth with Responses {

  implicit val timeout = Timeout(3 seconds)

  def metric: ActorRef

  def metricRoutes: Route = get {

    path("metrics") {
      withAuth(MetricRole) {
        onSuccess(metric ? MetricsReq) {
          case MetricsResp(metrics) => complete(metrics)
        }
      }
    }
  }
}