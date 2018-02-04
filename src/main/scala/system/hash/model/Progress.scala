package system.hash.model

import java.util.concurrent.atomic.AtomicInteger

import system.hash.helper.CommLineHelper.printProgress

trait Progress {

  def progressSize: Int

  private val progressCounter = new AtomicInteger(0)
  private val progressSet = (1L to 100L) map (_ * progressSize / 100) toSet

  def inc(): Unit = {
    val current = progressCounter.incrementAndGet()
    if (progressSet(current)) printProgress(progressSize, current)
  }
}
