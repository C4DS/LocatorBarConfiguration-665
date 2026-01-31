package dev.schnelle.locatorBarConfiguration.menu.submenus.range

import dev.schnelle.locatorBarConfiguration.Config
import dev.schnelle.locatorBarConfiguration.menu.submenus.AbstractMenu
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

/**
 * Submenu for configuring the players transmit range.
 */
class TransmitRangeMenu(
    player: Player,
    parentMenu: AbstractMenu?,
) : RangeSubMenu(player, parentMenu, Attribute.WAYPOINT_TRANSMIT_RANGE) {
    override fun getDescriptionText(): Array<String> =
        arrayOf(
            "选择其他人可以在定位栏中看到您的范围",
            "例如：将你的发射范围设置为 1k，如果你在 1000 个方块范围内，其他人就会在他们的定位栏上看到你",
        )

    override fun getButtonToolTip(rangeRepresentation: String): String =
        "其他玩家最远可以在 $rangeRepresentation 个方块范围内看到你"

    override fun getRanges(): List<Double> = Config.getInstance().getTransmitRanges()

    override fun isLocked(): Boolean = Config.getInstance().getTransmitRanges().isEmpty()
}
