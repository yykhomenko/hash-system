package system.hash.model

import akka.http.scaladsl.model.{HttpCharsets, HttpEntity, MediaTypes}
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import system.hash.App.{complete, extractUri}

trait Responses {

  val applicationXml = MediaTypes.`application/xml`.toContentType(HttpCharsets.`UTF-8`)

  sealed abstract class Resp {
    def body: String
    def resp: Route = complete(HttpEntity(applicationXml, body))
  }

  case class JsonResp(value: String = "", error: Error) extends Resp {
    def body: String =
      if (error.errorId == 0)
        s"""{"value":"$value"}"""
      else
        s"""{"value":$value,"errorId":${error.errorId},"errorMsg":${error.errorMsg}}""""
  }

  case class XmlHashResp(value: String = "", error: Error) extends Resp {
    def body: String =
      s"""<result><hash>$value</hash><status errorCode="${error.errorId}">${error.errorMsg}</status></result>"""
  }

  case class XmlMsisdnResp(value: String = "", error: Error) extends Resp {
    def body: String =
      s"""<result><msisdn>$value</msisdn><status errorCode="${error.errorId}">${error.errorMsg}</status></result>"""
  }

  sealed abstract class Error(val errorId: Int, val errorMsg: String)
  case object Ok extends Error(0, "Successful")
  case object InternalError extends Error(1, "Internal error")
  case object DataNotFound extends Error(2, "Data not found")
  case object IncorrectMsisdn extends Error(6, "Incorrect MSISDN format")
  case object IncorrectHash extends Error(6, "Incorrect HASH format")

  def xmlExHandler = ExceptionHandler {
    case t: Throwable =>
      extractUri { uri =>
        println(s"Request to $uri could not be handled normally, cause: $t")
        t.printStackTrace()
        XmlMsisdnResp(error = InternalError).resp
      }
  }
}