package system.hash.xml

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.`WWW-Authenticate`
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}
import system.hash.Config
import system.hash.route.Routes

class XmlGetHashTest extends WordSpec with Matchers with ScalatestRouteTest with Routes with Config {

  val method = "/anonym/getHash"
  val uri = s"$method?msisdn=$msisdn"
  val shortMsisdnUri = s"$method?msisdn=$msisdnTooShort"
  val alphaNameMsisdnUri = s"$method?msisdn=$msisdnAlphaName"

  s"The hash system with XML method $method" should {

    s"1. Return Unauthorized error for GET requests to $uri without credentials" in {

      Get(uri) ~>
        Route.seal(routes) ~> check {

        status shouldEqual StatusCodes.Unauthorized
        responseAs[String] shouldEqual requiresAuth
        header[`WWW-Authenticate`].get.challenges.head shouldEqual responseWWWAuthHeader
      }
    }

    s"2. Return Unauthorized error for GET requests to $uri with empty credentials" in {

      Get(uri) ~>
        addCredentials(invalidCredentials) ~>
        Route.seal(routes) ~> check {

        status shouldEqual StatusCodes.Unauthorized
        responseAs[String] shouldEqual invalidAuth
        header[`WWW-Authenticate`].get.challenges.head shouldEqual responseWWWAuthHeader
      }
    }

    s"3. Return Unauthorized error for GET requests to $uri with invalid credentials" in {

      Get(uri) ~>
        addCredentials(invalidCredentials) ~>
        Route.seal(routes) ~> check {

        status shouldEqual StatusCodes.Unauthorized
        responseAs[String] shouldEqual invalidAuth
        header[`WWW-Authenticate`].get.challenges.head shouldEqual responseWWWAuthHeader
      }
    }

    s"4. Return OK code with right error body for GET requests to $shortMsisdnUri with valid credentials" in {

      Get(shortMsisdnUri) ~>
        addCredentials(validCredentials) ~>
        Route.seal(routes) ~> check {

        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual XmlHashResp(error = IncorrectMsisdn).body
      }
    }

    s"5. Return OK code with right error body for GET requests to $alphaNameMsisdnUri with valid credentials" in {

      Get(alphaNameMsisdnUri) ~>
        addCredentials(validCredentials) ~>
        Route.seal(routes) ~> check {

        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual XmlHashResp(error = IncorrectMsisdn).body
      }
    }

    s"6. Return OK code with right body for GET requests to $uri with valid credentials" in {

      Get(uri) ~>
        addCredentials(validCredentials) ~>
        Route.seal(routes) ~> check {

        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual XmlHashResp(hash).body
      }
    }
  }
}