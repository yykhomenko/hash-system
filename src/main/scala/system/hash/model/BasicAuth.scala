package system.hash.model

import akka.http.scaladsl.model.StatusCodes.Forbidden
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.Credentials
import system.hash.App.{authenticateBasicAsync, complete, extractClientIP}
import system.hash.model.dao.User

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait BasicAuth {

  def users: Map[String, User]

  def myUserPassAuthenticator(credentials: Credentials): Future[Option[User]] =
    credentials match {
      case p@Credentials.Provided(id) =>
        Future {
          users.get(id).filter(user => p.verify(user.password))
        }
      case _ => Future.successful(None)
    }

  def withAuth(op: (String, User) => Route): Route = {
    extractClientIP { remote =>
      authenticateBasicAsync(realm = "secure site", myUserPassAuthenticator) { user =>
        val opt = Option(remote.getAddress().get())
          .map(address => address.getHostAddress)
          .filter(host => User.matchIp(host, user.allowedIp))

        opt match {
          case Some(ip) => op(ip, user)
          case None => complete((Forbidden, "ip not allowed"))
        }
      }
    }
  }
}
