package system.hash

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import org.scalameter._

class Test {// extends App {

  val standardConfig = config(
    Key.exec.minWarmupRuns -> 200,
    Key.exec.maxWarmupRuns -> 400,
    Key.exec.benchRuns -> 60,
    Key.verbose -> true
  ) withWarmer new Warmer.Default


  val msisdn = "380672244089"
  val salt = "Qzectbu8"

  val digest = DigestUtils.getSha512Digest


 // System.out.println()

  val time = standardConfig measure {
    digest.update((msisdn + salt).getBytes)
    val bytes = digest.digest
    val chars = Hex.encodeHex(bytes)
  }

  println(s"time: $time")
}
