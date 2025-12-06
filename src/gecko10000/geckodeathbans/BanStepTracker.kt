package gecko10000.geckodeathbans

import gecko10000.geckodeathbans.di.MyKoinComponent
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.koin.core.component.inject

class BanStepTracker : MyKoinComponent {

    private val plugin: GeckoDeathBans by inject()
    private val banStepKey = NamespacedKey(plugin, "ban_step")

    fun getBanStep(player: Player) =
        player.persistentDataContainer.getOrDefault(banStepKey, PersistentDataType.INTEGER, 0)

    fun setBanStep(player: Player, step: Int) = player.persistentDataContainer.set(
        banStepKey, PersistentDataType
            .INTEGER, step
    )

}
