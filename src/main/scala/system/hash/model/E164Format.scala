package system.hash.model

trait E164Format {

  val cc = 380 * 1000000000L
  val ndcs = List(67, 68, 96, 97, 98).zipWithIndex.toMap
  val ndcNums = 10000000

  def toE164(msisdn: Int): Long = cc + msisdn

  def fromE164(msisdn: Long): Int = (msisdn - cc).toInt

  def extract(msisdn: Int): (Int, Int) = {
    val ndc = msisdn / ndcNums
    val number = msisdn % ndcNums
    (ndc, number)
  }

  def toRange(ndc: Int): Range = {
    val lo = ndc * ndcNums
    val hi = lo + ndcNums
    lo until hi
  }
}