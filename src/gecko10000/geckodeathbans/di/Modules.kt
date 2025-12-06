package gecko10000.geckodeathbans.di

import gecko10000.geckodeathbans.*
import org.koin.dsl.module

fun pluginModules(plugin: GeckoDeathBans) = module {
    single { plugin }
    single(createdAtStart = true) { BanStepTracker() }
    single(createdAtStart = true) { BanManager() }
    single(createdAtStart = true) { WorldDeathBanStorage() }
    single(createdAtStart = true) { RespawnTotemManager() }
}
