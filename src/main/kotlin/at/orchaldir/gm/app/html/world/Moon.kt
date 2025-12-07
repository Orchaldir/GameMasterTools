package at.orchaldir.gm.app.html.world

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.material.parseMaterialId
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.world.moon.*
import at.orchaldir.gm.core.selector.time.getCurrentDate
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
    showVitalStatus(call, state, moon.status)
    fieldPosition(call, state, moon.position)
    field("Cycle", moon.getCycle().toString() + " days")
    fieldColor(moon.color)
    optionalFieldLink("Plane", call, state, moon.plane)
    fieldIds(call, state, "Resources", moon.resources)
    showLocalElements(call, state, moon.id)

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
    call: ApplicationCall,
    state: State,
    moon: Moon,
) {
    selectName(moon.name)
    selectOptionalNotEmptyString("Optional Title", moon.title, TITLE)
    selectVitalStatus(
        state,
        moon.id,
        null,
        moon.status,
        ALLOWED_VITAL_STATUS_FOR_MOON,
        ALLOWED_CAUSES_OF_DEATH_FOR_MOON,
    )
    selectPosition(
        state,
        POSITION,
        moon.position,
        null,
        ALLOWED_MOON_POSITIONS,
    )
    selectInt("Days per Quarter", moon.daysPerQuarter, 1, 100, 1, LENGTH)
    selectColor(moon.color)
    selectOptionalElement(state, "Plane", PLANE, state.getPlaneStorage().getAll(), moon.plane)
    selectElements(state, "Resources", MATERIAL, state.getMaterialStorage().getAll(), moon.resources)
}

// parse

fun parseMoonId(parameters: Parameters, param: String) = MoonId(parseInt(parameters, param))

fun parseMoon(state: State, parameters: Parameters, id: MoonId) = Moon(
    id,
    parseName(parameters),
    parseOptionalNotEmptyString(parameters, TITLE),
    parseVitalStatus(parameters, state),
    parsePosition(parameters, state),
    parseInt(parameters, LENGTH, 1),
    parse(parameters, COLOR, Color.White),
    parseOptionalPlaneId(parameters, PLANE),
    parseElements(parameters, MATERIAL, ::parseMaterialId),
)
