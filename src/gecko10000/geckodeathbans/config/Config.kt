@file:UseSerializers(DurationSerializer::class)

package gecko10000.geckodeathbans.config

import gecko10000.geckolib.config.serializers.DurationSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
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
)
