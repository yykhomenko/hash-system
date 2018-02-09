package system.hash

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.{BasicHttpCredentials, HttpChallenge, _}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}
import system.hash.model.MD5
import system.hash.route.UserRoutes

class AppTest extends WordSpec with Matchers with ScalatestRouteTest with UserRoutes {

  val invalidCredentials = BasicHttpCredentials("Peter", "pan")
  val validCredentials = BasicHttpCredentials("test-client", "test-client-password")

  val msisdn = 380672240000L
  val hash = "8801ddf0a8ef82313293d7cf3ab5d46c"

  msisdns(MD5(hash)) = msisdn

  "The hash system" should {

    "leave GET requests to other paths unhandled" in {

      Get("/undefined") ~> userRoutes ~> check {
        handled shouldBe false
      }
    }

    "return MethodNotAllowed error for POST requests to the root path" in {

      Post() ~> Route.seal(userRoutes) ~> check {
        status shouldEqual StatusCodes.MethodNotAllowed
        responseAs[String] shouldEqual "HTTP method not allowed, supported methods: GET"
      }
    }

    "return MethodNotAllowed error for PUT requests to the root path" in {

      Put() ~> Route.seal(userRoutes) ~> check {
        status shouldEqual StatusCodes.MethodNotAllowed
        responseAs[String] shouldEqual "HTTP method not allowed, supported methods: GET"
      }
    }

    s"return Unauthorized error for GET requests to /anonym/getHash?msisdn=$msisdn without credentials" in {

      Get(s"/anonym/getHash?msisdn=$msisdn") ~> Route.seal(userRoutes) ~> check {
        status shouldEqual StatusCodes.Unauthorized
        responseAs[String] shouldEqual "The resource requires authentication, which was not supplied with the request"
        header[`WWW-Authenticate`].get.challenges.head shouldEqual HttpChallenge("Basic", Some("hash system"), Map("charset" → "UTF-8"))
      }
    }

    s"return Unauthorized error for GET requests to /anonym/getHash?msisdn=$msisdn without invalid credentials" in {

      Get(s"/anonym/getHash?msisdn=$msisdn") ~> addCredentials(invalidCredentials) ~>
        Route.seal(userRoutes) ~> check {
        status shouldEqual StatusCodes.Unauthorized
        responseAs[String] shouldEqual "The supplied authentication is invalid"
        header[`WWW-Authenticate`].get.challenges.head shouldEqual HttpChallenge("Basic", Some("hash system"), Map("charset" → "UTF-8"))
      }
    }

    s"return OK code with right body for GET requests to /anonym/getHash?msisdn=$msisdn with valid credentials" in {

      Get(s"/anonym/getHash?msisdn=$msisdn") ~> addCredentials(validCredentials) ~>
        Route.seal(userRoutes) ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual XmlHashResp(hash).body
      }
    }

    s"return OK code with right body for GET requests to /anonym/getMsisdn?hash=$hash with valid credentials" in {

      Get(s"/anonym/getMsisdn?hash=$hash") ~> addCredentials(validCredentials) ~>
        Route.seal(userRoutes) ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual XmlMsisdnResp(msisdn.toString).body
      }
    }
  }
}