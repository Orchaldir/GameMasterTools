package at.orchaldir.gm.app.html.model.world

import at.orchaldir.gm.app.TERRAIN
import at.orchaldir.gm.app.TOWN
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.town.parseOptionalTownId
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.TerrainType
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.core.model.world.town.TownMapId
import at.orchaldir.gm.core.selector.util.sortTowns
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showTownMap(
    call: ApplicationCall,
    state: State,
    townMap: TownMap,
) {
    optionalFieldLink(call, state, townMap.town)
    field("Size", townMap.map.size.format())
}

// edit

fun FORM.editTownMap(
    state: State,
    townMap: TownMap,
) {
    selectOptionalElement(state, "Town", TOWN, state.sortTowns(), townMap.town)
}

// parse
fun parseTownMapId(parameters: Parameters, param: String) = TownMapId(parseInt(parameters, param))
fun parseOptionalTownMapId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { TownMapId(it) }

fun parseTerrainType(parameters: Parameters) = parse(parameters, combine(TERRAIN, TYPE), TerrainType.Plain)

fun parseTownMap(parameters: Parameters, state: State, oldTownMap: TownMap) =
    oldTownMap.copy(town = parseOptionalTownId(parameters, TOWN))