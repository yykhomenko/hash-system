package system.hash.auth

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.Credentials
import system.hash.App.authenticateBasicAsync
import system.hash.model.dao.User

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait BasicAuth {

  def users: Map[String, User]

  def userPassAuthenticator(credentials: Credentials): Future[Option[User]] =
    credentials match {
      case p@Credentials.Provided(id) =>
        Future {
          users.get(id).filter(user => p.verify(user.password))
        }
      case _ => Future.successful(None)
    }

  def withBasicAuth(op: User => Route): Route =
    authenticateBasicAsync(realm = "hash system", userPassAuthenticator) { user =>
      op(user)
    }
}