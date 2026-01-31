package dev.schnelle.locatorBarConfiguration

import dev.schnelle.locatorBarConfiguration.menu.submenus.range.MAX_RANGE
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin

/**
 * LocatorBarOption compatibility
 */
class LBO {
    companion object {
        const val INCOMPATIBILITY_MESSAGE =
            "Locator Bar Configuration 和 RedSyven's Locator Bar Options 都已启用，两个插件都无法正常运行"

        private val lboKeyOff = NamespacedKey("lbo", "off")
        private val lboKeyNormalize = NamespacedKey("lbo", "normalize")
        private val lboKeyCustomize = NamespacedKey("lbo", "customize")

        /**
         * check if Locator Bar Options datapack is enabled.
         *
         * Filters by description, which is NOT a perfect way to do it.
         */
        fun isEnabled(): Boolean {
            Bukkit.getDatapackManager().refreshPacks()
            return !Bukkit
                .getDatapackManager()
                .enabledPacks
                .mapNotNull { it.description as? TextComponent }
                .none { it.content().startsWith("RedSyven's Locator Bar Options") }
        }

        /**
         * Register player listener used for automatic data migration and admin incompatibility notifications.
         */
        fun registerPlayerListener(plugin: Plugin) {
            Bukkit.getPluginManager().registerEvents(PlayerListener(plugin), plugin)
        }

        /**
         * Migrate a players data from Locator Bar Options to the plugin.
         */
        fun migratePlayer(
            plugin: Plugin,
            player: Player,
        ) {
            val attributes =
                listOf(Attribute.WAYPOINT_TRANSMIT_RANGE, Attribute.WAYPOINT_RECEIVE_RANGE)

            for (attribute in attributes) {
                val instance = player.getAttribute(attribute)!!
                if (
                    instance.baseValue != MAX_RANGE || !AttributeAdapter.isLocatorBarEnabled(player)
                ) {
                    plugin.logger.fine(
                        "玩家 ${player.name} 已经通过插件自定义了范围",
                    )
                    return
                }
            }

            plugin.logger.finer("尝试为玩家 ${player.name} 将 LBO 迁移到 LBC")

            var migratedData = false
            var disableBar = false

            for (attribute in attributes) {
                val instance = player.getAttribute(attribute)!!

                val hasOff = AttributeAdapter.hasAttributeModifier(instance, lboKeyOff)
                val hasNormalize = AttributeAdapter.hasAttributeModifier(instance, lboKeyNormalize)
                val hasCustomize = AttributeAdapter.hasAttributeModifier(instance, lboKeyCustomize)

                if (hasOff) {
                    // Remove off to get values before disabling
                    AttributeAdapter.removeModifier(instance, lboKeyOff)
                    disableBar = true
                    migratedData = true
                }

                if (hasNormalize && hasCustomize) {
                    val value = AttributeAdapter.getAttributeValue(player, attribute)!!
                    AttributeAdapter.setAttributeBaseValue(player, attribute, value)
                }
                if (hasNormalize) {
                    AttributeAdapter.removeModifier(instance, lboKeyNormalize)
                    migratedData = true
                }
                if (hasCustomize) {
                    AttributeAdapter.removeModifier(instance, lboKeyCustomize)
                    migratedData = true
                }
            }

            if (disableBar) {
                AttributeAdapter.disableLocatorBar(player)
            }

            if (!migratedData) {
                return
            }

            val migrationMessageString = Config.getInstance().getLBOMigrationUserNotification()
            if (migrationMessageString.isBlank()) {
                return
            }
            val message = MiniMessage.miniMessage()

            player
                .sendMessage(
                    message.deserialize(migrationMessageString),
                )
        }
    }

    /**
     * Listener used for automatic data migration and admin incompatibility notifications.
     */
    class PlayerListener(
        private val plugin: Plugin,
    ) : Listener {
        @EventHandler
        fun onJoin(event: PlayerJoinEvent) {
            Scheduler(plugin).runAsyncTask("LBO 监听器出错。可能是迁移过程中出现的问题", 1) {
                val player = event.player
                if (isEnabled()) {
                    if (!player.isOp) {
                        return@runAsyncTask
                    }

                    player.sendMessage(
                        Component
                            .text("Incompatibility Alert: ", NamedTextColor.RED)
                            .appendNewline()
                            .append(Component.text(INCOMPATIBILITY_MESSAGE)),
                    )
                    return@runAsyncTask
                }

                if (Config.getInstance().getEnableLBOMigration()) {
                    migratePlayer(plugin, player)
                }
            }
        }
    }
}
