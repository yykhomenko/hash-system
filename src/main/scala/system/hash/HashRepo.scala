package system.hash

import java.security.MessageDigest
import java.util.UUID

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import system.hash.model.E164Format
import system.hash.model.store.impl.FileStore

object HashRepo extends FileStore with E164Format {

  def getMsisdn(hash: UUID): Long = toE164(msisdns(hash))

  def getHash(msisdn: Long): UUID = {
    val (ndc, number) = extract(fromE164(msisdn))
    hashes(ndcs(ndc))(number)
  }


  val salt = "qweqweqwe"


  def getHashMD5(msisdn: String): String = {
    val digest: MessageDigest = DigestUtils.getMd5Digest
    digest.update((msisdn + salt).getBytes)
    Hex.encodeHexString(digest.digest)
  }
}