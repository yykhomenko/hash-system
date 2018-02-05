package system.hash.model

import java.util.concurrent.atomic.AtomicInteger

trait Progress {

  private val progressCounter = new AtomicInteger(0)
  private val progressSet = (1L to 100L) map (_ * progressSize / 100) toSet

  def inc(): Unit = {
    val current = progressCounter.incrementAndGet()
    if (progressSet(current)) printProgress(progressSize, current)
  }

  private def printProgress(available: Int, progress: Long): Unit = {
    val persent = Math.round(progress * 100 / available)
    print(s"\r$persent% ")
  }

  protected def progressSize: Int
}
