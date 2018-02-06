package system.hash.auth

import java.util

import akka.http.scaladsl.model.StatusCodes.Forbidden
import akka.http.scaladsl.server.Route
import system.hash.App.{complete, extractClientIP}

trait IpAuth {

  def withIpAuth(allowedIps: util.Set[String])(op: String => Route): Route = {
    extractClientIP { remote =>
      val ip = Option(remote.getAddress().get())
        .map(address => address.getHostAddress)
        .getOrElse("127.0.0.1")

      if (matchIp(ip, allowedIps)) op(ip)
      else complete((Forbidden, s"ip not allowed: $ip"))
    }
  }

  def matchIp(ip: String, patternIps: util.Set[String]): Boolean =
    patternIps.contains(ip) || patternIps.stream.anyMatch((patternIp: String) => matchIp(ip, patternIp))

  def matchIp(ip: String, patternIp: String): Boolean = {
    if (ip == null || patternIp == null) throw new IllegalArgumentException("matchIp incorrect arguments: ip:" + ip + " patternIp:" + patternIp)
    if (ip.contains(".")) return matchIpV4(ip, patternIp)
    if (ip.contains(":")) return matchIpV6(ip, patternIp)
    throw new IllegalArgumentException("ip incorrect:" + ip)
  }

  private def matchIpV4(ip: String, patternIp: String): Boolean = {

    if (!patternIp.contains(".")) return false

    val userIp = ip.split("\\.")
    if (userIp.length != 4) throw new IllegalArgumentException("ip v4 incorrect:" + patternIp)

    val pattern = patternIp.split("\\.")
    if (pattern.length != 4) throw new IllegalArgumentException("patternIp v4 incorrect:" + patternIp)
    (pattern(0) == "*" || pattern(0) == userIp(0)) &&
      (pattern(1) == "*" || pattern(1) == userIp(1)) &&
      (pattern(2) == "*" || pattern(2) == userIp(2)) &&
      (pattern(3) == "*" || pattern(3) == userIp(3))
  }

  private def matchIpV6(ip: String, patternIp: String): Boolean = {

    if (!patternIp.contains(":")) return false

    val userIp = ip.split(":")
    if (userIp.length != 8) throw new IllegalArgumentException("patternIp v6 incorrect:" + patternIp)

    val pattern = patternIp.split(":")
    if (pattern.length != 8) throw new IllegalArgumentException("patternIp v6 incorrect:" + patternIp)

    compareDigit(userIp(1), pattern(1)) &&
      compareDigit(userIp(2), pattern(2)) &&
      compareDigit(userIp(3), pattern(3)) &&
      compareDigit(userIp(4), pattern(4)) &&
      compareDigit(userIp(5), pattern(5)) &&
      compareDigit(userIp(6), pattern(6)) &&
      compareDigit(userIp(7), pattern(7))
  }

  private def compareDigit(ipDigit: String, patternDigit: String) =
    patternDigit == "*" || Integer.valueOf(patternDigit, 16) == Integer.valueOf(ipDigit, 16)
}
