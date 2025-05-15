package at.orchaldir.gm.app.html.model.world

import at.orchaldir.gm.app.TERRAIN
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.parseSimpleOptionalInt
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.world.terrain.TerrainType
import at.orchaldir.gm.core.model.world.town.TownMapId
import io.ktor.http.*

fun parseTownMapId(parameters: Parameters, param: String) = TownMapId(parseInt(parameters, param))
fun parseOptionalTownMapId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { TownMapId(it) }

fun parseTerrainType(parameters: Parameters) = parse(parameters, combine(TERRAIN, TYPE), TerrainType.Plain)
