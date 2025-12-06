package gecko10000.geckodeathbans

import gecko10000.geckodeathbans.di.MyKoinComponent
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.koin.core.component.inject

class RespawnTotemManager : MyKoinComponent {

    private val plugin: GeckoDeathBans by inject()

    private val totemKey = NamespacedKey(plugin, "respawn_totem")

    fun getTotemItem(): ItemStack {
        val item = plugin.config.totemItem.clone()
        item.editPersistentDataContainer {
            it.set(totemKey, PersistentDataType.BOOLEAN, true)
        }
        return item
    }

    fun setTotemItem(item: ItemStack) {
        item.editPersistentDataContainer {
            it.remove(totemKey)
        }
        plugin.config.totemItem = item
        plugin.saveConfigs()
    }

    fun isTotemItem(item: ItemStack): Boolean {
        return item.persistentDataContainer.has(totemKey)
    }

}
