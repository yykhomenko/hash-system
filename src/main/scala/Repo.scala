import java.io._
import java.util.UUID

import CommLineHelper._

import scala.collection.mutable

object Repo {

  private val ndcs = List(67, 68, 96, 97, 98).zipWithIndex.toMap
  private val ndcNums = 10000000

  private val hashes = Array.ofDim[UUID](ndcs.size, ndcNums)
  private val msisdns = new mutable.HashMap[UUID, Int] {
    override def initialSize = Math.round(ndcs.size * ndcNums * 1.3).toInt
  }

  private def extract(msisdn: Int) = {
    val ndc = msisdn / ndcNums
    val number = msisdn % ndcNums
    (ndc, number)
  }

  def readFrom(fileName: String): Unit = {

    val in = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName), 1000000))

    try {

      val bytesPerObject = 20

      var available = in.available()
      var progress = 0L
      val progressSet = (1L to 100L) map (p => available * p / 100) toSet

      while (progress < available) {

        progress += bytesPerObject

        if (progressSet(progress))
          printProgress(available, progress)

        val msisdn = in.readInt()
        val most = in.readLong()
        val least = in.readLong()

        val (ndc, number) = extract(msisdn)
        val uuid = new UUID(most, least)

        hashes(ndcs(ndc))(number) = uuid
        msisdns(uuid) = msisdn
      }

    } finally {
      in.close()
    }
  }

  def writeTo(fileName: String): Unit = {

    def toRange(ndc: Int) = {
      val lo = ndc * ndcNums
      val hi = lo + ndcNums
      lo until hi
    }

    def getUniqUuid(uuidSet: mutable.Set[UUID]): UUID = {
      val uuid = UUID.randomUUID()
      if (!uuidSet(uuid)) {
        uuidSet += uuid
        uuid
      }
      else getUniqUuid(uuidSet)
    }

    val uuidSet = mutable.Set[UUID]()
    val out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)))

    try {

      for {
        ndc <- ndcs.keys
        number <- toRange(ndc)
      } {
        val uuid = getUniqUuid(uuidSet)
        out.writeInt(number)
        out.writeLong(uuid.getMostSignificantBits)
        out.writeLong(uuid.getLeastSignificantBits)
      }

    } finally {
      out.close()
    }
  }

  def getMsisdn(hash: UUID): Int = msisdns(hash)

  def getHash(msisdn: Int): UUID = {
    val (ndc, number) = extract(msisdn)
    hashes(ndcs(ndc))(number)
  }
}