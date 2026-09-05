package at.orchaldir.gm.app.html.item.ammunition

import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.money.parsePriceLookup
import at.orchaldir.gm.app.html.economy.money.selectPriceLookup
import at.orchaldir.gm.app.html.economy.money.showPriceLookupDetails
import at.orchaldir.gm.app.html.rpg.equipment.parseAmmunitionTypeId
import at.orchaldir.gm.app.html.rpg.equipment.parseEquipmentModifiers
import at.orchaldir.gm.app.html.rpg.equipment.selectEquipmentModifier
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
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentModifierCategory
import at.orchaldir.gm.core.selector.gm.treasure.getTreasureParcelsWith
import at.orchaldir.gm.core.selector.util.sortAmmunitionTypes
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.unit.VolumePerMaterial
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

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

    showUsage(call, state, ammunition.id)
}

private fun HtmlBlockTag.showUsage(
    call: ApplicationCall,
    state: State,
    id: AmmunitionId,
) {
    val parcels = state.getTreasureParcelsWith(id)

    if (parcels.isEmpty()) {
        return
    }

    h2 { +"Usage" }

    fieldElements(call, state, parcels)
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

fun parseOptionalAmmunitionId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { AmmunitionId(it) }

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