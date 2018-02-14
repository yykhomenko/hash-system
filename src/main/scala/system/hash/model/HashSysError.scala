package system.hash.model

sealed abstract class HashSysError(val errorId: Int, val errorMsg: String)
case object Ok extends HashSysError(0, "Successful")
case object InternalHashSysError extends HashSysError(1, "Internal error")
case object DataNotFound extends HashSysError(2, "Data not found")
case object IncorrectMsisdn extends HashSysError(6, "Incorrect MSISDN format")
case object IncorrectHash extends HashSysError(6, "Incorrect HASH format")