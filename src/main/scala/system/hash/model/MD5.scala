package system.hash.model

import java.util

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils

case class MD5(digest: Array[Byte]) {
  override def hashCode(): Int = util.Arrays.hashCode(digest)
  override def equals(that: scala.Any): Boolean = util.Arrays.equals(digest, that.asInstanceOf[MD5].digest)
  override def toString: String = Hex.encodeHexString(digest)
}

object MD5 {
  def apply(msisdn: String, salt: String): MD5 = MD5(msisdn.toLong, salt)

  def apply(msisdn: Long, salt: String): MD5 = {
    val digest = DigestUtils.getSha512Digest
    digest.update((msisdn + salt).getBytes)
    MD5(digest.digest)
  }

  def apply(hash: String): MD5 = MD5(Hex.decodeHex(hash))
}