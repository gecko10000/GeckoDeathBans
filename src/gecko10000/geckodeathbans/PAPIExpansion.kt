package gecko10000.geckodeathbans

import gecko10000.geckodeathbans.di.MyKoinComponent
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player
import org.koin.core.component.inject

class PAPIExpansion : MyKoinComponent, PlaceholderExpansion() {

    private val plugin: GeckoDeathBans by inject()
    private val kdCountManager: KDCountManager by inject()

    init {
        this.register()
    }

    override fun getIdentifier() = plugin.namespace()

    override fun getAuthor() = plugin.pluginMeta.authors.joinToString()

    override fun getVersion() = plugin.pluginMeta.version

    override fun persist() = true

    override fun onPlaceholderRequest(player: Player, params: String): String? {
        return when (params) {
            "kills" -> kdCountManager.getKills(player).toString()
            "deaths" -> kdCountManager.getDeaths(player).toString()
            else -> null
        }
    }

    override fun getPlaceholders() = setOf("kills", "deaths").map { "%${identifier}_$it%" }

}
