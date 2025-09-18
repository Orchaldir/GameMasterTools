package at.orchaldir.gm.app.html.world

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.material.parseMaterialId
import at.orchaldir.gm.app.html.realm.parseOptionalBattleId
import at.orchaldir.gm.app.html.realm.parseOptionalCatastropheId
import at.orchaldir.gm.app.html.util.fieldPosition
import at.orchaldir.gm.app.html.util.parsePosition
import at.orchaldir.gm.app.html.util.selectPosition
import at.orchaldir.gm.app.html.util.showLocalElements
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.*
import at.orchaldir.gm.core.selector.util.sortBattles
import at.orchaldir.gm.core.selector.util.sortCatastrophes
import at.orchaldir.gm.core.selector.util.sortMaterial
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
    fieldPosition(call, state, region.position)
    fieldIdList(call, state, "Resources", region.resources)
    fieldElements(call, state, state.getTowns(region.id))
    showLocalElements(call, state, region.id)
}

private fun HtmlBlockTag.showRegionData(
    call: ApplicationCall,
    state: State,
    data: RegionData,
) {
    field("Type", data.getType())

    when (data) {
        Continent, Desert, Forrest, Lake, Mountain, Sea, UndefinedRegionData -> doNothing()
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

    selectName(region.name)
    editRegionData(state, region.data)
    selectPosition(
        state,
        POSITION,
        region.position,
        null,
        region.data.getAllowedRegionTypes(),
    )
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
        Continent, Desert, Forrest, Lake, Mountain, Sea, UndefinedRegionData -> doNothing()
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

fun parseRegion(parameters: Parameters, state: State, id: RegionId) = Region(
    id,
    parseName(parameters),
    parseRegionData(parameters),
    parsePosition(parameters, state),
    parseElements(parameters, MATERIAL, ::parseMaterialId),
)

fun parseRegionData(parameters: Parameters) = when (parse(parameters, TYPE, RegionDataType.Undefined)) {
    RegionDataType.Battlefield -> Battlefield(
        parseOptionalBattleId(parameters, BATTLE),
    )

    RegionDataType.Continent -> Continent
    RegionDataType.Desert -> Desert
    RegionDataType.Forrest -> Forrest
    RegionDataType.Lake -> Lake
    RegionDataType.Mountain -> Mountain
    RegionDataType.Sea -> Sea
    RegionDataType.Undefined -> UndefinedRegionData
    RegionDataType.Wasteland -> Wasteland(
        parseOptionalCatastropheId(parameters, CATASTROPHE),
    )

}
