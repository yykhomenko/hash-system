package system.hash.model

import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{HttpCharsets, HttpEntity, HttpResponse}
import akka.http.scaladsl.server.{Directives, Route}

trait Responses extends Directives {

  sealed abstract class Resp {
    def resp: Route
  }

  case class JsonResp(value: String = "", error: Error = Ok) extends Resp {

    private def bodyOk = s"""{"value":"$value"}"""
    private def bodyErr = s"""{"errorId":${error.errorId},"errorMsg":"${error.errorMsg}"}"""

    override def resp: Route = error match {
      case Ok => complete(HttpEntity(`application/json`, bodyOk))
      case InternalError => complete(HttpResponse(InternalServerError))
      case DataNotFound => complete(HttpResponse(NotFound))
      case IncorrectMsisdn => complete(HttpResponse(BadRequest, entity = HttpEntity(`application/json`, bodyErr)))
      case IncorrectHash => complete(HttpResponse(BadRequest, entity = HttpEntity(`application/json`, bodyErr)))
    }
  }

  private val applicationXml = `application/xml`.toContentType(HttpCharsets.`UTF-8`)
  case class XmlHashResp(value: String = "", error: Error = Ok) extends Resp {
    def body: String =
      s"""<result><hash>$value</hash><status errorCode="${error.errorId}">${error.errorMsg}</status></result>"""
    override def resp: Route = complete(HttpEntity(applicationXml, body))
  }

  case class XmlMsisdnResp(value: String = "", error: Error = Ok) extends Resp {
    def body: String =
      s"""<result><msisdn>$value</msisdn><status errorCode="${error.errorId}">${error.errorMsg}</status></result>"""
    override def resp: Route = complete(HttpEntity(applicationXml, body))
  }

  sealed abstract class Error(val errorId: Int, val errorMsg: String)
  case object Ok extends Error(0, "Successful")
  case object InternalError extends Error(1, "Internal error")
  case object DataNotFound extends Error(2, "Data not found")
  case object IncorrectMsisdn extends Error(6, "Incorrect MSISDN format")
  case object IncorrectHash extends Error(6, "Incorrect HASH format")
}