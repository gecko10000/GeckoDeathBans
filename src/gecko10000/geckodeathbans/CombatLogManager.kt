package gecko10000.geckodeathbans

import com.Zrips.CMI.CMI
import gecko10000.geckodeathbans.di.MyKoinComponent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.koin.core.component.inject
import java.util.*

class CombatLogManager : Listener, MyKoinComponent {

    private val plugin: GeckoDeathBans by inject()
    private val banStepTracker: BanStepTracker by inject()
    private val banManager: BanManager by inject()
    private val plainTextComponentSerializer: PlainTextComponentSerializer by inject()

    private val killedForCombatLogging = mutableSetOf<UUID>()

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    fun isBeingKilledForCombatLogging(player: Player) = killedForCombatLogging.contains(player.uniqueId)

    @EventHandler
    private fun PlayerQuitEvent.onQuit() {
        if (player.hasPermission("geckodeathbans.bypass.combatlogkill")) {
            return
        }
        val inCombat = CMI.getInstance().playerCombatManager.isInCombatWithPlayer(player.uniqueId)
        if (!inCombat) {
            return
        }
        player.inventory.clear()
        killedForCombatLogging += player.uniqueId
        player.health = 0.0
        killedForCombatLogging -= player.uniqueId
        if (player.hasPermission("geckodeathbans.bypass.ban")) { // kill but don't ban
            return
        }
        val deathMessageComponent = plugin.config.combatLogDeathMessage(player)
        val deathMessageString = plainTextComponentSerializer.serialize(deathMessageComponent)
        banManager.banPlayer(
            player, banStepTracker.stepBanDuration(player), deathMessageString
        )
    }

}
