package gecko10000.geckodeathbans

import gecko10000.geckodeathbans.di.MyKoinComponent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.koin.core.component.inject
import kotlin.math.min

class Listeners : Listener, MyKoinComponent {

    private val plugin: GeckoDeathBans by inject()
    private val banStepTracker: BanStepTracker by inject()
    private val banManager: BanManager by inject()
    private val worldDeathBanStorage: WorldDeathBanStorage by inject()

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private fun PlayerDeathEvent.onPlayerDeath() {
        val lastBanStep = plugin.config.banTimes.size - 1
        val storedBanStep = banStepTracker.getBanStep(player)
        val actualBanStep = min(storedBanStep, lastBanStep)
        val nextBanStep = min(actualBanStep + 1, plugin.config.banTimes.size - 1)
        banStepTracker.setBanStep(player, nextBanStep)
        val banDuration = plugin.config.banTimes[actualBanStep]
        banManager.banPlayer(player, banDuration, this)
    }

    @EventHandler
    private fun PlayerBucketEmptyEvent.onBucketEmpty() {
        banStepTracker.setBanStep(player, 0)
    }

}
