package at.orchaldir.gm.app.html.race

import at.orchaldir.gm.app.RACE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.RaceGroup
import at.orchaldir.gm.core.model.race.RaceGroupId
import at.orchaldir.gm.core.selector.util.sortRaces
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag


// show

fun HtmlBlockTag.showRaceGroup(
    call: ApplicationCall,
    state: State,
    group: RaceGroup,
) {
    fieldIds(call, state, group.races)
}

// edit

fun HtmlBlockTag.editRaceGroup(
    call: ApplicationCall,
    state: State,
    group: RaceGroup,
) {
    selectName(group.name)
    selectElements(state, RACE, state.sortRaces(), group.races)
}

// parse

fun parseRaceGroupId(parameters: Parameters, param: String) = RaceGroupId(parseInt(parameters, param))

fun parseRaceGroup(state: State, parameters: Parameters, id: RaceGroupId) = RaceGroup(
    id,
    parseName(parameters),
    parseElements(parameters, RACE, ::parseRaceId),
)
