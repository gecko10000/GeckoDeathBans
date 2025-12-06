@file:UseSerializers(DurationSerializer::class)

package gecko10000.geckodeathbans.config

import gecko10000.geckolib.config.serializers.DurationSerializer
import gecko10000.geckolib.extensions.MM
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
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
) {
    fun unbanAllBroadcast(amount: Int): Component {
        return MM.deserialize(unbanAllBroadcast, Placeholder.unparsed("amount", amount.toString()))
    }
}
