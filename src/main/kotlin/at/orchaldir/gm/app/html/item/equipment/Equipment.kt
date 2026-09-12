package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.SCHEME
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.money.parsePriceLookup
import at.orchaldir.gm.app.html.economy.money.selectPriceLookup
import at.orchaldir.gm.app.html.economy.money.showPriceLookupDetails
import at.orchaldir.gm.app.html.rpg.equipment.editEquipmentStats
import at.orchaldir.gm.app.html.rpg.equipment.parseEquipmentStats
import at.orchaldir.gm.app.html.rpg.equipment.showEquipmentStats
import at.orchaldir.gm.app.html.util.color.editColorSchemeOption
import at.orchaldir.gm.app.html.util.color.fieldColorSchemeOption
import at.orchaldir.gm.app.html.util.color.parseColorSchemeOption
import at.orchaldir.gm.app.html.util.math.parseWeightLookup
import at.orchaldir.gm.app.html.util.math.selectWeightLookup
import at.orchaldir.gm.app.html.util.math.showWeightLookupDetails
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.selector.character.getCharacterTemplates
import at.orchaldir.gm.core.selector.character.getCharactersWith
import at.orchaldir.gm.core.selector.culture.getFashions
import at.orchaldir.gm.core.selector.gm.treasure.getTreasureParcelsWith
import at.orchaldir.gm.core.selector.item.equipment.CalculateVolumeConfig
import at.orchaldir.gm.core.selector.item.equipment.calculateCostFactors
import at.orchaldir.gm.core.selector.item.equipment.calculateVolumePerMaterial
import at.orchaldir.gm.core.selector.item.equipment.calculateWeightBasedOnType
import at.orchaldir.gm.core.selector.item.getUniforms
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showEquipment(
    call: ApplicationCall,
    state: State,
    equipment: Equipment,
) {
    val material = equipment.data.mainMaterial()
    val costFactors = calculateCostFactors(state, equipment.stats)
    val vpm = calculateVolumePerMaterial(CalculateVolumeConfig.from(CHARACTER_CONFIG), equipment.data)

    showEquipmentStats(call, state, equipment.stats, material)
    showEquipmentData(call, state, equipment.data)
    fieldColorSchemeOption(call, state, equipment.colorSchemes)
    showWeightLookupDetails(
        call,
        state,
        equipment.weight,
        vpm,
    ) {
        calculateWeightBasedOnType(state, equipment)
    }
    showPriceLookupDetails(call, state, equipment.price, vpm, costFactors)
    showUsages(call, state, equipment.id)
}

private fun HtmlBlockTag.showUsages(
    call: ApplicationCall,
    state: State,
    equipment: EquipmentId,
) {
    val characters = state.getCharactersWith(equipment)
    val characterTemplates = state.getCharacterTemplates(equipment)
    val fashions = state.getFashions(equipment)
    val parcels = state.getTreasureParcelsWith(equipment)
    val uniforms = state.getUniforms(equipment)

    if (characters.isEmpty() && characterTemplates.isEmpty() && fashions.isEmpty() && parcels.isEmpty() && uniforms.isEmpty()) {
        return
    }

    h2 { +"Usage" }

    fieldElements(call, state, characters)
    fieldElements(call, state, characterTemplates)
    fieldElements(call, state, fashions)
    fieldElements(call, state, parcels)
    fieldElements(call, state, uniforms)
}

// edit

fun HtmlBlockTag.editEquipment(
    call: ApplicationCall,
    state: State,
    equipment: Equipment,
) {
    selectName(equipment.name)
    editEquipmentStats(call, state, equipment.stats)
    editEquipmentData(state, equipment.data)
    selectColorSchemes(state, equipment)
    selectWeightLookup(equipment.weight, MIN_EQUIPMENT_WEIGHT, MAX_EQUIPMENT_WEIGHT)
    selectPriceLookup(state, equipment.price, MIN_EQUIPMENT_PRICE, MAX_EQUIPMENT_PRICE)
}

private fun HtmlBlockTag.selectColorSchemes(
    state: State,
    equipment: Equipment,
) {
    val requiredSchemaColors = equipment.data.requiredSchemaColors()

    if (requiredSchemaColors > 0) {
        editColorSchemeOption(
            state,
            equipment.colorSchemes,
            combine(COLOR, SCHEME),
        )
    }
}

// parse

fun parseEquipmentId(value: String) = EquipmentId(value.toInt())

fun parseEquipmentId(parameters: Parameters, param: String) = EquipmentId(parseInt(parameters, param))

fun parseOptionalEquipmentId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { EquipmentId(it) }

fun parseEquipment(
    state: State,
    parameters: Parameters,
    id: EquipmentId,
): Equipment {
    val data = parseEquipmentData(state, parameters)

    return Equipment(
        id,
        parseName(parameters),
        parseEquipmentStats(parameters),
        data,
        parseWeightLookup(parameters, MIN_EQUIPMENT_WEIGHT),
        parsePriceLookup(state, parameters),
        parseColorSchemeOption(parameters, combine(COLOR, SCHEME)),
    )
}
