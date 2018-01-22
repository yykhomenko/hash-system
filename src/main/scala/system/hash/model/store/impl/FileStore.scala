package system.hash.model.store.impl

import java.io._
import java.util.UUID

import system.hash.helper.CommLineHelper._
import system.hash.model.store.Store

import scala.collection.mutable

class FileStore extends Store {

  val fileName = "hashesUUID.bin"

  override def loadHashes(): Unit = {

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

  override def storeNewHashes(): Unit = {

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

    val available = ndcs.size * ndcNums
    var progress = 0L
    val progressSet = (1L to 100L) map (_ * available / 100) toSet

    def writeRecord(number: Int, out: DataOutputStream): Unit = {
      val uuid = getUniqUuid(uuidSet)
      out.writeInt(number)
      out.writeLong(uuid.getMostSignificantBits)
      out.writeLong(uuid.getLeastSignificantBits)

      progress += 1
      if (progressSet(progress)) printProgress(available, progress)
    }

    withDataOutputStream(fileName, out =>
      for {
        ndc <- ndcs.keys
        number <- toRange(ndc)
      } writeRecord(number, out)
    )
  }
}