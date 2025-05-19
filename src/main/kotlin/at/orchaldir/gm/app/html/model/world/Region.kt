package at.orchaldir.gm.app.html.model.world

import at.orchaldir.gm.app.CATASTROPHE
import at.orchaldir.gm.app.MATERIAL
import at.orchaldir.gm.app.PARENT
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.parseMaterialId
import at.orchaldir.gm.app.html.model.realm.parseOptionalCatastropheId
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.*
import at.orchaldir.gm.core.selector.util.sortCatastrophes
import at.orchaldir.gm.core.selector.util.sortMaterial
import at.orchaldir.gm.core.selector.util.sortRegions
import at.orchaldir.gm.core.selector.world.getSubRegions
import at.orchaldir.gm.core.selector.world.getTowns
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showRegion(
    call: ApplicationCall,
    state: State,
    region: Region,
) {
    showRegionData(call, state, region.data)
    optionalFieldLink("Parent Region", call, state, region.parent)
    fieldList(call, state, "Subregions", state.getSubRegions(region.id))
    fieldIdList(call, state, "Resources", region.resources)
    fieldList(call, state, state.getTowns(region.id))
}

private fun HtmlBlockTag.showRegionData(
    call: ApplicationCall,
    state: State,
    data: RegionData,
) {
    field("Type", data.getType())

    when (data) {
        Battlefield, Continent, Mountain, UndefinedRegionData -> doNothing()
        is Wasteland -> optionalFieldLink("Caused by", call, state, data.catastrophe)
    }
}

// edit

fun HtmlBlockTag.editRegion(
    state: State,
    region: Region,
) {
    val materials = state.sortMaterial()
    val regions = state.sortRegions(state.getRegionStorage().getAllExcept(region.id))

    selectName(region.name)
    editRegionData(state, region.data)
    selectOptionalElement(state, "Parent Region", PARENT, regions, region.parent)
    selectElements(state, "Resources", MATERIAL, materials, region.resources)
}

private fun HtmlBlockTag.editRegionData(
    state: State,
    data: RegionData,
) {
    val catastrophes = state.sortCatastrophes()

    selectValue("Type", TYPE, RegionDataType.entries, data.getType()) {
        when (it) {
            RegionDataType.Wasteland -> catastrophes.isEmpty()
            else -> false
        }
    }

    when (data) {
        Battlefield, Continent, Mountain, UndefinedRegionData -> doNothing()
        is Wasteland -> selectOptionalElement(
            state,
            "Caused By",
            CATASTROPHE,
            catastrophes,
            data.catastrophe,
        )
    }
}

// parse

fun parseRegionId(parameters: Parameters, param: String) = RegionId(parseInt(parameters, param))
fun parseOptionalRegionId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { RegionId(it) }

fun parseRegion(id: RegionId, parameters: Parameters) = Region(
    id,
    parseName(parameters),
    parseRegionData(parameters),
    parseOptionalRegionId(parameters, PARENT),
    parseElements(parameters, MATERIAL, ::parseMaterialId),
)

fun parseRegionData(parameters: Parameters) = when (parse(parameters, TYPE, RegionDataType.Undefined)) {
    RegionDataType.Battlefield -> Battlefield
    RegionDataType.Continent -> Continent
    RegionDataType.Mountain -> Mountain
    RegionDataType.Undefined -> UndefinedRegionData
    RegionDataType.Wasteland -> Wasteland(
        parseOptionalCatastropheId(parameters, CATASTROPHE),
    )

}
