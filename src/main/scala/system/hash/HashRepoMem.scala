package system.hash

import java.util.concurrent.atomic.AtomicInteger

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import system.hash.helper.CommLineHelper.printProgress
import system.hash.model.{E164Format, MD5}

object HashRepoMem extends E164Format {

  val salt = "qweqeqe"

  private val msisdns = new collection.concurrent.TrieMap[MD5, Long]

  def getMsisdn(hash: String): Long = msisdns.getOrElse(MD5(hash), 0)

  def getHash(msisdn: String): String = {
    val digest = DigestUtils.getMd5Digest
    digest.update((msisdn + salt).getBytes)
    Hex.encodeHexString(digest.digest)
  }

  def loadHashes(): Unit = {

    val available = ndcs.size * ndcNums
    val progress = new AtomicInteger(0)
    val progressSet = (1L to 100L) map (_ * available / 100) toSet

    def writeHash(msisdn: Long): Unit = {

      val hash = MD5(getHash(msisdn.toString))
      assert(!msisdns.contains(hash), "Hashes contains duplicate! Pick up different salt!")

      msisdns(hash) = msisdn

      progress.incrementAndGet()
      if (progressSet(progress.get)) printProgress(available, progress.get)
    }

    for {
      ndc <- ndcs.keys
      number <- toRange(ndc).par
    } writeHash(cc + number)
  }
}