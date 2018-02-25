package system.hash.model

import java.util

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils

case class Hash(digest: Array[Byte]) {
  override def hashCode(): Int = util.Arrays.hashCode(digest)

  override def equals(that: scala.Any): Boolean =
    util.Arrays.equals(digest, that.asInstanceOf[Hash].digest)

  override def toString: String = Hex.encodeHexString(digest)
}

object Hash {
  def apply(msisdn: String, salt: String): Hash = Hash(msisdn.toLong, salt)

  def apply(msisdn: Long, salt: String): Hash = {
    val digest = DigestUtils.getMd5Digest
    digest.update((msisdn + salt).getBytes)
    Hash(digest.digest)
  }

  def apply(hash: String): Hash = Hash(Hex.decodeHex(hash))
}
