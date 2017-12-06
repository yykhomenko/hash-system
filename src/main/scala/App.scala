import CommLineHelper._
import akka.http.scaladsl.server.{HttpApp, Route}

object App extends HttpApp {

  def main(args: Array[String]): Unit = {

    val (mode, fileName) = extractArgs(args)

    mode match {

      case "generate" =>
        withTimer("start write new hash file: " + fileName, Repo.writeTo(fileName))

      case "server" =>
        withTimer("start read hashes file: " + fileName, Repo.readFrom(fileName))
        startServer("0.0.0.0", 8080)
    }
  }

  override protected def routes: Route = Routes.routes()
}