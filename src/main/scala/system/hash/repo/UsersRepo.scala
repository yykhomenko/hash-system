package system.hash.repo

import com.datastax.driver.core.Cluster
import com.datastax.driver.mapping.MappingManager
import system.hash.model.dao.UserAccessor

import scala.collection.JavaConverters._

object UsersRepo {

  private val cluster = Cluster.builder()
    .addContactPoint("127.0.0.1")
    .build()

  private val session = cluster.connect("hash_system")

  private val accessor = new MappingManager(session).createAccessor(classOf[UserAccessor])

  val users = accessor.getAll.all.asScala.map(user => (user.login, user)).toMap
}