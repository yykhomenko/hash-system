package system.hash

import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterEach, FunSuite}

class AppTest extends FunSuite with BeforeAndAfterEach {

  private val app = App(ConfigFactory.load.getConfig("dev"))

//  private val appRoute = testRoute(app.routes)

  override def beforeEach() {

  }

  test("testRoutes") {

  }

}
