package dev.schnelle.locatorBarConfiguration.menu

import dev.schnelle.locatorBarConfiguration.menu.submenus.AbstractMenu
import dev.schnelle.locatorBarConfiguration.menu.submenus.implementations.MainSubMenu
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase
import io.papermc.paper.registry.data.dialog.DialogRegistryEntry
import io.papermc.paper.registry.data.dialog.action.DialogAction
import io.papermc.paper.registry.data.dialog.type.DialogType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickCallback
import org.bukkit.entity.Player
import java.time.Duration

/**
 * The "main menu" which shows buttons to navigate to the MainSubMenu and the info screen.
 *
 * Necessary because to use dialogs in the pause screen, they have to be built by the time the players join.
 * Meant to be registered in registry during bootstrap.
 */
@Suppress("UnstableApiUsage")
class MainMenu : AbstractMenu() {
    override fun build(builder: DialogRegistryEntry.Builder) {
        builder
            .base(
                DialogBase
                    .builder(Component.text("定位栏配置"))
                    .pause(false)
                    .afterAction(DialogBase.DialogAfterAction.NONE)
                    .build(),
            ).type(
                DialogType.multiAction(
                    listOf(getSubMenuButton()),
                    ActionButton.create(
                        Component.text("完成"),
                        null,
                        200,
                        DialogAction.customClick(
                            { _, audience -> audience.closeDialog() },
                            ClickCallback.Options
                                .builder()
                                .uses(1)
                                .build(),
                        ),
                    ),
                    1,
                ),
            )
    }

    override fun beforeDialog() {}

    override fun getTitle(): String = "定位栏配置"

    /**
     * Get button for navigating to MainSubMenu.
     */
    private fun getSubMenuButton(): ActionButton =
        getNavButton(
            Component.text("配置"),
            Component.text(
                "配置您在定位栏上如何看到其他人，以及其他人如何看到您",
            ),
            ::MainSubMenu,
        )

    private fun getNavButton(
        label: Component,
        tooltip: Component?,
        menuFactory: (player: Player, parent: AbstractMenu) -> AbstractMenu,
    ): ActionButton {
        return ActionButton.create(
            label,
            tooltip,
            200,
            DialogAction.customClick(
                { _, audience ->
                    if (audience !is Player) {
                        audience.sendMessage(Component.text("只有玩家才能使用"))
                        return@customClick
                    }

                    menuFactory(audience, this).showDialog(audience)
                },
                ClickCallback.Options
                    .builder()
                    .uses(ClickCallback.UNLIMITED_USES)
                    .lifetime(Duration.ofDays(1024L))
                    .build(),
            ),
        )
    }
}
