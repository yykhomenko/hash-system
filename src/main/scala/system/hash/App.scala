package system.hash

import akka.http.scaladsl.server.{HttpApp, Route}
import com.typesafe.config.{Config, ConfigFactory}
import system.hash.auth.BasicAuthIp
import system.hash.model.{Responses, Validation}
import system.hash.model.dao.User
import system.hash.repo.{HashRepo, DbRepo}

object App extends HttpApp with BasicAuthIp with Validation with Responses {

  // todo add salt, algorithm, ip db, etc. to config
  // todo add json support
  // todo add logging
  // todo add tests

  override def routes: Route =

    get {

      handleExceptions(xmlExHandler) {

        path("anonym" / "getMsisdn") {
          withBasicAuthIp {
            parameters('hash) { hash =>

              withHashValidation(hash) {

                case false => XmlMsisdnResp(error = IncorrectHash).resp
                case true =>
                  HashRepo.getMsisdn(hash) match {
                    case None => XmlMsisdnResp(error = DataNotFound).resp
                    case Some(m) => XmlMsisdnResp(m.toString, Ok).resp
                  }
              }
            }
          }

        } ~
          path("anonym" / "getHash") {
            withBasicAuthIp {
              parameters('msisdn) { msisdn =>

                withMsisdnValidation(msisdn) {
                  case false => XmlHashResp(error = IncorrectMsisdn).resp
                  case true => XmlHashResp(HashRepo.getHash(msisdn), Ok).resp
                }
              }
            }
          }
      }
    }

  override def config: Config = ConfigFactory.parseMap(DbRepo.configs)
  override def users: Map[String, User] = DbRepo.users

  def main(args: Array[String]): Unit = {

    val (mode, fileName) = extractArgs(args)

    mode match {

      case "server" =>
       // withTimer("start load hashes", HashRepo.loadHashes())
        startServer("0.0.0.0", 8080)
    }
  }
}