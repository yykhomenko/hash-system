import java.io.{DataInputStream, _}
import java.util.UUID

import CommLineHelper._

import scala.collection.mutable

object Repo {

  private val cc = 380 * 1000000000L
  private val ndcs = List(67, 68, 96, 97, 98).zipWithIndex.toMap
  private val ndcNums = 10000000

  private val hashes = Array.ofDim[UUID](ndcs.size, ndcNums)
  private val msisdns = new mutable.HashMap[UUID, Int] {
    override def initialSize = Math.round(ndcs.size * ndcNums * 1.5).toInt
  }

  private def toE164(msisdn: Int) = cc + msisdn
  private def fromE164(msisdn: Long) = (msisdn - cc).toInt

  private def extract(msisdn: Int) = {
    val ndc = msisdn / ndcNums
    val number = msisdn % ndcNums
    (ndc, number)
  }

  def readFrom(fileName: String): Unit = {

    def withDataInputStream(fileName: String, op: DataInputStream => Unit): Unit = {
      val in = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName), 1000000))
      try { op(in) } finally { in.close() }
    }

    def readRecord(in: DataInputStream): Unit = {
      val msisdn = in.readInt()
      val most = in.readLong()
      val least = in.readLong()

      val (ndc, number) = extract(msisdn)
      val uuid = new UUID(most, least)

      hashes(ndcs(ndc))(number) = uuid
      msisdns(uuid) = msisdn
    }

    withDataInputStream(fileName, in => {

      val bytesPerObject = 20

      var available = in.available()
      var progress = 0L
      val progressSet = (1L to 100L) map (_ * available / 100) toSet

      while (progress < available) {

        progress += bytesPerObject
        if (progressSet(progress)) printProgress(available, progress)

        readRecord(in)
      }
    })
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

    def withDataOutputStream(fileName: String, op: DataOutputStream => Unit): Unit = {
      val out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName), 1000000))
      try { op(out) } finally { out.close() }
    }

    val uuidSet = mutable.Set[UUID]()

    def writeRecord(number: Int, out: DataOutputStream): Unit = {
      val uuid = getUniqUuid(uuidSet)
      out.writeInt(number)
      out.writeLong(uuid.getMostSignificantBits)
      out.writeLong(uuid.getLeastSignificantBits)
    }

    withDataOutputStream(fileName, out =>
      for {
        ndc <- ndcs.keys
        number <- toRange(ndc)
      } writeRecord(number, out)
    )
  }

  def getMsisdn(hash: UUID): Long = toE164(msisdns(hash))

  def getHash(msisdn: Long): UUID = {
    val (ndc, number) = extract(fromE164(msisdn))
    hashes(ndcs(ndc))(number)
  }
}