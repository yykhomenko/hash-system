package system.hash.validation

import akka.http.scaladsl.server.Route
import com.typesafe.config.Config

trait Validate {

  def conf: Config

  val msisdnLength = conf.getInt("msisdn.length")

  def withMsisdnValidation(msisdn: String)(f: Boolean => Route): Route =
    f(msisdn.length == msisdnLength)

  def withHashValidation(hash: String)(f: Boolean => Route): Route =
    f(hash.length % 2 == 0)
}