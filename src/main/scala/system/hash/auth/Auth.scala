package system.hash.auth

import akka.http.scaladsl.server.Route
import system.hash.model.dao.User

trait Auth extends BasicAuth with IpAuth with RoleAuth {

  def withBasicAuthIp(f: (User, String) => Route): Route = {
    withBasicAuth { user =>
      withIpAuth(user.allowedIp) { ip =>
        f(user, ip)
      }
    }
  }

  def withBasicAuthIp(f: => Route): Route = {
    withBasicAuth { user =>
      withIpAuth(user.allowedIp) { ip =>
        f
      }
    }
  }

  def withAuth(role: Role)(f: => Route): Route = {
    withBasicAuth { user =>
      withIpAuth(user.allowedIp) { ip =>
        withRoleAuth(role, user) {
          f
        }
      }
    }
  }
}