package gecko10000.geckodeathbans

import gecko10000.geckodeathbans.di.MyKoinComponent
import org.bukkit.entity.Player
import org.koin.core.component.inject
import space.arim.libertybans.api.LibertyBans
import space.arim.libertybans.api.PlayerVictim
import space.arim.libertybans.api.PunishmentType
import space.arim.omnibus.OmnibusProvider
import space.arim.omnibus.util.concurrent.CentralisedFuture
import java.util.*
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class BanManager : MyKoinComponent {

    private val plugin: GeckoDeathBans by inject()
    private val worldDeathBanStorage: WorldDeathBanStorage by inject()

    private val libertyBans: LibertyBans = OmnibusProvider
        .getOmnibus().registry
        .getProvider(LibertyBans::class.java)
        .orElseThrow()

    fun banPlayer(player: Player, banDuration: Duration, banCause: String?) {
        plugin.server.broadcast(plugin.config.deathBanBroadcast(player.name, banDuration))
        val cause = banCause
            ?: "No one knows how you died. Congrats!"
        val reason = plugin.config.banMessage.replace("<cause>", cause)
        val draftBan = libertyBans.drafter.draftBuilder()
            .type(PunishmentType.BAN)
            .victim(PlayerVictim.of(player.uniqueId))
            .reason(reason)
            .duration(banDuration.toJavaDuration())
            .build()
        draftBan.enactPunishment()
        worldDeathBanStorage.storeDeathBan(player.uniqueId)
    }

    fun unbanPlayer(username: String): CentralisedFuture<Boolean> {
        lateinit var sharedUUID: UUID // uhhh
        return libertyBans.userResolver
            .lookupUUID(username)
            .thenComposeAsync {
                val uuid = it.orElse(null) ?: return@thenComposeAsync null
                sharedUUID = uuid
                libertyBans.revoker
                    .revokeByTypeAndVictim(PunishmentType.BAN, PlayerVictim.of(uuid))
                    .undoPunishment()
            }.thenApply { success ->
                if (success) {
                    worldDeathBanStorage.removeDeathBan(sharedUUID)
                }
                return@thenApply success
            }
    }

    fun unbanByUUID(uuid: UUID) = libertyBans.revoker
        .revokeByTypeAndVictim(PunishmentType.BAN, PlayerVictim.of(uuid))
        .undoPunishment()

}
