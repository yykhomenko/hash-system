package system.hash.auth

import akka.http.scaladsl.server.Route
import system.hash.model.dao.User

trait BasicAuthIp extends BasicAuth with IpAuth {

  def withBasicAuthIp(op: (User, String) => Route): Route = {
    withBasicAuth { user =>
      withIpAuth(user.allowedIp) { ip =>
        op(user, ip)
      }
    }
  }

  def withBasicAuthIp(op: Unit => Route): Route = {
    withBasicAuth { user =>
      withIpAuth(user.allowedIp) { ip =>
        op()
      }
    }
  }
}