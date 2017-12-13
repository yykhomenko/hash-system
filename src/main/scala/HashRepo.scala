import java.util.UUID

import model.FileStore

object HashRepo extends FileStore {

  def getMsisdn(hash: UUID): Long = toE164(msisdns(hash))

  def getHash(msisdn: Long): UUID = {
    val (ndc, number) = extract(fromE164(msisdn))
    hashes(ndcs(ndc))(number)
  }
}