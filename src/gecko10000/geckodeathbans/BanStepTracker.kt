package gecko10000.geckodeathbans

import gecko10000.geckodeathbans.di.MyKoinComponent
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.koin.core.component.inject
import kotlin.math.min
import kotlin.time.Duration

class BanStepTracker : MyKoinComponent {

    private val plugin: GeckoDeathBans by inject()
    private val banStepKey = NamespacedKey(plugin, "ban_step")

    fun getBanStep(player: Player) =
        player.persistentDataContainer.getOrDefault(banStepKey, PersistentDataType.INTEGER, 0)

    fun setBanStep(player: Player, step: Int) = player.persistentDataContainer.set(
        banStepKey, PersistentDataType
            .INTEGER, step
    )

    /**
     * Sets the next ban duration step and returns the current duration.
     */
    fun stepBanDuration(player: Player): Duration {
        val lastBanStep = plugin.config.banTimes.size - 1
        val storedBanStep = getBanStep(player)
        val actualBanStep = min(storedBanStep, lastBanStep)
        val nextBanStep = min(actualBanStep + 1, plugin.config.banTimes.size - 1)
        setBanStep(player, nextBanStep)
        val banDuration = plugin.config.banTimes[actualBanStep]
        return banDuration
    }

}
