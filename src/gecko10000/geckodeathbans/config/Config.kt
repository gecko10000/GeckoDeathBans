@file:UseSerializers(DurationSerializer::class)

package gecko10000.geckodeathbans.config

import gecko10000.geckolib.config.serializers.DurationSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.damage.DamageType
import org.bukkit.event.entity.PlayerDeathEvent
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
    private val banMessage: String = "<cause>"
) {

    private val plainTextComponentSerializer by lazy { PlainTextComponentSerializer.plainText() }

    private fun translateDamageType(damageType: DamageType): String {
        val translationKey = damageType.translationKey
        val translated = plainTextComponentSerializer.serialize(Component.translatable(translationKey))
        return translated
    }

    fun banMessage(event: PlayerDeathEvent): String {
        val causeString = event.deathMessage()?.let { plainTextComponentSerializer.serialize(it) } ?: ""
        return banMessage.replace("<cause>", causeString)
    }
}
