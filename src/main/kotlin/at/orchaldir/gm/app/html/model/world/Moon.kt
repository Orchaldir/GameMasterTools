package at.orchaldir.gm.app.html.model.world

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.LENGTH
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.TILE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.field
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.model.world.moon.MoonId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.util.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showMoon(
    call: ApplicationCall,
    state: State,
    moon: Moon,
) {
    val nextNewMoon = moon.getNextNewMoon(state.time.currentDate)
    val nextFullMoon = moon.getNextFullMoon(state.time.currentDate)

    optionalField("Title", moon.title)
    field("Cycle", moon.getCycle().toString() + " days")
    field("Color", moon.color)

    if (nextNewMoon > nextFullMoon) {
        field(call, state, "Next Full Moon", nextFullMoon)
        field(call, state, "Next New Moon", nextNewMoon)
    } else {
        field(call, state, "Next New Moon", nextNewMoon)
        field(call, state, "Next Full Moon", nextFullMoon)
    }
}

// edit

fun HtmlBlockTag.editMoon(
    moon: Moon,
) {
    selectName(moon.name)
    selectText("Optional Name", moon.title ?: "", TILE, 0)
    selectInt("Days per Quarter", moon.daysPerQuarter, 1, 100, 1, LENGTH, false)
    selectColor("Color", COLOR, Color.entries, moon.color)
}

// parse

fun parseMoonId(parameters: Parameters, param: String) = MoonId(parseInt(parameters, param))

fun parseMoon(id: MoonId, parameters: Parameters) = Moon(
    id,
    parameters.getOrFail(NAME),
    parameters[TILE]?.ifEmpty { null },
    parseInt(parameters, LENGTH, 1),
    parse(parameters, COLOR, Color.White),
)
