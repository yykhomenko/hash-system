package system.hash.model

import akka.http.scaladsl.server.Route
import com.typesafe.config.Config

trait Validation {

  def config: Config
  private val vConfig = config.getConfig("validation")

  private val msisdnLengthMin = vConfig.getInt("msisdn.length.min")
  private val msisdnLengthMax = vConfig.getInt("msisdn.length.max")

  def withMsisdnValidation(msisdn: String)(f: Boolean => Route): Route =
    f(msisdnLengthMin <= msisdn.length && msisdn.length <= msisdnLengthMax && (msisdn forall Character.isDigit))

  def withHashValidation(hash: String)(f: Boolean => Route): Route =
    f(hash.length % 2 == 0)
}