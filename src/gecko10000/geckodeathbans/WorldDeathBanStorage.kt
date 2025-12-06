package gecko10000.geckodeathbans

import gecko10000.geckodeathbans.di.MyKoinComponent
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataType
import org.koin.core.component.inject
import java.util.*

class WorldDeathBanStorage : MyKoinComponent {

    private val plugin: GeckoDeathBans by inject()

    private val database // lol
        get() = plugin.server.worlds.first().persistentDataContainer

    private fun keyForPlayer(uuid: UUID) = NamespacedKey(plugin, uuid.toString())

    // Store that this is not an official ban and the player can be unbanned.
    fun storeDeathBan(uuid: UUID) {
        database.set(keyForPlayer(uuid), PersistentDataType.BOOLEAN, true)
    }

    fun isDeathBan(uuid: UUID): Boolean {
        return database.getOrDefault(keyForPlayer(uuid), PersistentDataType.BOOLEAN, false)
    }

    fun removeDeathBan(uuid: UUID) {
        database.remove(keyForPlayer(uuid))
    }

}
