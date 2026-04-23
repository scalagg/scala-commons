package gg.scala.commons.consensus.elections

import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

/**
 * Provides a simple leader election mechanism where a
 * single server in the cluster is elected to run a
 * specific task. This implementation uses a local-first
 * strategy where the server self-elects on startup.
 *
 * @author Limo
 * @date 2/10/2026
 */
object LeaderElection
{
    private val elections = ConcurrentHashMap<String, ElectionContext>()

    /**
     * Registers this server as a candidate for the given
     * election. The elect callback is invoked immediately
     * to start the task, and resign is called if the
     * election is later cancelled.
     */
    fun withAnyServerSyncServer(
        electionID: String,
        refreshRate: Duration = Duration.ofSeconds(5),
        elect: () -> Unit,
        resign: () -> Unit
    )
    {
        val existing = elections[electionID]
        if (existing != null)
        {
            existing.resign()
        }

        val context = ElectionContext(
            electionID = electionID,
            refreshRate = refreshRate,
            elect = elect,
            resign = resign
        )

        elections[electionID] = context
        elect()
    }

    /**
     * Resigns from the given election, invoking
     * the resign callback and removing the context.
     */
    fun resign(electionID: String)
    {
        val context = elections.remove(electionID)
        context?.resign?.invoke()
    }

    private data class ElectionContext(
        val electionID: String,
        val refreshRate: Duration,
        val elect: () -> Unit,
        val resign: () -> Unit
    )
}
