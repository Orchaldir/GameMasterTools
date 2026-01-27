package at.orchaldir.gm.app.html.item.ammunition

import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.money.parsePriceLookup
import at.orchaldir.gm.app.html.economy.money.selectPriceLookup
import at.orchaldir.gm.app.html.economy.money.showPriceLookupDetails
import at.orchaldir.gm.app.html.rpg.combat.parseAmmunitionTypeId
import at.orchaldir.gm.app.html.rpg.combat.parseEquipmentModifiers
import at.orchaldir.gm.app.html.rpg.combat.selectEquipmentModifier
import at.orchaldir.gm.app.html.util.math.parseWeightLookup
import at.orchaldir.gm.app.html.util.math.selectWeightLookup
import at.orchaldir.gm.app.html.util.math.showWeightLookupDetails
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ammunition.Ammunition
import at.orchaldir.gm.core.model.item.ammunition.AmmunitionId
import at.orchaldir.gm.core.model.item.equipment.MAX_EQUIPMENT_PRICE
import at.orchaldir.gm.core.model.item.equipment.MAX_EQUIPMENT_WEIGHT
import at.orchaldir.gm.core.model.item.equipment.MIN_EQUIPMENT_PRICE
import at.orchaldir.gm.core.model.item.equipment.MIN_EQUIPMENT_WEIGHT
import at.orchaldir.gm.core.model.rpg.combat.EquipmentModifierCategory
import at.orchaldir.gm.core.selector.util.sortAmmunitionTypes
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.unit.VolumePerMaterial
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showAmmunition(
    call: ApplicationCall,
    state: State,
    ammunition: Ammunition,
) {
    val costFactors: Map<Id<*>, Factor> = emptyMap()
    val vpm = VolumePerMaterial()

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
    selectElement(
        state,
        "Type",
        TYPE,
        state.sortAmmunitionTypes(),
        ammunition.type,
    )
    selectEquipmentModifier(state, EquipmentModifierCategory.Ammunition, ammunition.modifiers)
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
    parseEquipmentModifiers(parameters),
    parseWeightLookup(parameters, MIN_EQUIPMENT_WEIGHT),
    parsePriceLookup(state, parameters),
)