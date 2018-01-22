package system.hash

import java.util.UUID

import system.hash.model.E164Format
import system.hash.model.store.impl.FileStore

object HashRepo extends FileStore with E164Format {

  def getMsisdn(hash: UUID): Long = toE164(msisdns(hash))

  def getHash(msisdn: Long): UUID = {
    val (ndc, number) = extract(fromE164(msisdn))
    hashes(ndcs(ndc))(number)
  }
}