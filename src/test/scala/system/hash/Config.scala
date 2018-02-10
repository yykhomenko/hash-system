package system.hash

import akka.http.scaladsl.model.headers.BasicHttpCredentials
import system.hash.model.MD5
import system.hash.route.Routes

trait Config extends Routes {

  val invalidCredentials = BasicHttpCredentials("Peter", "pan")
  val validCredentials = BasicHttpCredentials("test-client", "test-client-password")

  val msisdn = 380672240000L
  val incorrectMsisdn1 = 38067224L
  val incorrectMsisdn2 = "38067224qweqw"

  val hashMD5 = "8801ddf0a8ef82313293d7cf3ab5d46c"
  val incorrectHashMD5 = "9801ddf0a8ef82313293d7cf3ab5d46ce"
  val absentHashMD5 = "0001ddf0a8ef82313293d7cf3ab5d65f"

  msisdns(MD5(hashMD5)) = msisdn
}