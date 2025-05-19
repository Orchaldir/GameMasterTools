package at.orchaldir.gm.app.html.model.world

import at.orchaldir.gm.app.MATERIAL
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.parseMaterialId
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.Region
import at.orchaldir.gm.core.model.world.terrain.RegionId
import at.orchaldir.gm.core.selector.util.sortMaterial
import at.orchaldir.gm.core.selector.world.getTowns
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showRegion(
    call: ApplicationCall,
    state: State,
    region: Region,
) {
    fieldIdList(call, state, "Resources", region.resources)
    fieldList(call, state, state.getTowns(region.id))
}

// edit

fun HtmlBlockTag.editRegion(
    state: State,
    region: Region,
) {
    val materials = state.sortMaterial()

    selectName(region.name)
    selectElements(state, "Resources", MATERIAL, materials, region.resources)
}

// parse

fun parseRegionId(parameters: Parameters, param: String) = RegionId(parseInt(parameters, param))

fun parseRegion(id: RegionId, parameters: Parameters) = Region(
    id,
    parseName(parameters),
    parseElements(parameters, MATERIAL, ::parseMaterialId),
)

