package system.hash.xml

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.{HttpChallenge, `WWW-Authenticate`}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}
import system.hash.Config
import system.hash.route.Routes

class XmlGetHashTest extends WordSpec with Matchers with ScalatestRouteTest with Routes with Config {

  val hashUri = s"/anonym/getHash?msisdn=$msisdn"
  val incorrectHashUri1 = s"/anonym/getHash?msisdn=$incorrectMsisdn1"
  val incorrectHashUri2 = s"/anonym/getHash?msisdn=$incorrectMsisdn2"

  "The hash system with XML protocol getHash method" should {

    s"return Unauthorized error for GET requests to $hashUri without credentials" in {

      Get(hashUri) ~> Route.seal(routes) ~> check {
        status shouldEqual StatusCodes.Unauthorized
        responseAs[String] shouldEqual "The resource requires authentication, which was not supplied with the request"
        header[`WWW-Authenticate`].get.challenges.head shouldEqual HttpChallenge("Basic", Some("hash system"), Map("charset" → "UTF-8"))
      }
    }

    s"return Unauthorized error for GET requests to $hashUri without invalid credentials" in {

      Get(hashUri) ~> addCredentials(invalidCredentials) ~>
        Route.seal(routes) ~> check {
        status shouldEqual StatusCodes.Unauthorized
        responseAs[String] shouldEqual "The supplied authentication is invalid"
        header[`WWW-Authenticate`].get.challenges.head shouldEqual HttpChallenge("Basic", Some("hash system"), Map("charset" → "UTF-8"))
      }
    }

    s"return OK code with right error body for GET requests to $incorrectHashUri1 with valid credentials" in {

      Get(incorrectHashUri1) ~> addCredentials(validCredentials) ~>
        Route.seal(routes) ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual XmlHashResp(error = IncorrectMsisdn).body
      }
    }

    s"return OK code with right error body for GET requests to $incorrectHashUri2 with valid credentials" in {

      Get(incorrectHashUri2) ~> addCredentials(validCredentials) ~>
        Route.seal(routes) ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual XmlHashResp(error = IncorrectMsisdn).body
      }
    }

    s"return OK code with right body for GET requests to $hashUri with valid credentials" in {

      Get(hashUri) ~> addCredentials(validCredentials) ~>
        Route.seal(routes) ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual XmlHashResp(hashMD5).body
      }
    }
  }
}