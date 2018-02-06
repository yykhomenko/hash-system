package system.hash

import akka.http.scaladsl.model.{HttpCharsets, HttpEntity, MediaTypes}
import akka.http.scaladsl.server.{HttpApp, Route}
import system.hash.model.dao.User
import system.hash.model.{BasicAuth, Responses}
import system.hash.repo.{HashRepo, UsersRepo}

object App extends HttpApp with BasicAuth with Responses {

  // todo add ip security,
  // todo add basic auth,
  // todo add error answers
  // todo add tests

  def main(args: Array[String]): Unit = {

    val (mode, fileName) = extractArgs(args)

    mode match {

      case "server" =>
        // withTimer("start load hashes", HashRepo.loadHashes())

        println(UsersRepo.users)

        startServer("0.0.0.0", 8080)
    }
  }

  override def routes: Route = get {

    path("anonym" / "getMsisdn") {
      parameters('hash) { hash =>
        withAuth { (ip, user) =>
          val response = HashRepo.getMsisdn(hash) match {
            case 0 => XmlMsisdnResponse("0", DataNotFound)
            case msisdn => XmlMsisdnResponse(msisdn.toString, Ok)
          }
          complete(HttpEntity(MediaTypes.`application/xml`.toContentType(HttpCharsets.`UTF-8`), response.toString))
        }
      }

    } ~
      path("anonym" / "getHash") {
        withAuth { (ip, user) =>
          parameters('msisdn) { msisdn =>
            val hash = HashRepo.getHash(msisdn)
            val response = XmlHashResponse(hash, Ok)
            complete(HttpEntity(MediaTypes.`application/xml`.toContentType(HttpCharsets.`UTF-8`), response.toString))
          }
        }
      }
  }

  override def users: Map[String, User] = UsersRepo.users
}
