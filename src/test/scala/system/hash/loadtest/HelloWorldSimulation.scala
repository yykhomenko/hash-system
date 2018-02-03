package system.hash.loadtest

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import com.typesafe.config._

/**
  * @author jittagornp
  * create 23/11/2015
  */
class HelloWorldSimulation extends Simulation {

  // 1. โหลด config
//  val conf = ConfigFactory.load();


  val baseUrl = "http://127.0.0.1:8080"

  val httpConf = http
    .baseURL(baseUrl)
    .acceptHeader("application/xml")

  val scn = scenario("get hash")
    .exec(http("get hash").get("/anonym/getHash").queryParam("msisdn", "380672244089"))

//  setUp(scn.inject(atOnceUsers(1)).protocols(httpConf))


  //setUp(scn.inject(rampUsers(10) over(20 second))).maxDuration(10 minutes).protocols(httpConf)


//  setUp(scn.inject(constantUsersPerSec(300) during(1 minutes))).throttle(
////    reachRps(1000) in (5 seconds),
////    holdFor(20 second),
////    jumpToRps(2000),
////    holdFor(10 second)
////  ).protocols(httpConf)
//
//
////  setUp(
////    scn.inject(rampUsers(20000) over (20 seconds))
////  )
////    .protocols(httpConf)
//
//  setUp(scn.inject(constantRate(10000 usersPerSec) during (1 minute)))
}