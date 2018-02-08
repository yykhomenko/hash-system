package system.hash.repo

import com.typesafe.scalalogging.LazyLogging
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.{DigestUtils, MessageDigestAlgorithms}
import system.hash.model.{E164Format, MD5, Progress}

object HashRepo extends E164Format with Progress with LazyLogging {

  private val algorithm = DbRepo.config.getString("algorithm")
  private val salt = DbRepo.config.getString("salt")

  logger.info(s"used algorithm: $algorithm, also available: ${MessageDigestAlgorithms.values()}")

  private val msisdns = collection.concurrent.TrieMap[MD5, Long]()
//  msisdns(MD5("55c201c6760f2cbc78e674e2f66e453f")) = 380672244089L

  protected def progressSize: Int = ndcs.size * ndcNums

  def getMsisdn(hash: String): Option[Long] = msisdns.get(MD5(hash))

  def loadHashes(): Unit = {

    def writeHash(msisdn: Long): Unit = {
      val digest = getHash(msisdn.toString)
      val hash = MD5(digest)
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