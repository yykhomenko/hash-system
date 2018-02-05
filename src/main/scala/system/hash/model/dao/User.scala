package system.hash.model.dao

import java.util
import java.util.{Set, UUID}

import com.datastax.driver.mapping.Result
import com.datastax.driver.mapping.annotations.{Accessor, PartitionKey, Query, Table}

@Accessor trait UserAccessor {
  @Query("SELECT * FROM hash_system.users") def getAll: Result[User]
}

@Table(keyspace = "hash_system", name = "users", readConsistency = "QUORUM", writeConsistency = "QUORUM")
case class User(@PartitionKey id: UUID, login: String, password: String, allowedIp: util.Set[String], roles: util.Set[String]) {
  def this() = this(id = null, login = null, password = null, allowedIp = null, roles = null)
}

object User {

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