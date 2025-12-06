package gecko10000.geckodeathbans.di

import gecko10000.geckodeathbans.GeckoDeathBans
import org.koin.core.Koin
import org.koin.dsl.koinApplication

object MyKoinContext {
    internal lateinit var koin: Koin
    fun init(plugin: GeckoDeathBans) {
        koin = koinApplication(createEagerInstances = false) {
            modules(pluginModules(plugin))
        }.koin
        koin.createEagerInstances()
    }
}
