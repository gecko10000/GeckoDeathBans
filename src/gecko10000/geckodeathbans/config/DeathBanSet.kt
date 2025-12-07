@file:UseSerializers(UUIDSerializer::class)

package gecko10000.geckodeathbans.config

import gecko10000.geckolib.config.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.util.*

@Serializable
data class DeathBanSet(
    val deathBannedPlayers: MutableSet<UUID> = mutableSetOf()
)
