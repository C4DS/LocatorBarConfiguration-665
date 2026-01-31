package dev.schnelle.locatorBarConfiguration.menu.submenus.implementations

import dev.schnelle.locatorBarConfiguration.AttributeAdapter
import dev.schnelle.locatorBarConfiguration.menu.bodyFromString
import dev.schnelle.locatorBarConfiguration.menu.submenus.AbstractMenu
import dev.schnelle.locatorBarConfiguration.menu.submenus.AbstractSubMenu
import dev.schnelle.locatorBarConfiguration.menu.submenus.range.ReceiveRangeMenu
import dev.schnelle.locatorBarConfiguration.menu.submenus.range.TransmitRangeMenu
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.action.DialogAction
import io.papermc.paper.registry.data.dialog.body.DialogBody
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickCallback
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player

/**
 * Main submenu.
 *
 * Used for enabling and disabling the locator bar entirely and for navigation to other submenus.
 */
@Suppress("UnstableApiUsage")
class MainSubMenu(
    private val player: Player,
    parentMenu: AbstractMenu?,
) : AbstractSubMenu(player, "菜单", parentMenu, 1) {
    override fun getNavigationButtonContent(): Component = Component.text("Menu")

    override fun getNavigationTooltip(): String = "配置你的定位栏"

    override fun beforeDialog() {}

    override fun getBody(): List<DialogBody> =
        bodyFromString(
            "定位栏显示其他玩家的位置",
            "您可以使用此菜单配置其他玩家在其定位栏上看到您的范围，以及您在自己的定位栏上看到其他玩家的范围",
        )

    override fun getActionButtons(): List<ActionButton> =
        listOf(
            getToggleButton(),
            ReceiveRangeMenu(player, this).getNavigationButton(),
            TransmitRangeMenu(player, this).getNavigationButton(),
            ColorSubMenu(player, this).getNavigationButton(),
        )

    // The main menu can never be locked.
    override fun isLocked(): Boolean = false

    /**
     * Get button to toggle locator bar entirely.
     */
    private fun getToggleButton(): ActionButton {
        val barEnabled = AttributeAdapter.isLocatorBarEnabled(player)

        val enabledComponent =
            if (barEnabled) {
                Component.text("已启用").color(NamedTextColor.GREEN)
            } else {
                Component.text("已禁用").color(NamedTextColor.RED)
            }

        return ActionButton.create(
            Component.text("定位栏: ").append(enabledComponent),
            Component.text(
                "禁用定位栏。禁用后，您将不会显示在其他玩家的定位栏中，您也看不到自己的定位栏",
            ),
            200,
            DialogAction.customClick(
                { _, _ ->
                    if (barEnabled) {
                        AttributeAdapter.disableLocatorBar(player)
                    } else {
                        AttributeAdapter.enableLocatorBar(player)
                    }
                    showDialog()
                },
                ClickCallback.Options
                    .builder()
                    .uses(1)
                    .build(),
            ),
        )
    }
}
