package system.hash

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

class CommonRouteTest extends Config {

  "The hash system with http protocol" should {

    "leave GET requests to other paths unhandled" in {

      Get("/undefined") ~> routes ~> check {
        handled shouldBe false
      }
    }

    "return MethodNotAllowed error for POST requests to the root path" in {

      Post() ~> Route.seal(routes) ~> check {
        status shouldEqual StatusCodes.MethodNotAllowed
        responseAs[String] shouldEqual "HTTP method not allowed, supported methods: GET"
      }
    }

    "return MethodNotAllowed error for PUT requests to the root path" in {

      Put() ~> Route.seal(routes) ~> check {
        status shouldEqual StatusCodes.MethodNotAllowed
        responseAs[String] shouldEqual "HTTP method not allowed, supported methods: GET"
      }
    }
  }
}