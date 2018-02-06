package system

package object hash {

  def extractArgs(args: Array[String]): (String, String) = {
    val mode = if (args.length > 0) args(0) else "server"
    val fileName = if (args.length > 1) args(1) else "hashesUUID.bin"

    (mode, fileName)
  }

  def withTimer(comment: String, block: => Unit): Unit = {
    println(comment)
    val start = System.currentTimeMillis
    block
    println(s"completed in ${(System.currentTimeMillis - start) / 1000}s")
  }
}