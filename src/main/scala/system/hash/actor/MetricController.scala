package system.hash.actor

import java.io.StringWriter
import java.lang.management.ManagementFactory

import akka.actor.{Actor, Props}
import io.prometheus.client.exporter.common.TextFormat
import io.prometheus.client.hotspot.{GarbageCollectorExports, MemoryPoolsExports, StandardExports, ThreadExports}
import io.prometheus.client.{CollectorRegistry, Counter}
import system.hash.model.HashSysError

import scala.collection.JavaConverters._

object MetricController {

  sealed trait MetricMsg
  object IncXmlHashOk extends MetricMsg
  object IncXmlMsisdnOk extends MetricMsg
  object IncJsonHashOk extends MetricMsg
  object IncJsonMsisdnOk extends MetricMsg

  case class IncXmlError(error: HashSysError) extends MetricMsg
  case class IncXmlHashError(error: HashSysError) extends MetricMsg
  case class IncXmlMsisdnError(error: HashSysError) extends MetricMsg
  case class IncJsonHashError(error: HashSysError) extends MetricMsg
  case class IncJsonMsisdnError(error: HashSysError) extends MetricMsg

  object MetricsReq extends MetricMsg
  case class MetricsResp(metrics: String) extends MetricMsg

  def props = Props[MetricController]
}

class MetricController extends Actor {
  import MetricController._

  val group: String = "hash_system"
  var requestCounter: Counter = _
  var errorCounter: Counter = _

  override def preStart(): Unit = {

    CollectorRegistry.defaultRegistry.clear()
    new StandardExports().register()
    new MemoryPoolsExports(ManagementFactory.getMemoryMXBean, Nil.asJava).register()
    new GarbageCollectorExports().register()
    new ThreadExports().register()

    requestCounter = Counter.build.namespace(group).name("request_total").labelNames("name").help("request total").register
    errorCounter = Counter.build.namespace(group).name("errors_total").labelNames("name", "code").help("http error total").register
  }

  def metrics = {
    val metrics = new StringWriter
    TextFormat.write004(metrics, CollectorRegistry.defaultRegistry.metricFamilySamples)
    metrics.toString
  }

  override def receive = {

    case IncXmlHashOk     => requestCounter.labels("xml_hash_ok").inc()
    case IncXmlMsisdnOk   => requestCounter.labels("xml_msisdn_ok").inc()
    case IncJsonHashOk    => requestCounter.labels("json_hash_ok").inc()
    case IncJsonMsisdnOk  => requestCounter.labels("json_msisdn_ok").inc()

    case IncXmlError(error)     => errorCounter.labels("xml_error", error.errorId.toString)
    case IncXmlHashError(error)     => errorCounter.labels("xml_hash_error", error.errorId.toString)
    case IncXmlMsisdnError(error)  => errorCounter.labels("xml_msisdn_error", error.errorId.toString)
    case IncJsonHashError(error)  => errorCounter.labels("json_hash_error", error.errorId.toString)
    case IncJsonMsisdnError(error)  => errorCounter.labels("json_msisdn_error", error.errorId.toString)

    case MetricsReq       => sender ! MetricsResp(metrics)

    case m                => println(s"$getClass: unknown message: $m")
  }
}