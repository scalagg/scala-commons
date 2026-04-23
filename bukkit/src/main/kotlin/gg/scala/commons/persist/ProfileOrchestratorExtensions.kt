package gg.scala.commons.persist

import gg.scala.store.storage.storable.IDataStoreObject

private val readOnlyProfiles = mutableSetOf<ProfileOrchestrator<*>>()

/**
 * @author Limo
 * @date 2/5/2026
 */
fun <T : IDataStoreObject> ProfileOrchestrator<T>.enableReadOnlyProfile()
{
    readOnlyProfiles.add(this)
}

/**
 * @author Limo
 * @date 2/5/2026
 */
fun <T : IDataStoreObject> ProfileOrchestrator<T>.isReadOnlyProfile(): Boolean
{
    return this in readOnlyProfiles
}
