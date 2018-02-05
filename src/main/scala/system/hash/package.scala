package system

import akka.http.scaladsl.server.directives.Credentials
import system.hash.model.dao.User
import system.hash.repo.UsersRepo

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

package object hash {

  def myUserPassAuthenticator(credentials: Credentials): Future[Option[User]] =
    credentials match {
      case p @ Credentials.Provided(id) =>
        Future {
          UsersRepo.users.get(id).filter(user => p.verify(user.password))
        }
      case _ => Future.successful(None)
    }

  def extractArgs(args: Array[String]): (String, String) = {
    val mode = if (args.length > 0) args(0) else "server"
    val fileName = if (args.length > 1) args(1) else "hashesUUID.bin"

    (mode, fileName)
  }

  def withTimer(comment: String, block: => Unit): Unit = {
    println(comment)
    val start = System.currentTimeMillis
    block
    println(s"completed in ${(System.currentTimeMillis - start) / 1000}s")
  }
}