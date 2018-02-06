package system.hash.auth

import akka.http.scaladsl.server.Route
import system.hash.model.dao.User

trait BasicAuthIp extends BasicAuth with IpAuth {

  def withBasicAuthIp(f: (User, String) => Route): Route = {
    withBasicAuth { user =>
      withIpAuth(user.allowedIp) { ip =>
        f(user, ip)
      }
    }
  }

  def withBasicAuthIp(f: Unit => Route): Route = {
    withBasicAuth { user =>
      withIpAuth(user.allowedIp) { ip =>
        f()
      }
    }
  }
}