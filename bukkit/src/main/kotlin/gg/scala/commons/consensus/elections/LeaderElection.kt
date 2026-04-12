package gg.scala.commons.consensus.elections

import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Limo
 * @date 2/10/2026
 */
object LeaderElection
{
    private val elections = ConcurrentHashMap<String, ElectionContext>()

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
