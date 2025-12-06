package gecko10000.geckodeathbans

import gecko10000.geckodeathbans.commands.CommandHandler
import gecko10000.geckodeathbans.config.Config
import gecko10000.geckodeathbans.di.MyKoinContext
import gecko10000.geckolib.config.YamlFileManager
import org.bukkit.plugin.java.JavaPlugin

class GeckoDeathBans : JavaPlugin() {

    private val configFile = YamlFileManager(
        configDirectory = dataFolder,
        initialValue = Config(),
        serializer = Config.serializer(),
    )

    val config: Config
        get() = configFile.value

    override fun onEnable() {
        MyKoinContext.init(this)
        Listeners()
        CommandHandler().register()
    }

    fun reloadConfigs() {
        configFile.reload()
    }

}
