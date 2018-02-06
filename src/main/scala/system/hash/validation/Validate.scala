package system.hash.validation

import akka.http.scaladsl.server.Route
import com.typesafe.config.Config

trait Validate {

  def conf: Config

  val msisdnLength = conf.getInt("msisdn.length")

  def withMsisdnValidation(msisdn: String)(op: Boolean => Route): Route =
    op(msisdn.length == msisdnLength)

  def withHashValidation(hash: String)(op: Boolean => Route): Route =
    op(hash.length % 2 == 0)
}