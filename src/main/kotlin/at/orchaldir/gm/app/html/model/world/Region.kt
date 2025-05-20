package at.orchaldir.gm.app.html.model.world

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.economy.material.parseMaterialId
import at.orchaldir.gm.app.html.model.realm.parseOptionalBattleId
import at.orchaldir.gm.app.html.model.realm.parseOptionalCatastropheId
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.*
import at.orchaldir.gm.core.selector.util.sortBattles
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
        Continent, Forrest, Mountain, UndefinedRegionData -> doNothing()
        is Battlefield -> optionalFieldLink("Caused by", call, state, data.battle)
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
    val battles = state.sortBattles()
    val catastrophes = state.sortCatastrophes()

    selectValue("Type", TYPE, RegionDataType.entries, data.getType()) {
        when (it) {
            RegionDataType.Battlefield -> battles.isEmpty()
            RegionDataType.Wasteland -> catastrophes.isEmpty()
            else -> false
        }
    }

    when (data) {
        Continent, Forrest, Mountain, UndefinedRegionData -> doNothing()
        is Battlefield -> selectOptionalElement(
            state,
            "Caused By",
            BATTLE,
            battles,
            data.battle,
        )

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
    RegionDataType.Battlefield -> Battlefield(
        parseOptionalBattleId(parameters, BATTLE),
    )

    RegionDataType.Continent -> Continent
    RegionDataType.Forrest -> Forrest
    RegionDataType.Mountain -> Mountain
    RegionDataType.Undefined -> UndefinedRegionData
    RegionDataType.Wasteland -> Wasteland(
        parseOptionalCatastropheId(parameters, CATASTROPHE),
    )

}
