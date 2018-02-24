package system.hash.json

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.`WWW-Authenticate`
import akka.http.scaladsl.server.Route
import system.hash.Config
import system.hash.model.IncorrectMsisdn

class JsonHashTest extends Config {

  val resource = "/api/hash"
  val uri = s"$resource/$msisdn"
  val shortMsisdnUri = s"$resource/$msisdnTooShort"
  val alphaNameMsisdnUri = s"$resource/$msisdnAlphaName"

  s"The hash system with REST resource $resource" should {

    s"1. Return Unauthorized error for GET requests to $uri without credentials" in {

      Get(uri) ~>
        Route.seal(routes) ~> check {

        status shouldEqual Unauthorized
        responseAs[String] shouldEqual requiresAuth
        header[`WWW-Authenticate`].get.challenges.head shouldEqual responseWWWAuthHeader
      }
    }

    s"2. Return Unauthorized error for GET requests to $uri with empty credentials" in {

      Get(uri) ~>
        addCredentials(invalidCredentials) ~>
        Route.seal(routes) ~> check {

        status shouldEqual Unauthorized
        responseAs[String] shouldEqual invalidAuth
        header[`WWW-Authenticate`].get.challenges.head shouldEqual responseWWWAuthHeader
      }
    }

    s"3. Return Unauthorized error for GET requests to $uri with invalid credentials" in {

      Get(uri) ~>
        addCredentials(invalidCredentials) ~>
        Route.seal(routes) ~> check {

        status shouldEqual Unauthorized
        responseAs[String] shouldEqual invalidAuth
        header[`WWW-Authenticate`].get.challenges.head shouldEqual responseWWWAuthHeader
      }
    }

    s"4. Return Forbidden error with right error body for GET requests to $uri with other role credentials" in {

      Get(uri) ~>
        addCredentials(validMetricCredentials) ~>
        Route.seal(routes) ~> check {

        status shouldEqual Forbidden
        responseAs[String] shouldEqual invalidRole + ClientRole.role
      }
    }

    s"5. Return BadRequest error with right error body for GET requests to $shortMsisdnUri with valid credentials" in {

      Get(shortMsisdnUri) ~> addCredentials(validClientCredentials) ~>
        Route.seal(routes) ~> check {

        status shouldEqual BadRequest
        responseAs[String] shouldEqual JsonResp(error = IncorrectMsisdn).body
      }
    }

    s"6. Return BadRequest error with right error body for GET requests to $alphaNameMsisdnUri with valid credentials" in {

      Get(alphaNameMsisdnUri) ~>
        addCredentials(validClientCredentials) ~>
        Route.seal(routes) ~> check {

        status shouldEqual BadRequest
        responseAs[String] shouldEqual JsonResp(error = IncorrectMsisdn).body
      }
    }

    s"7. Return OK code with right body for GET requests to $uri with valid credentials" in {

      Get(uri) ~>
        addCredentials(validClientCredentials) ~>
        Route.seal(routes) ~> check {

        status shouldEqual OK
        responseAs[String] shouldEqual JsonResp(hash).body
      }
    }
  }
}