package at.orchaldir.gm.app.html.world

import at.orchaldir.gm.app.ENCOUNTER
import at.orchaldir.gm.app.MATERIAL
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.ecology.editEcology
import at.orchaldir.gm.app.html.ecology.parseEcology
import at.orchaldir.gm.app.html.ecology.showEcology
import at.orchaldir.gm.app.html.economy.material.parseMaterialId
import at.orchaldir.gm.app.html.rpg.encounter.editEncounterEntry
import at.orchaldir.gm.app.html.rpg.encounter.parseEncounterEntry
import at.orchaldir.gm.app.html.rpg.encounter.showEncounterEntry
import at.orchaldir.gm.app.html.util.fieldPosition
import at.orchaldir.gm.app.html.util.parsePosition
import at.orchaldir.gm.app.html.util.selectPosition
import at.orchaldir.gm.app.html.util.showLocalElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.Region
import at.orchaldir.gm.core.model.world.terrain.RegionId
import at.orchaldir.gm.core.selector.world.getSettlementMaps
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
    showEcology(call, state, region.ecology)
    fieldPosition(call, state, region.position)
    fieldIds(call, state, "Resources", region.resources)
    showEncounterEntry(call, state, region.encounter)
    fieldElements(call, state, state.getSettlementMaps(region.id))
    showLocalElements(call, state, region.id)
}

// edit

fun HtmlBlockTag.editRegion(
    call: ApplicationCall,
    state: State,
    region: Region,
) {
    selectName(region.name)
    editRegionData(state, region.data, null)
    selectPosition(
        state,
        region.position,
        null,
        region.data.getAllowedRegionTypes(),
    )
    editEcology(state, region.ecology)
    selectElements(state, "Resources", MATERIAL, state.getMaterialStorage().getAll(), region.resources)
    editEncounterEntry(state, region.encounter, ENCOUNTER, null)
}


// parse

fun parseRegionId(parameters: Parameters, param: String) = RegionId(parseInt(parameters, param))
fun parseOptionalRegionId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { RegionId(it) }

fun parseRegion(state: State, parameters: Parameters, id: RegionId) = Region(
    id,
    parseName(parameters),
    parseRegionData(parameters),
    parsePosition(parameters, state),
    parseEcology(parameters),
    parseElements(parameters, MATERIAL, ::parseMaterialId),
    parseEncounterEntry(parameters, ENCOUNTER),
)

