package net.evilblock.cubed.metrics

import dev.cubxity.plugins.metrics.api.UnifiedMetricsProvider
import dev.cubxity.plugins.metrics.api.metric.collector.CollectorCollection
import gg.scala.commons.annotations.custom.CustomAnnotationProcessors
import gg.scala.commons.annotations.plugin.SoftDependency
import gg.scala.commons.metrics.Metrics
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import net.evilblock.cubed.ScalaCommonsSpigot

@Service
@IgnoreAutoScan
@SoftDependency("UnifiedMetrics")
object UnifiedMetricsFeature
{
    @Inject
    lateinit var plugin: ScalaCommonsSpigot

    @Configure
    fun configure()
    {
        val provider = UnifiedMetricsProvider.get()
        plugin.logger.info(
            "[Metrics] Using UnifiedMetrics to export Prometheus-compatible metrics."
        )

        CustomAnnotationProcessors.process<Metrics> {
            if (it is CollectorCollection)
            {
                provider.metricsManager.registerCollection(provider as CollectorCollection)
                plugin.logger.info("[Metrics] Registered metrics collection ${it.javaClass.name}.")
            }
        }
    }
}
