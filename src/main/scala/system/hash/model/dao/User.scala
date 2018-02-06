package system.hash.model.dao

import java.util
import java.util.UUID

import com.datastax.driver.mapping.Result
import com.datastax.driver.mapping.annotations.{Accessor, PartitionKey, Query, Table}

@Accessor trait UserAccessor {
  @Query("SELECT * FROM hash_system.users") def getAll: Result[User]
}

@Table(keyspace = "hash_system", name = "users", readConsistency = "QUORUM", writeConsistency = "QUORUM")
case class User(@PartitionKey id: UUID, login: String, password: String, allowedIp: util.Set[String], roles: util.Set[String]) {
  def this() = this(id = null, login = null, password = null, allowedIp = null, roles = null)
}