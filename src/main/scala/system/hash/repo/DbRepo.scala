package system.hash.repo

import com.datastax.driver.core.Cluster
import com.datastax.driver.mapping.MappingManager
import system.hash.model.dao.{ConfigAccessor, UserAccessor}

import scala.collection.JavaConverters._

object DbRepo {

  private val cluster = Cluster.builder()
    .addContactPoint("127.0.0.1")
    .build()

  private val session = cluster.connect("hash_system")

  private val configAccessor = new MappingManager(session).createAccessor(classOf[ConfigAccessor])
  private val userAccessor = new MappingManager(session).createAccessor(classOf[UserAccessor])

  val configs = configAccessor.getAll.all.asScala.map(config => (config.key, config.value)).toMap.asJava
  val users = userAccessor.getAll.all.asScala.map(user => (user.login, user)).toMap
}