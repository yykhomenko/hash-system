package system.hash.xml

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.`WWW-Authenticate`
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}
import system.hash.Config
import system.hash.model.{DataNotFound, IncorrectHash}
import system.hash.route.Routes

class XmlGetMsisdnTest extends WordSpec with Matchers with ScalatestRouteTest with Routes with Config {

  val method = "/anonym/getMsisdn"
  val uri = s"$method?hash=$hash"
  val incorrectHashUri = s"$method?hash=$incorrectHash"
  val absentHashUri = s"$method?hash=$absentHash"

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

    s"4. Return Forbidden error with right error body for GET requests to $uri with other role credentials" in {

      Get(uri) ~>
        addCredentials(validMetricCredentials) ~>
        Route.seal(routes) ~> check {

        status shouldEqual StatusCodes.Forbidden
        responseAs[String] shouldEqual invalidRole + ClientRole.role
      }
    }

    s"5. Return OK code with right error body for GET requests to $incorrectHashUri with valid credentials" in {

      Get(incorrectHashUri) ~>
        addCredentials(validClientCredentials) ~>
        Route.seal(routes) ~> check {

        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual XmlMsisdnResp(error = IncorrectHash).body
      }
    }

    s"6. Return OK code with right error body for GET requests to $absentHashUri with valid credentials" in {

      Get(absentHashUri) ~>
        addCredentials(validClientCredentials) ~>
        Route.seal(routes) ~> check {

        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual XmlMsisdnResp(error = DataNotFound).body
      }
    }

    s"7. Return OK code with right body for GET requests to $uri with valid credentials" in {

      Get(uri) ~>
        addCredentials(validClientCredentials) ~>
        Route.seal(routes) ~> check {

        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual XmlMsisdnResp(msisdn.toString).body
      }
    }
  }
}