package gecko10000.geckodeathbans

import gecko10000.geckodeathbans.config.DeathBanSet
import gecko10000.geckodeathbans.di.MyKoinComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import org.koin.core.component.inject
import java.util.*

@OptIn(ExperimentalSerializationApi::class)
class DeathBanStorage : MyKoinComponent {

    private val plugin: GeckoDeathBans by inject()

    private val json = Json
    private val deathBanFile = plugin.dataFolder.resolve("death_bans.json")
    private val deathBans: DeathBanSet = run {
        if (deathBanFile.exists()) {
            json.decodeFromStream(deathBanFile.inputStream())
        } else {
            DeathBanSet()
        }
    }


    private fun save() {
        json.encodeToStream(deathBans, deathBanFile.outputStream())
    }

    // Store that this is not an official ban and the player can be unbanned.
    fun storeDeathBan(uuid: UUID) {
        deathBans.deathBannedPlayers += uuid
        save()
    }

    fun isDeathBan(uuid: UUID): Boolean {
        return deathBans.deathBannedPlayers.contains(uuid)
    }

    fun removeDeathBan(uuid: UUID) {
        deathBans.deathBannedPlayers -= uuid
        save()
    }

    fun getAllDeathBans(): Set<UUID> {
        return deathBans.deathBannedPlayers.toSet()
    }

}
