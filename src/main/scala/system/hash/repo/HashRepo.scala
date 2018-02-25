package system.hash.repo

import com.typesafe.scalalogging.LazyLogging
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.{DigestUtils, MessageDigestAlgorithms}
import system.hash.model.{E164Format, Hash, ConsoleMetric}

trait HashRepo extends CassandraDB with E164Format with ConsoleMetric with LazyLogging {

  private val algorithm = config.getString("algorithm")
  private val salt = config.getString("salt")

  protected val msisdns = collection.concurrent.TrieMap[Hash, Long]()

  protected def progressSize: Int = ndcs.size * ndcNums

  def getMsisdn(hash: String): Option[Long] = msisdns.get(Hash(hash))

  def loadHashes(): Unit = {

    logger.info(s"used algorithm: $algorithm, supports: ${MessageDigestAlgorithms.values()}")

    def writeHash(msisdn: Long): Unit = {
      val digest = getHash(msisdn.toString)
      val hash = Hash(digest)
      assert(!msisdns.contains(hash),
        "Hashes contains duplicate! Pick up different salt!")

      msisdns(hash) = msisdn
      inc()
    }

    for {
      ndc <- ndcs.keys
      number <- toRange(ndc).par
    } writeHash(cc + number)
  }

  def getHash(msisdn: String): String = {
    val digest = DigestUtils.getDigest(algorithm)
    digest.update((msisdn + salt).getBytes)
    Hex.encodeHexString(digest.digest)
  }
}