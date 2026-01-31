package dev.schnelle.locatorBarConfiguration.menu.submenus.range

import dev.schnelle.locatorBarConfiguration.Config
import dev.schnelle.locatorBarConfiguration.menu.submenus.AbstractMenu
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

/**
 * Submenu for configuring the players receive range.
 */
class ReceiveRangeMenu(
    player: Player,
    parentMenu: AbstractMenu?,
) : RangeSubMenu(player, parentMenu, Attribute.WAYPOINT_RECEIVE_RANGE) {
    override fun getDescriptionText(): Array<String> =
        arrayOf(
            "选择您在定位栏中可以看到其他人的距离",
            "例如：将接收范围设置为 1k，即可在定位栏上看到 1000 个方块范围内的玩家",
            "你的发射范围同时也是接收范围。如果你将接收范围设置为 10k，而将发射范围设置为 1k，那么你的定位栏上将只能看到距离你 1k 方块以内的玩家",
        )

    override fun getButtonToolTip(rangeRepresentation: String): String = "你最远可以看到 $rangeRepresentation 个方块的玩家"

    override fun getRanges(): List<Double> = Config.getInstance().getReceiveRanges()

    override fun isLocked(): Boolean = Config.getInstance().getReceiveRanges().isEmpty()
}
