package system.hash.model.dao

import java.util
import java.util.UUID

import com.datastax.driver.mapping.Result
import com.datastax.driver.mapping.annotations.{Accessor, PartitionKey, Query, Table}

@Accessor trait ConfigAccessor {
  @Query("SELECT * FROM hash_system.configs") def getAll: Result[Config]
}

@Table(keyspace = "hash_system", name = "configs", readConsistency = "QUORUM", writeConsistency = "QUORUM")
case class Config(@PartitionKey key: String, value: String) {
  def this() = this(key = null, value = null)
}