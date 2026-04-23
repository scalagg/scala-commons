package gg.scala.commons.annotations

/**
 * @author Limo
 * @date 2/5/2026
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ServiceablePackage(val value: String)
