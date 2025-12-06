package gecko10000.geckodeathbans.commands;

import com.destroystokyo.paper.profile.PlayerProfile;
import gecko10000.geckodeathbans.GeckoDeathBans;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.strokkur.commands.annotations.Aliases;
import net.strokkur.commands.annotations.Command;
import net.strokkur.commands.annotations.Executes;
import net.strokkur.commands.annotations.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@Command("geckodeathbans")
@Aliases("gdb")
@Permission("geckodeathbans.command")
public class CommandHandler {

    private final GeckoDeathBans plugin = JavaPlugin.getPlugin(GeckoDeathBans.class);
    private final InternalCommandHandler internalCommandHandler = new InternalCommandHandler();

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
        internalCommandHandler.setBanStep(target, 0);
        sender.sendRichMessage("<green>Reset ban step for player " + target.getName());
    }

    @Executes("set")
    @Permission("geckodeathbans.command.set")
    void set(CommandSender sender, Player target, int step) {
        internalCommandHandler.setBanStep(target, step);
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
        internalCommandHandler.unban(sender, profile);
    }

    @Executes("set_deathban_flag")
    @Permission("geckodeathbans.command.set_deathban_flag")
    void setDeathbanFlag(CommandSender sender, PlayerProfile profile) {
        internalCommandHandler.setDeathbanFlag(profile);
    }

}
