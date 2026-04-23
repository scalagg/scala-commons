package gg.scala.commons.persist

import gg.scala.store.storage.storable.IDataStoreObject

private val readOnlyProfiles = mutableSetOf<ProfileOrchestrator<*>>()

/**
 * Extension to enable read-only profile mode.
 * In this mode, profiles are not automatically saved on logout.
 *
 * @author Limo
 * @date 2/5/2026
 */
fun <T : IDataStoreObject> ProfileOrchestrator<T>.enableReadOnlyProfile()
{
    readOnlyProfiles.add(this)
}

/**
 * Check if this orchestrator is in read-only mode.
 *
 * @author Limo
 * @date 2/5/2026
 */
fun <T : IDataStoreObject> ProfileOrchestrator<T>.isReadOnlyProfile(): Boolean
{
    return this in readOnlyProfiles
}
