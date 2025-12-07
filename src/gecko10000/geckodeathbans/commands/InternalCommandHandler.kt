package gecko10000.geckodeathbans.commands

import com.destroystokyo.paper.profile.PlayerProfile
import gecko10000.geckodeathbans.*
import gecko10000.geckodeathbans.di.MyKoinComponent
import gecko10000.geckolib.extensions.MM
import gecko10000.geckolib.misc.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.inject

class InternalCommandHandler : MyKoinComponent {

    private val plugin: GeckoDeathBans by inject()
    private val banStepTracker: BanStepTracker by inject()
    private val banManager: BanManager by inject()
    private val deathBanStorage: DeathBanStorage by inject()
    private val respawnTotemManager: RespawnTotemManager by inject()

    protected fun setBanStep(target: Player, step: Int) {
        banStepTracker.setBanStep(target, step)
    }

    protected fun unban(sender: CommandSender, profile: PlayerProfile) {
        CoroutineScope(Dispatchers.IO).launch {
            profile.complete(false)
            Task.syncDelayed { ->
                val username = profile.name!!
                if (!deathBanStorage.isDeathBan(profile.id!!)) {
                    sender.sendMessage(
                        MM.deserialize(
                            "<red>Player <player> is not death banned.",
                            Placeholder.unparsed("player", username)
                        )
                    )
                    return@syncDelayed
                }
                banManager.unbanPlayer(username)
                    .thenAccept { success ->
                        val message = MM.deserialize(
                            (if (success) "<green>Unbanned"
                            else "<red>Couldn't unban") +
                                    " <player>.",
                            Placeholder.unparsed("player", username)
                        )
                        sender.sendMessage(message)
                    }
            }
        }
    }

    protected fun setDeathbanFlag(profile: PlayerProfile) {
        CoroutineScope(Dispatchers.IO).launch {
            profile.complete(false)
            val uuid = profile.id!!
            Task.syncDelayed { -> deathBanStorage.storeDeathBan(uuid) }
        }
    }

    protected fun unbanAll(sender: CommandSender) {
        val allBans = deathBanStorage.getAllDeathBans()
        allBans.forEach {
            banManager.unbanByUUID(it)
            deathBanStorage.removeDeathBan(it)
        }
        plugin.server.broadcast(plugin.config.unbanAllBroadcast(allBans.size))
    }

    protected fun setTotemItem(player: Player) {
        val item = player.inventory.itemInMainHand
        if (item.isEmpty) {
            player.sendRichMessage("<red>Hold an item to do this.")
            return
        }
        respawnTotemManager.setTotemItem(item)
        player.sendMessage(
            MM.deserialize(
                "<green>Set the totem item to <item>",
                Placeholder.component("item", item.effectiveName().hoverEvent(item.asHoverEvent()))
            )
        )
    }

    protected fun giveTotemItem(sender: CommandSender, target: Player, amount: Int) {
        target.give(IntRange(1, amount).map { respawnTotemManager.getTotemItem() })
        sender.sendMessage(
            MM.deserialize(
                "<green>Gave <player> <amount> respawn totem<s>.",
                Placeholder.unparsed("player", target.name),
                Placeholder.unparsed("amount", amount.toString()),
                Placeholder.unparsed("s", if (amount == 1) "" else "s")
            )
        )
    }

}
