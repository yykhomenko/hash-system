package system.hash

import akka.http.scaladsl.server.HttpApp
import system.hash.route.Routes

object App extends HttpApp with Routes {

  def main(args: Array[String]): Unit = {
    //withTimer("start load hashes", loadHashes())
    startServer("0.0.0.0", 8080)
  }
}

// todo add DB credentials support
// todo add test containers for cassandra
// todo add logging with logstash console
// todo add prometeus metrics