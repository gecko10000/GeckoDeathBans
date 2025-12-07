package gecko10000.geckodeathbans.commands;

import com.destroystokyo.paper.profile.PlayerProfile;
import gecko10000.geckodeathbans.GeckoDeathBans;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.strokkur.commands.annotations.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@Command("geckodeathbans")
@Aliases("gdb")
@Permission("geckodeathbans.command")
public class CommandHandler {

    private final GeckoDeathBans plugin = JavaPlugin.getPlugin(GeckoDeathBans.class);

    public void register() {
        plugin.getLifecycleManager()
                .registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event ->
                        CommandHandlerBrigadier.register(
                                event.registrar()
                        )
                ));
    }

    @Executes("reset")
    @Permission("geckodeathbans.command.reset")
    void reset(CommandSender sender, Player target) {
        plugin.getInternalCommandHandler().setBanStep(target, 0);
        sender.sendRichMessage("<green>Reset ban step for player " + target.getName());
    }

    @Executes("set")
    @Permission("geckodeathbans.command.set")
    void set(CommandSender sender, Player target, int step) {
        plugin.getInternalCommandHandler().setBanStep(target, step);
        sender.sendRichMessage("<green>Set ban step to " + step + " for player " + target.getName());
    }

    @Executes("reload")
    @Permission("geckodeathbans.command.reload")
    void reload(CommandSender sender) {
        plugin.reloadConfigs();
        sender.sendRichMessage("<green>Configs reloaded.");
    }

    @Executes("unban")
    @Permission("geckodeathbans.command.unban")
    void unban(CommandSender sender, PlayerProfile profile) {
        plugin.getInternalCommandHandler().unban(sender, profile);
    }

    @Executes("set_deathban_flag")
    @Permission("geckodeathbans.command.set_deathban_flag")
    void setDeathbanFlag(CommandSender sender, PlayerProfile profile) {
        plugin.getInternalCommandHandler().setDeathbanFlag(profile);
        sender.sendRichMessage("<green>Set flag.");
    }

    @Executes("unban_all")
    @Permission("geckodeathbans.command.unban_all")
    void unbanAll(CommandSender sender) {
        plugin.getInternalCommandHandler().unbanAll(sender);
    }

    @Executes("set_totem_item")
    @Permission("geckodeathbans.command.set_totem_item")
    void setTotemItem(CommandSender sender, @Executor Player player) {
        plugin.getInternalCommandHandler().setTotemItem(player);
    }

    @Executes("give_totem_item")
    @Permission("geckodeathbans.command.give_totem_item")
    void giveTotemItem(CommandSender sender, Player target, int amount) {
        plugin.getInternalCommandHandler().giveTotemItem(sender, target, amount);
    }

}
