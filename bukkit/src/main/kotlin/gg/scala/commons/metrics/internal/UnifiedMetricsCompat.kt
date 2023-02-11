package gg.scala.commons.metrics.internal

import com.destroystokyo.paper.event.server.ServerTickEndEvent
import dev.cubxity.plugins.metrics.api.UnifiedMetrics
import dev.cubxity.plugins.metrics.api.logging.Logger
import dev.cubxity.plugins.metrics.api.metric.collector.Collector
import dev.cubxity.plugins.metrics.api.metric.collector.CollectorCollection
import dev.cubxity.plugins.metrics.api.metric.collector.Counter
import dev.cubxity.plugins.metrics.api.metric.collector.Histogram
import dev.cubxity.plugins.metrics.api.metric.collector.MILLISECONDS_PER_SECOND
import dev.cubxity.plugins.metrics.api.metric.data.GaugeMetric
import dev.cubxity.plugins.metrics.api.metric.data.Metric
import dev.cubxity.plugins.metrics.api.metric.store.VolatileDoubleStore
import dev.cubxity.plugins.metrics.api.metric.store.VolatileLongStore
import dev.cubxity.plugins.metrics.api.platform.PlatformType
import dev.cubxity.plugins.metrics.api.util.fastForEach
import dev.cubxity.plugins.metrics.bukkit.BukkitDispatcher
import dev.cubxity.plugins.metrics.bukkit.metric.tick.TickReporter
import dev.cubxity.plugins.metrics.common.UnifiedMetricsBootstrap
import dev.cubxity.plugins.metrics.common.metric.Metrics
import dev.cubxity.plugins.metrics.core.plugin.CoreUnifiedMetricsPlugin
import kotlinx.coroutines.CoroutineDispatcher
import net.evilblock.cubed.ScalaCommonsSpigot
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.ServerListPingEvent
import org.bukkit.plugin.ServicePriority
import java.io.File
import java.nio.file.Path
import java.util.concurrent.Executors
import java.util.logging.Level

/**
 * @author GrowlyX
 * @since 2/11/2023
 */
val confDirectory: Path
    get() = File(Bukkit.getWorldContainer(), "plugins/UnifiedMetrics").toPath()

class ProxiedJavaLogger(
    private val plugin: ScalaCommonsSpigot
) : Logger
{
    override fun info(message: String)
    {
        plugin.logger.info("[metrics] $message")
    }

    override fun severe(message: String)
    {
        plugin.logger.severe("[metrics] $message")
    }

    override fun severe(message: String, error: Throwable)
    {
        plugin.logger.log(Level.SEVERE, "[metrics] $message", error)
    }

    override fun warn(message: String)
    {
        plugin.logger.warning("[metrics] $message")
    }

    override fun warn(message: String, error: Throwable)
    {
        plugin.logger.log(Level.WARNING, "[metrics] $message", error)
    }
}

class UnifiedMetricsBukkitBootstrap(
    val plugin: ScalaCommonsSpigot,
    override val configDirectory: Path = confDirectory,
    override val dataDirectory: Path = confDirectory,
    override val dispatcher: CoroutineDispatcher = BukkitDispatcher(plugin),
    override val logger: Logger = ProxiedJavaLogger(plugin),
    override val serverBrand: String = Bukkit.getName(),
    override val type: PlatformType = PlatformType.Bukkit,
    override val version: String = plugin.description.version
) : UnifiedMetricsBootstrap
{
    val metrics = UnifiedMetricsBukkitPlugin(this)
}

class BukkitWorldCollector(private val bootstrap: UnifiedMetricsBukkitBootstrap) :
    Collector
{
    override fun collect(): List<Metric> {
        val worlds = bootstrap.plugin.server.worlds
        val samples = ArrayList<Metric>(worlds.size * 3)

        worlds.fastForEach { world ->
            val tags = mapOf("world" to world.name)
            samples.add(GaugeMetric(Metrics.Server.WorldEntitiesCount, tags, world.entities.size))
            samples.add(GaugeMetric(Metrics.Server.WorldPlayersCount, tags, world.players.size))
            samples.add(GaugeMetric(Metrics.Server.WorldLoadedChunks, tags, world.loadedChunks.size))
        }

        return samples
    }
}

class WorldCollection(bootstrap: UnifiedMetricsBukkitBootstrap) :
    CollectorCollection
{
    private val collector = BukkitWorldCollector(bootstrap)
    override val collectors: List<Collector> = listOf(collector)
}

class PaperTickReporter(
    private val metric: TickCollection,
    private val bootstrap: UnifiedMetricsBukkitBootstrap
) : TickReporter, Listener
{
    override fun initialize() {
        bootstrap.plugin.server.pluginManager.registerEvents(this, bootstrap.plugin)
    }

    override fun dispose() {
        HandlerList.unregisterAll(this)
    }

    @EventHandler
    fun onTick(event: ServerTickEndEvent) {
        metric.onTick(event.tickDuration / MILLISECONDS_PER_SECOND)
    }
}

class TickCollection(bootstrap: UnifiedMetricsBukkitBootstrap) : CollectorCollection {
    private val reporter = PaperTickReporter(this, bootstrap)

    // The callback is called from a single thread
    private val tickDuration = Histogram(
        Metrics.Server.TickDurationSeconds,
        sumStoreFactory = VolatileDoubleStore,
        countStoreFactory = VolatileLongStore
    )

    override val collectors: List<Collector> = listOf(tickDuration)

    override val isAsync: Boolean
        get() = true

    override fun initialize() {
        reporter.initialize()
    }

    override fun dispose() {
        reporter.dispose()
    }

    /**
     * @param duration tick duration in seconds
     */
    fun onTick(duration: Double) {
        tickDuration += duration
    }
}

class EventsCollection(private val bootstrap: UnifiedMetricsBukkitBootstrap) : CollectorCollection, Listener {
    private val loginCounter = Counter(Metrics.Events.Login)
    private val joinCounter = Counter(Metrics.Events.Join, valueStoreFactory = VolatileDoubleStore)
    private val quitCounter = Counter(Metrics.Events.Quit, valueStoreFactory = VolatileDoubleStore)
    private val chatCounter = Counter(Metrics.Events.Chat)
    private val pingCounter = Counter(Metrics.Events.Ping) // TODO: is this async?

    override val collectors: List<Collector> =
        listOf(loginCounter, joinCounter, quitCounter, chatCounter, pingCounter)

    override val isAsync: Boolean
        get() = true

    override fun initialize() {
        bootstrap.plugin.server.pluginManager.registerEvents(this, bootstrap.plugin)
    }

    override fun dispose() {
        HandlerList.unregisterAll(this)
    }

    @EventHandler
    fun onLogin(event: AsyncPlayerPreLoginEvent) {
        loginCounter.inc()
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        joinCounter.inc()
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        quitCounter.inc()
    }

    @EventHandler
    fun onChat(event: AsyncPlayerChatEvent) {
        chatCounter.inc()
    }

    @EventHandler
    fun onPing(event: ServerListPingEvent) {
        pingCounter.inc()
    }
}

class ServerCollection(bootstrap: UnifiedMetricsBukkitBootstrap) : CollectorCollection {
    override val collectors: List<Collector> = listOf(ServerCollector(bootstrap))
}

class ServerCollector(private val bootstrap: UnifiedMetricsBukkitBootstrap) : Collector {
    override fun collect(): List<Metric> {
        val server = bootstrap.plugin.server
        return listOf(
            GaugeMetric(Metrics.Server.Plugins, value = server.pluginManager.plugins.size),
            GaugeMetric(Metrics.Server.PlayersCount, value = server.onlinePlayers.size),
            GaugeMetric(Metrics.Server.PlayersMax, value = server.maxPlayers)
        )
    }
}

class UnifiedMetricsBukkitPlugin(
    override val bootstrap: UnifiedMetricsBukkitBootstrap
) : CoreUnifiedMetricsPlugin() {
    private val executor = Executors.newScheduledThreadPool(1)

    override fun disable() {
        executor.shutdownNow()
        super.disable()
    }

    override fun registerPlatformService(api: UnifiedMetrics) {
        bootstrap.plugin.server.servicesManager.register(
            UnifiedMetrics::class.java, api, bootstrap.plugin, ServicePriority.Normal
        )

        bootstrap.plugin.flavor().bind<UnifiedMetrics>() to api
    }

    override fun registerPlatformMetrics() {
        super.registerPlatformMetrics()

        apiProvider.metricsManager.apply {
            with(config.metrics.collectors) {
                if (server) registerCollection(ServerCollection(bootstrap))
                if (world) registerCollection(WorldCollection(bootstrap))
                if (tick) registerCollection(TickCollection(bootstrap))
                if (events) registerCollection(EventsCollection(bootstrap))
            }
        }
    }
}
