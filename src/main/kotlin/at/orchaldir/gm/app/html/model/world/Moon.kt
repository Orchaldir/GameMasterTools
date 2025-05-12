package at.orchaldir.gm.app.html.model.world

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.LENGTH
import at.orchaldir.gm.app.PLANE
import at.orchaldir.gm.app.TITLE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.field
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.model.world.moon.MoonId
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.core.selector.util.sortPlanes
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showMoon(
    call: ApplicationCall,
    state: State,
    moon: Moon,
) {
    val currentDate = state.getCurrentDate()
    val nextNewMoon = moon.getNextNewMoon(currentDate)
    val nextFullMoon = moon.getNextFullMoon(currentDate)

    optionalField("Title", moon.title)
    field("Cycle", moon.getCycle().toString() + " days")
    fieldColor(moon.color)
    optionalFieldLink("Plane", call, state, moon.plane)

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
    state: State,
    moon: Moon,
) {
    selectName(moon.name)
    selectOptionalNotEmptyString("Optional Name", moon.title, TITLE)
    selectInt("Days per Quarter", moon.daysPerQuarter, 1, 100, 1, LENGTH)
    selectColor(moon.color)
    selectOptionalElement(state, "Plane", PLANE, state.sortPlanes(), moon.plane)
}

// parse

fun parseMoonId(parameters: Parameters, param: String) = MoonId(parseInt(parameters, param))

fun parseMoon(id: MoonId, parameters: Parameters) = Moon(
    id,
    parseName(parameters),
    parseOptionalNotEmptyString(parameters, TITLE),
    parseInt(parameters, LENGTH, 1),
    parse(parameters, COLOR, Color.White),
    parseOptionalPlaneId(parameters, PLANE),
)
