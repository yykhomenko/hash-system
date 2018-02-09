package system.hash

import akka.http.scaladsl.server.{HttpApp, Route}
import system.hash.route.UserRoutes

object App extends HttpApp with UserRoutes {

  override def routes: Route = userRoutes

  def main(args: Array[String]): Unit = {
//    withTimer("start load hashes", loadHashes())
    startServer("0.0.0.0", 8080)
  }
}

// todo add json support
// todo add logging with logstash console
// todo add tests