package gg.scala.banana

import gg.scala.banana.credentials.BananaCredentials
import gg.scala.banana.helper.BananaHelper
import gg.scala.banana.options.BananaOptions

/**
 * @author GrowlyX
 * @since 9/19/2021
 */
class BananaBuilder {

    private var options: BananaOptions = BananaOptions("nothing")
    private var credentials: BananaCredentials = BananaCredentials()

    fun options(options: BananaOptions): BananaBuilder {
        this.options = options
        return this
    }

    fun credentials(credentials: BananaCredentials): BananaBuilder {
        this.credentials = credentials
        return this
    }

    fun build(): Banana {
        val banana = Banana(
            credentials, options
        )
        BananaHelper.banana = banana

        return banana
    }
}
