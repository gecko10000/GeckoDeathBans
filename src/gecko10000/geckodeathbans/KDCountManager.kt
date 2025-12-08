package gecko10000.geckodeathbans

import com.Zrips.CMI.events.CMIPvPEndEventAsync
import com.Zrips.CMI.events.CMIPvPStartEventAsync
import gecko10000.geckodeathbans.di.MyKoinComponent
import gecko10000.geckolib.misc.Task
import net.milkbowl.vault.economy.Economy
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType
import org.koin.core.component.inject
import java.util.*

class KDCountManager : Listener, MyKoinComponent {

    private companion object {
        const val KILLER_STORE_TICKS = 100
    }

    private val plugin: GeckoDeathBans by inject()
    private val economy: Economy by inject()

    private val killsKey = NamespacedKey(plugin, "kills")
    private val deathsKey = NamespacedKey(plugin, "deaths")
    private val killerStorage = mutableMapOf<UUID, UUID?>()

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
        Task.syncRepeating(::updateKillers, 0, KILLER_STORE_TICKS - 1)
    }

    private fun incrementPDC(persistentDataHolder: PersistentDataHolder, key: NamespacedKey, amount: Int = 1) {
        val prev = persistentDataHolder.persistentDataContainer.getOrDefault(key, PersistentDataType.INTEGER, 0)
        persistentDataHolder.persistentDataContainer.set(key, PersistentDataType.INTEGER, prev + amount)
    }

    /**
     * Increments statistics for both the victim (death)
     * and for the killer (kill). Also gives the killer
     * reward money.
     */
    fun logKill(victim: Player) {
        incrementPDC(victim, deathsKey)
        getKiller(victim)?.let {
            incrementPDC(it, killsKey)
            economy.depositPlayer(it, plugin.config.killReward)
        }
    }

    private fun getInt(player: Player, key: NamespacedKey) =
        player.persistentDataContainer.getOrDefault(key, PersistentDataType.INTEGER, 0)

    fun getKills(player: Player) = getInt(player, killsKey)
    fun getDeaths(player: Player) = getInt(player, deathsKey)

    private fun getKiller(victim: Player): Player? {
        return killerStorage[victim.uniqueId]
            ?.let { plugin.server.getPlayer(it) }
    }

    private fun updateKillers() {
        killerStorage.keys.forEach(::updateKiller)
    }

    private fun updateKiller(victimId: UUID) {
        val victim = plugin.server.getPlayer(victimId)
        // Player no longer online
        victim ?: run {
            killerStorage.remove(victimId)
            return
        }
        val newKillerId = victim.killer?.uniqueId
        // Only update if killer is set
        if (newKillerId != null) {
            killerStorage[victimId] = newKillerId
        }
    }

    @EventHandler
    private fun CMIPvPStartEventAsync.onPvpStart() {
        Task.syncDelayed { ->
            killerStorage[player.uniqueId] = player.killer?.uniqueId
        }
    }

    @EventHandler
    private fun CMIPvPEndEventAsync.onPvpEnd() {
        Task.syncDelayed { ->
            killerStorage.remove(player.uniqueId)
        }
    }

}
