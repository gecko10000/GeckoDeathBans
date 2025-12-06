package gecko10000.geckodeathbans.di

import gecko10000.geckodeathbans.BanManager
import gecko10000.geckodeathbans.BanStepTracker
import gecko10000.geckodeathbans.GeckoDeathBans
import gecko10000.geckodeathbans.WorldDeathBanStorage
import org.koin.dsl.module

fun pluginModules(plugin: GeckoDeathBans) = module {
    single { plugin }
    single(createdAtStart = true) { BanStepTracker() }
    single(createdAtStart = true) { BanManager() }
    single(createdAtStart = true) { WorldDeathBanStorage() }
}
