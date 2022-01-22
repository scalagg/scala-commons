package gg.scala.cloudsync.wrapper

/**
 * @author GrowlyX
 * @since 1/21/2022
 */
interface RepositoryWrapper<T>
{
    val items: MutableList<T>
}
