package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.AMMUNITION
import at.orchaldir.gm.app.MODIFIER
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.money.parsePriceLookup
import at.orchaldir.gm.app.html.economy.money.selectPriceLookup
import at.orchaldir.gm.app.html.economy.money.showPriceLookupDetails
import at.orchaldir.gm.app.html.rpg.combat.*
import at.orchaldir.gm.app.html.util.math.parseWeightLookup
import at.orchaldir.gm.app.html.util.math.selectWeightLookup
import at.orchaldir.gm.app.html.util.math.showWeightLookupDetails
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.selector.item.equipment.CalculateVolumeConfig
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showAmmunition(
    call: ApplicationCall,
    state: State,
    ammunition: Ammunition,
) {
    val costFactors = calculateCostFactors(state, ammunition.data)
    val vpm = calculateVolumePerMaterial(CalculateVolumeConfig.from(CHARACTER_CONFIG), ammunition.data)

    fieldLink("Type", call, state, ammunition.type)
    fieldIds(call, state, "Modifiers", ammunition.modifiers)
    showWeightLookupDetails(call, state, ammunition.weight, vpm)
    showPriceLookupDetails(call, state, ammunition.price, vpm, costFactors)
}

// edit

fun HtmlBlockTag.editAmmunition(
    call: ApplicationCall,
    state: State,
    ammunition: Ammunition,
) {
    selectName(ammunition.name)
    selectElements(
        state,
        "Modifiers",
        combine(AMMUNITION, MODIFIER),
        state.getEquipmentModifierStorage().getAll(),
        ammunition.modifiers,
    )
    selectWeightLookup(state, ammunition.weight, MIN_EQUIPMENT_WEIGHT, MAX_EQUIPMENT_WEIGHT)
    selectPriceLookup(state, ammunition.price, MIN_EQUIPMENT_PRICE, MAX_EQUIPMENT_PRICE)
}

// parse

fun parseAmmunitionId(value: String) = AmmunitionId(value.toInt())

fun parseAmmunitionId(parameters: Parameters, param: String) = AmmunitionId(parseInt(parameters, param))

fun parseAmmunition(
    state: State,
    parameters: Parameters,
    id: AmmunitionId,
) = Ammunition(
    id,
    parseName(parameters),
    parseAmmunitionTypeId(parameters, TYPE),
    parseElements(
        parameters,
        combine(AMMUNITION, MODIFIER),
        ::parseEquipmentModifierId,
    ),
    parseWeightLookup(parameters, MIN_EQUIPMENT_WEIGHT),
    parsePriceLookup(state, parameters),
)