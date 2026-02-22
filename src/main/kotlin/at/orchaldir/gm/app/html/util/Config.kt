package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.AREA
import at.orchaldir.gm.app.html.economy.editEconomyConfig
import at.orchaldir.gm.app.html.economy.parseEconomyConfig
import at.orchaldir.gm.app.html.economy.showEconomyConfig
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.app.html.rpg.editRpgConfig
import at.orchaldir.gm.app.html.rpg.parseRpgConfig
import at.orchaldir.gm.app.html.rpg.showRpgConfig
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.time.editTime
import at.orchaldir.gm.app.html.time.parseTime
import at.orchaldir.gm.app.html.time.showTime
import at.orchaldir.gm.core.model.Config
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.selector.time.getDefaultCalendar
import at.orchaldir.gm.utils.math.unit.AreaUnit
import at.orchaldir.gm.utils.math.unit.LARGE_AREA_UNITS
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showConfig(
    call: ApplicationCall,
    state: State,
    config: Config,
) {
    field("Large Area Unit", config.largeAreaUnit)
    showEconomyConfig(call, state, config.economy)
    showRpgConfig(call, state, config.rpg)
    showTime(call, state, config.time)
}


// edit

fun HtmlBlockTag.editConfig(state: State, config: Config) {
    selectValue(
        "Large Area Unit",
        AREA,
        LARGE_AREA_UNITS,
        config.largeAreaUnit,
    )
    editEconomyConfig(state, config.economy)
    editRpgConfig(state, config.rpg)
    editTime(state, config.time)
}

// parse

fun parseConfig(
    state: State,
    parameters: Parameters,
) = Config(
    parseEconomyConfig(state, parameters),
    parseRpgConfig(parameters),
    parseTime(parameters, state.getDefaultCalendar()),
    parse(parameters, AREA, AreaUnit.Hectare),
)
