package gecko10000.geckodeathbans

import gecko10000.geckodeathbans.di.MyKoinComponent
import gecko10000.geckolib.misc.Task
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
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

class Listeners : Listener, MyKoinComponent {

    private val plugin: GeckoDeathBans by inject()
    private val banStepTracker: BanStepTracker by inject()
    private val banManager: BanManager by inject()
    private val deathBanStorage: DeathBanStorage by inject()
    private val respawnTotemManager: RespawnTotemManager by inject()
    private val combatLogManager: CombatLogManager by inject()
    private val plainTextComponentSerializer: PlainTextComponentSerializer by inject()

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
        deathBanStorage.removeDeathBan(player.uniqueId)
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
        if (player.hasPermission("geckodeathbans.bypass.ban")) {
            return
        }
        if (respawnTotemUsers.contains(player.uniqueId)) {
            player.playEffect(EntityEffect.PROTECTED_FROM_DEATH)
            return
        }
        if (combatLogManager.isBeingKilledForCombatLogging(player)) {
            deathMessage(plugin.config.combatLogDeathMessage(player))
            return
        }
        val banCause = this.deathMessage()?.let { plainTextComponentSerializer.serialize(it) }
        Task.syncDelayed { -> banManager.banPlayer(player, banStepTracker.stepBanDuration(player), banCause) }
    }

}
