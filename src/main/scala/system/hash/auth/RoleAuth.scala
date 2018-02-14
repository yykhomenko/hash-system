package system.hash.auth

import akka.http.scaladsl.model.StatusCodes.Forbidden
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.Route
import system.hash.model.dao.User

trait RoleAuth {

  sealed abstract case class Role(role: String)
  object ReloadRole extends Role("reload")
  object MetricRole extends Role("metric")
  object ClientRole extends Role("client")

  def withRoleAuth(role: Role, user: User)(f: => Route): Route = {
    if (user.roles.contains(role.role))
      f
    else
      complete(Forbidden, s"You're don't have necessary role: ${role.role}")
  }
}