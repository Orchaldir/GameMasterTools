package at.orchaldir.gm.app.parse.world

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.TERRAIN
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.model.parseCreator
import at.orchaldir.gm.app.html.model.parseDataSources
import at.orchaldir.gm.app.html.model.parseDate
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.TerrainType
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.model.world.town.TownId
import io.ktor.http.*

fun parseTerrainType(parameters: Parameters) = parse(parameters, combine(TERRAIN, TYPE), TerrainType.Plain)

fun parseTownId(parameters: Parameters, param: String) = TownId(parseInt(parameters, param))

fun parseTown(parameters: Parameters, state: State, oldTown: Town) = oldTown.copy(
    name = parseName(parameters),
    foundingDate = parseDate(parameters, state, DATE),
    founder = parseCreator(parameters),
    sources = parseDataSources(parameters),
)
