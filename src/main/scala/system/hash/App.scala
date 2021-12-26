package system.hash

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import system.hash.actor.MetricController
import system.hash.route.Routes

import scala.concurrent.Await
import scala.concurrent.duration._

object App extends App with Routes {
  implicit val system: ActorSystem = ActorSystem("hash-system")
  val metric = system.actorOf(MetricController.props, "metric-controller")

  withTimer("start load hashes", loadHashes())
  Http().newServerAt("0.0.0.0", 8080).bind(routes)
  Await.result(system.whenTerminated, Duration.Inf)
}