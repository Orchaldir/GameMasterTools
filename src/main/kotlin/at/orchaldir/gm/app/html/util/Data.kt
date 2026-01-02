package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.AREA
import at.orchaldir.gm.app.html.economy.editEconomyData
import at.orchaldir.gm.app.html.economy.parseEconomyData
import at.orchaldir.gm.app.html.economy.showEconomyData
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.app.html.rpg.editRpgData
import at.orchaldir.gm.app.html.rpg.parseRpgData
import at.orchaldir.gm.app.html.rpg.showRpgData
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.time.editTime
import at.orchaldir.gm.app.html.time.parseTime
import at.orchaldir.gm.app.html.time.showTime
import at.orchaldir.gm.core.model.Data
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.selector.time.getDefaultCalendar
import at.orchaldir.gm.utils.math.unit.AreaUnit
import at.orchaldir.gm.utils.math.unit.LARGE_AREA_UNITS
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showData(
    call: ApplicationCall,
    state: State,
    data: Data,
) {
    field("Large Area Unit", data.largeAreaUnit)
    showEconomyData(call, state, data.economy)
    showRpgData(call, state, data.rpg)
    showTime(call, state, data.time)
}


// edit

fun HtmlBlockTag.editData(state: State, data: Data) {
    selectValue(
        "Large Area Unit",
        AREA,
        LARGE_AREA_UNITS,
        data.largeAreaUnit,
    )
    editEconomyData(state, data.economy)
    editRpgData(state, data.rpg)
    editTime(state, data.time)
}

// parse

fun parseData(
    state: State,
    parameters: Parameters,
) = Data(
    parseEconomyData(state, parameters),
    parseRpgData(parameters),
    parseTime(parameters, state.getDefaultCalendar()),
    parse(parameters, AREA, AreaUnit.Hectare),
)
