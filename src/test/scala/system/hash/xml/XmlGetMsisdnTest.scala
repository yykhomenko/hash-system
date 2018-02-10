package system.hash.xml

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.{HttpChallenge, `WWW-Authenticate`}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}
import system.hash.Config
import system.hash.route.Routes

class XmlGetMsisdnTest extends WordSpec with Matchers with ScalatestRouteTest with Routes with Config {

  val msisdnUri = s"/anonym/getMsisdn?hash=$hashMD5"
  val incorrectMsisdnUri1 = s"/anonym/getMsisdn?hash=$incorrectHashMD5"
  val incorrectMsisdnUri2 = s"/anonym/getMsisdn?hash=$absentHashMD5"

  "The hash system with XML protocol getMsisdn method" should {

    s"return Unauthorized error for GET requests to $msisdnUri without credentials" in {

      Get(msisdnUri) ~> Route.seal(routes) ~> check {
        status shouldEqual StatusCodes.Unauthorized
        responseAs[String] shouldEqual "The resource requires authentication, which was not supplied with the request"
        header[`WWW-Authenticate`].get.challenges.head shouldEqual HttpChallenge("Basic", Some("hash system"), Map("charset" → "UTF-8"))
      }
    }

    s"return Unauthorized error for GET requests to $msisdnUri without invalid credentials" in {

      Get(msisdnUri) ~> addCredentials(invalidCredentials) ~>
        Route.seal(routes) ~> check {
        status shouldEqual StatusCodes.Unauthorized
        responseAs[String] shouldEqual "The supplied authentication is invalid"
        header[`WWW-Authenticate`].get.challenges.head shouldEqual HttpChallenge("Basic", Some("hash system"), Map("charset" → "UTF-8"))
      }
    }

    s"return OK code with right error body for GET requests to $incorrectMsisdnUri1 with valid credentials" in {

      Get(incorrectMsisdnUri1) ~> addCredentials(validCredentials) ~>
        Route.seal(routes) ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual XmlMsisdnResp(error = IncorrectHash).body
      }
    }

    s"return OK code with right error body for GET requests to $incorrectMsisdnUri2 with valid credentials" in {

      Get(incorrectMsisdnUri2) ~> addCredentials(validCredentials) ~>
        Route.seal(routes) ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual XmlMsisdnResp(error = DataNotFound).body
      }
    }

    s"return OK code with right body for GET requests to $msisdnUri with valid credentials" in {

      Get(msisdnUri) ~> addCredentials(validCredentials) ~>
        Route.seal(routes) ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual XmlMsisdnResp(msisdn.toString).body
      }
    }
  }
}