@file:UseSerializers(DurationSerializer::class, InternalItemStackSerializer::class)

package gecko10000.geckodeathbans.config

import gecko10000.geckolib.config.serializers.DurationSerializer
import gecko10000.geckolib.config.serializers.InternalItemStackSerializer
import gecko10000.geckolib.extensions.MM
import gecko10000.geckolib.extensions.parseMM
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

@Serializable
data class Config(
    val banTimes: List<Duration> = listOf(
        3.days,
        7.days,
        14.days,
        28.days,
    ),
    val banMessage: String = "<cause>",
    private val unbanAllBroadcast: String = "<green><amount> players were unbanned.",
    var totemItem: ItemStack = ItemStack.of(Material.TOTEM_OF_UNDYING).apply {
        this.editMeta {
            it.setEnchantmentGlintOverride(true)
            it.itemName(parseMM("<green><b>Respawn Totem"))
            it.lore(
                listOf(
                    parseMM("<yellow>Lets you respawn on death"),
                    parseMM("<yellow>instead of getting banned."),
                    parseMM("<yellow>Use like a <lang:item.minecraft.totem_of_undying>."),
                    Component.empty(),
                    parseMM("<yellow>This will not keep your inventory.")
                )
            )
        }
    },
    private val combatLogDeathMessage: String = "<player> logged out during combat",
    private val deathBanBroadcast: String = "<red><player> has been death-banned for <duration>."
) {
    fun unbanAllBroadcast(amount: Int): Component {
        return MM.deserialize(unbanAllBroadcast, Placeholder.unparsed("amount", amount.toString()))
    }

    fun combatLogDeathMessage(player: Player): Component {
        return MM.deserialize(combatLogDeathMessage, Placeholder.unparsed("player", player.name))
    }

    fun deathBanBroadcast(playerName: String, duration: Duration): Component {
        return MM.deserialize(
            deathBanBroadcast,
            Placeholder.unparsed("player", playerName),
            Placeholder.unparsed("duration", duration.toString())
        )
    }

}
