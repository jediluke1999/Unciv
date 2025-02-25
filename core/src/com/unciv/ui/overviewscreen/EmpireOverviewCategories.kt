package com.unciv.ui.overviewscreen

import com.badlogic.gdx.utils.Align
import com.unciv.UncivGame
import com.unciv.logic.civilization.Civilization
import com.unciv.ui.overviewscreen.EmpireOverviewTab.EmpireOverviewTabPersistableData
import com.unciv.ui.utils.KeyCharAndCode

private typealias FactoryType = (Civilization, EmpireOverviewScreen, EmpireOverviewTabPersistableData?) -> EmpireOverviewTab

enum class EmpireOverviewTabState { Normal, Disabled, Hidden }
private typealias StateTesterType = (Civilization) -> EmpireOverviewTabState
private fun Boolean.toState(): EmpireOverviewTabState = if (this) EmpireOverviewTabState.Disabled else EmpireOverviewTabState.Normal

/** This controls which Tabs for the [EmpireOverviewScreen] exist and their order.
 *
 *  To add a Tab, build a new [EmpireOverviewTab] subclass and fill out a new entry here, that's all.
 *  Note the enum value's name is used as Tab caption, so if you ever need a non-alphanumeric caption please redesign to include a property for the caption.
 */
enum class EmpireOverviewCategories(
    val iconName: String,
    val shortcutKey: KeyCharAndCode,
    val scrollAlign: Int,
    val factory: FactoryType,
    val stateTester: StateTesterType
) {
    Cities("OtherIcons/Cities", 'C', Align.topLeft,
        fun (viewingPlayer: Civilization, overviewScreen: EmpireOverviewScreen, persistedData: EmpireOverviewTabPersistableData?)
                = CityOverviewTab(viewingPlayer, overviewScreen, persistedData),
        fun (viewingPlayer: Civilization) = viewingPlayer.cities.isEmpty().toState()),
    Stats("StatIcons/Gold", 'S', Align.top,
        fun (viewingPlayer: Civilization, overviewScreen: EmpireOverviewScreen, _: EmpireOverviewTabPersistableData?)
                = StatsOverviewTab(viewingPlayer, overviewScreen),
        fun (_: Civilization) = EmpireOverviewTabState.Normal),
    Trades("StatIcons/Acquire", 'T', Align.top,
        fun (viewingPlayer: Civilization, overviewScreen: EmpireOverviewScreen, _: EmpireOverviewTabPersistableData?)
                = TradesOverviewTab(viewingPlayer, overviewScreen),
        fun (viewingPlayer: Civilization) = viewingPlayer.diplomacy.values.all { it.trades.isEmpty() }.toState()),
    Units("OtherIcons/Shield", 'U', Align.topLeft,
        fun (viewingPlayer: Civilization, overviewScreen: EmpireOverviewScreen, persistedData: EmpireOverviewTabPersistableData?)
                = UnitOverviewTab(viewingPlayer, overviewScreen, persistedData),
        fun (viewingPlayer: Civilization) = viewingPlayer.units.getCivUnits().none().toState()),
    Politics("OtherIcons/Politics", 'P', Align.top,
        fun (viewingPlayer: Civilization, overviewScreen: EmpireOverviewScreen, persistedData: EmpireOverviewTabPersistableData?)
                = GlobalPoliticsOverviewTable(viewingPlayer, overviewScreen, persistedData),
        fun (_: Civilization) = EmpireOverviewTabState.Normal),
    Resources("StatIcons/Happiness", 'R', Align.topLeft,
        fun (viewingPlayer: Civilization, overviewScreen: EmpireOverviewScreen, persistedData: EmpireOverviewTabPersistableData?)
                = ResourcesOverviewTab(viewingPlayer, overviewScreen, persistedData),
        fun (viewingPlayer: Civilization) = viewingPlayer.detailedCivResources.isEmpty().toState()),
    Religion("StatIcons/Faith", 'F', Align.top,
        fun (viewingPlayer: Civilization, overviewScreen: EmpireOverviewScreen, persistedData: EmpireOverviewTabPersistableData?)
                = ReligionOverviewTab(viewingPlayer, overviewScreen, persistedData),
        fun (viewingPlayer: Civilization) = when {
            !viewingPlayer.gameInfo.isReligionEnabled() -> EmpireOverviewTabState.Hidden
            viewingPlayer.gameInfo.religions.isEmpty() -> EmpireOverviewTabState.Disabled
            else -> EmpireOverviewTabState.Normal
        }),
    Wonders("OtherIcons/Wonders", 'W', Align.top,
        fun (viewingPlayer: Civilization, overviewScreen: EmpireOverviewScreen, _: EmpireOverviewTabPersistableData?)
                = WonderOverviewTab(viewingPlayer, overviewScreen),
        fun (viewingPlayer: Civilization) = (viewingPlayer.naturalWonders.isEmpty() && viewingPlayer.cities.isEmpty()).toState()),
    Notifications("OtherIcons/Notifications", 'N', Align.top,
        fun (viewingPlayer: Civilization, overviewScreen: EmpireOverviewScreen, persistedData: EmpireOverviewTabPersistableData?)
                = NotificationsOverviewTable(worldScreen = UncivGame.Current.worldScreen!!, viewingPlayer, overviewScreen, persistedData),
        fun (_: Civilization) = EmpireOverviewTabState.Normal)

    ; //must be here

    constructor(iconName: String, shortcutChar: Char, scrollAlign: Int, factory: FactoryType, stateTester: StateTesterType = { _ -> EmpireOverviewTabState.Normal })
        : this(iconName, KeyCharAndCode(shortcutChar), scrollAlign, factory, stateTester)
}
