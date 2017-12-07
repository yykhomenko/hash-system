package model

import java.util.UUID

trait Model {

  sealed abstract class Error(val errorId: Int, val errorMsg: String) // todo add error codes
  case object OK extends Error(0, "Successfull")

  case class Response(value: String, error: Error) {
    def toJson: String =
      if (error.errorId == 0) s"""{"value":"$value"}"""
      else s"""{"value":$value,"errorId":${error.errorId},"errorMsg":${error.errorMsg}}""""
  }

  case class XmlHashResponse(hash: UUID, error: Error) {
    def toXml: String = s"""<result><hash>$hash</hash><status errorCode="${error.errorId}">${error.errorMsg}</status></result>"""
  }

  case class XmlMsisdnResponse(msisdn: Long, error: Error) {
    def toXml: String = s"""<result><msisdn>$msisdn</msisdn><status errorCode="${error.errorId}">${error.errorMsg}</status></result>"""
  }
}