package gecko10000.geckodeathbans

import gecko10000.geckodeathbans.di.MyKoinComponent
import gecko10000.geckolib.misc.Task
import org.bukkit.EntityEffect
import org.bukkit.GameRule
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityResurrectEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.world.WorldLoadEvent
import org.koin.core.component.inject
import java.util.*
import kotlin.math.min

class Listeners : Listener, MyKoinComponent {

    private val plugin: GeckoDeathBans by inject()
    private val banStepTracker: BanStepTracker by inject()
    private val banManager: BanManager by inject()
    private val worldDeathBanStorage: WorldDeathBanStorage by inject()
    private val respawnTotemManager: RespawnTotemManager by inject()

    private val respawnTotemUsers = mutableSetOf<UUID>()

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
        plugin.server.worlds.forEach(::fixWorldGamerules)
    }

    @EventHandler
    private fun WorldLoadEvent.onWorldLoad() {
        fixWorldGamerules(world)
    }

    private fun fixWorldGamerules(world: World) {
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true)
    }

    /**
     * In case the player has been unbanned via an external method.
     */
    @EventHandler
    private fun PlayerJoinEvent.onPlayerJoin() {
        worldDeathBanStorage.removeDeathBan(player.uniqueId)
    }

    @EventHandler(ignoreCancelled = true)
    private fun EntityResurrectEvent.onPlayerResurrect() {
        val player = entity as? Player ?: return
        val hand = hand ?: return
        val item = player.inventory.getItem(hand)
        if (item.isEmpty) return
        if (!respawnTotemManager.isTotemItem(item)) {
            return
        }
        this.isCancelled = true
        player.inventory.setItem(hand, item.subtract())
        respawnTotemUsers += player.uniqueId
        Task.syncDelayed { -> respawnTotemUsers -= player.uniqueId }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private fun PlayerDeathEvent.onPlayerDeath() {
        if (player.hasPermission("geckodeathbans.bypass")) {
            return
        }
        if (respawnTotemUsers.contains(player.uniqueId)) {
            player.playEffect(EntityEffect.PROTECTED_FROM_DEATH)
            return
        }
        val lastBanStep = plugin.config.banTimes.size - 1
        val storedBanStep = banStepTracker.getBanStep(player)
        val actualBanStep = min(storedBanStep, lastBanStep)
        val nextBanStep = min(actualBanStep + 1, plugin.config.banTimes.size - 1)
        banStepTracker.setBanStep(player, nextBanStep)
        val banDuration = plugin.config.banTimes[actualBanStep]
        banManager.banPlayer(player, banDuration, this)
    }

}
