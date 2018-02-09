package system.hash

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.{BasicHttpCredentials, HttpChallenge, _}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}
import system.hash.route.UserRoutes

class AppTest extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest with UserRoutes {

  "The hash system" should {

    "leave GET requests to other paths unhandled" in {

      Get("/undefined") ~> userRoutes ~> check {
        handled shouldBe false
      }
    }

    "return a MethodNotAllowed error for PUT requests to the root path" in {

      Put() ~> Route.seal(userRoutes) ~> check {
        status shouldEqual StatusCodes.MethodNotAllowed
        responseAs[String] shouldEqual "HTTP method not allowed, supported methods: GET"
      }
    }

    "return a Unauthorized response for GET requests to /anonym/getHash without credentials" in {

      Get("/anonym/getHash?msisdn=380672240000") ~> Route.seal(userRoutes) ~> check {
        status shouldEqual StatusCodes.Unauthorized
        responseAs[String] shouldEqual "The resource requires authentication, which was not supplied with the request"
        header[`WWW-Authenticate`].get.challenges.head shouldEqual HttpChallenge("Basic", Some("hash system"), Map("charset" → "UTF-8"))
      }
    }

    "return a Unauthorized response for GET requests to /anonym/getHash without invalid credentials" in {

      val invalidCredentials = BasicHttpCredentials("Peter", "pan")
      Get("/anonym/getHash?msisdn=380672240000") ~> addCredentials(invalidCredentials) ~>
        Route.seal(userRoutes) ~> check {
        status shouldEqual StatusCodes.Unauthorized
        responseAs[String] shouldEqual "The supplied authentication is invalid"
        header[`WWW-Authenticate`].get.challenges.head shouldEqual HttpChallenge("Basic", Some("hash system"), Map("charset" → "UTF-8"))
      }
    }

    "return a OK response for GET requests to /anonym/getHash with valid credentials" in {

      val validCredentials = BasicHttpCredentials("test-client", "test-client-password")
      Get("/anonym/getHash?msisdn=380672240000") ~> addCredentials(validCredentials) ~>
        Route.seal(userRoutes) ~> check {
        responseAs[String] shouldEqual "<result><hash>8801ddf0a8ef82313293d7cf3ab5d46c</hash><status errorCode=\"0\">Successful</status></result>"
      }
    }
  }
}