package system.hash.route

import akka.http.scaladsl.server.Route

trait Routes extends XmlRoutes with JsonRoutes with MetricRoutes {
  def routes: Route = xmlRoutes ~ jsonRoutes ~ metricRoutes
}