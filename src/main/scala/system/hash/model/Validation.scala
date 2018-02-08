package system.hash.model

import akka.http.scaladsl.server.Route
import com.typesafe.config.Config

trait Validation {

  def conf: Config
  private val vConfig = conf.getConfig("validation")

  private val msisdnLengthMin = vConfig.getInt("msisdn.length.min")
  private val msisdnLengthMax = vConfig.getInt("msisdn.length.max")

  def withMsisdnValidation(msisdn: String)(f: Boolean => Route): Route =
    f(msisdnLengthMin <= msisdn.length && msisdn.length <= msisdnLengthMax)

  def withHashValidation(hash: String)(f: Boolean => Route): Route =
    f(hash.length % 2 == 0)
}