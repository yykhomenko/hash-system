package system.hash.json

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.`WWW-Authenticate`
import akka.http.scaladsl.server.Route
import system.hash.Config
import system.hash.model.IncorrectHash

class JsonMsisdnTest extends Config {

  val resource = "/api/msisdn"
  val uri = s"$resource/$hash"
  val incorrectHashUri = s"$resource/$incorrectHash"
  val absentHashUri = s"$resource/$absentHash"

  s"The hash system with REST resource $resource" should {

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

    s"5. Return BadRequest error with right error body for GET requests to $incorrectHashUri with valid credentials" in {

      Get(incorrectHashUri) ~>
        addCredentials(validClientCredentials) ~>
        Route.seal(routes) ~> check {

        status shouldEqual StatusCodes.BadRequest
        responseAs[String] shouldEqual JsonResp(error = IncorrectHash).body
      }
    }

    s"6. Return NotFound error with right error body for GET requests to $absentHashUri with valid credentials" in {

      Get(absentHashUri) ~>
        addCredentials(validClientCredentials) ~>
        Route.seal(routes) ~> check {

        status shouldEqual StatusCodes.NotFound
        responseAs[String] shouldEqual ""
      }
    }

    s"7. Return OK code with right body for GET requests to $uri with valid credentials" in {

      Get(uri) ~>
        addCredentials(validClientCredentials) ~>
        Route.seal(routes) ~> check {

        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual JsonResp(msisdn.toString).body
      }
    }
  }
}