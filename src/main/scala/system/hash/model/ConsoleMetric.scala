package system.hash.model

import java.util.concurrent.atomic.AtomicInteger

import com.typesafe.scalalogging.LazyLogging

trait ConsoleMetric extends LazyLogging {

  protected def progressSize: Int

  private val progressCounter = new AtomicInteger(0)
  private val progressSet = (1L to 100L) map (_ * progressSize / 100) toSet

  def inc(): Unit = {
    val current = progressCounter.incrementAndGet()
    if (progressSet(current)) printProgress(progressSize, current)
  }

  private def printProgress(available: Int, progress: Long): Unit = {
    val persent = Math.round(progress * 100 / available)
//    print(s"\r$persent%")
    logger.info(s"$persent%")
  }
  
  def withTimer(comment: String, block: => Unit): Unit = {
    logger.info(comment)
    val start = System.currentTimeMillis
    block
    logger.info(s"completed in ${(System.currentTimeMillis - start) / 1000}s")
  }
}
