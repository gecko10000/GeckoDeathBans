package gecko10000.geckodeathbans.di

import gecko10000.geckodeathbans.*
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.milkbowl.vault.economy.Economy
import org.koin.dsl.module

fun pluginModules(plugin: GeckoDeathBans) = module {
    single { plugin }
    single(createdAtStart = true) { BanStepTracker() }
    single(createdAtStart = true) { BanManager() }
    single(createdAtStart = true) { DeathBanStorage() }
    single(createdAtStart = true) { RespawnTotemManager() }
    single(createdAtStart = true) { CombatLogManager() }
    single { PlainTextComponentSerializer.plainText() }
    single(createdAtStart = true) { KDCountManager() }
    single<Economy> {
        plugin.server.servicesManager.getRegistration(Economy::class.java)!!.provider
    }
}
