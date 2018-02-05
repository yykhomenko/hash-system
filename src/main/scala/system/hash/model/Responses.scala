package system.hash.model

trait Responses {

  sealed abstract class Error(val errorId: Int, val errorMsg: String)

  sealed abstract class Response(value: String, error: Error)

  case class JsonResponse(value: String, error: Error)
    extends Response(value, error) {
    override def toString: String =
      if (error.errorId == 0)
        s"""{"value":"$value"}"""
      else
        s"""{"value":$value,"errorId":${error.errorId},"errorMsg":${error.errorMsg}}""""
  }

  case class XmlHashResponse(value: String, error: Error) extends Response(value, error) {
    override def toString: String =
      s"""<result><hash>$value</hash><status errorCode="${error.errorId}">${error.errorMsg}</status></result>"""
  }

  case class XmlMsisdnResponse(value: String, error: Error) extends Response(value, error) {
    override def toString: String =
      s"""<result><msisdn>$value</msisdn><status errorCode="${error.errorId}">${error.errorMsg}</status></result>"""
  }

  case object Ok extends Error(0, "Successful")

  case object DataNotFound extends Error(2, "Data not found")

  case object IncorrectMsisdn extends Error(6, "Incorrect MSISDN format")

}
