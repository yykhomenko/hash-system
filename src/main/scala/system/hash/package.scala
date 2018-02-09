package system

package object hash {

  def withTimer(comment: String, block: => Unit): Unit = {
    println(comment)
    val start = System.currentTimeMillis
    block
    println(s"completed in ${(System.currentTimeMillis - start) / 1000}s")
  }
}