package system.hash.repo

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import system.hash.model.{E164Format, MD5, Progress}

object HashRepo extends E164Format with Progress {

  val salt = "qweqeqe" // todo load from db
  private val msisdns = collection.concurrent.TrieMap[MD5, Long]()

  protected def progressSize: Int = ndcs.size * ndcNums

  def getMsisdn(hash: String): Long = msisdns.getOrElse(MD5(hash), 0)

  def loadHashes(): Unit = {

    def writeHash(msisdn: Long): Unit = {

      val hash = MD5(getHash(msisdn.toString))
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
    val digest = DigestUtils.getMd5Digest
    digest.update((msisdn + salt).getBytes)
    Hex.encodeHexString(digest.digest)
  }
}
