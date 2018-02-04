package system.hash

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import system.hash.model.{E164Format, MD5, Progress}

object HashRepoMem extends E164Format with Progress {

  def progressSize: Int = ndcs.size * ndcNums

  val salt = "qweqeqe" // todo load from db

  private val msisdns = collection.concurrent.TrieMap[MD5, Long]()

  def getMsisdn(hash: String): Long = msisdns.getOrElse(MD5(hash), 0)

  def getHash(msisdn: String): String = {
    val digest = DigestUtils.getMd5Digest
    digest.update((msisdn + salt).getBytes)
    Hex.encodeHexString(digest.digest)
  }

  def loadHashes(): Unit = {

    def writeHash(msisdn: Long): Unit = {

      val hash = MD5(getHash(msisdn.toString))
      assert(!msisdns.contains(hash), "Hashes contains duplicate! Pick up different salt!")

      msisdns(hash) = msisdn
      inc()
    }

    for {
      ndc <- ndcs.keys.par
      number <- toRange(ndc)
    } writeHash(cc + number)
  }
}