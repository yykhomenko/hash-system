package system.hash.actor

import java.io.{IOException, StringWriter}
import java.lang.management.ManagementFactory

import akka.actor.{Actor, Props}
import io.prometheus.client.exporter.common.TextFormat
import io.prometheus.client.hotspot.{GarbageCollectorExports, MemoryPoolsExports, StandardExports, ThreadExports}
import io.prometheus.client.{CollectorRegistry, Counter}

import scala.collection.JavaConverters._

object MetricController {

  sealed trait MetricMsg
  object IncXmlHashOk extends MetricMsg
  object IncXmlMsisdnOk extends MetricMsg
  object IncJsonHashOk extends MetricMsg
  object IncJsonMsisdnOk extends MetricMsg
  object MetricsReq extends MetricMsg
  case class MetricsResp(metrics: String) extends MetricMsg

  def props = Props[MetricController]
}

class MetricController extends Actor {
  import MetricController._

  val group: String = "hash_system"
  var requestCounter: Counter = _

  override def preStart(): Unit = {

    CollectorRegistry.defaultRegistry.clear()
    new StandardExports().register()
    new MemoryPoolsExports(ManagementFactory.getMemoryMXBean, Nil.asJava).register()
    new GarbageCollectorExports().register()
    new ThreadExports().register()

    requestCounter = Counter.build.namespace(group).name("request_total").labelNames("name").help("request total").register
  }

  @throws[IOException]
  def metrics = {
    val metrics = new StringWriter
    TextFormat.write004(metrics, CollectorRegistry.defaultRegistry.metricFamilySamples)
    metrics.toString
  }

  override def receive = {

    case IncXmlHashOk =>
      requestCounter.labels("xml_hash_ok").inc()
    //  println(s"metric xml_hash_ok was incremented!!!")
    case IncXmlMsisdnOk =>
      requestCounter.labels("xml_msisdn_ok").inc()
      println(s"metric xml_msisdn_ok was incremented!!!")

    case IncJsonHashOk =>
      requestCounter.labels("json_hash_ok").inc()
      println(s"metric json_hash_ok was incremented!!!")
    case IncJsonMsisdnOk =>
      requestCounter.labels("json_msisdn_ok").inc()
      println(s"metric json_msisdn_ok was incremented!!!")

    case MetricsReq =>
      //sender ! MetricsResp(metrics)

      println(metrics.split("\n").toList.filter(_.startsWith("hash_system_request_total")).headOption.getOrElse("."))

    case m =>
      println(s"$getClass: unknown message: $m")
  }
}