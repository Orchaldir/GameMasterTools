package at.orchaldir.gm.app.html.character

import at.orchaldir.gm.app.EQUIPMENT
import at.orchaldir.gm.app.UNIFORM
import at.orchaldir.gm.app.UPDATE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.money.fieldPrice
import at.orchaldir.gm.app.html.item.parseUniformId
import at.orchaldir.gm.app.html.rpg.combat.showMeleeAttackTable
import at.orchaldir.gm.app.html.rpg.combat.showProtectionTable
import at.orchaldir.gm.app.html.util.math.fieldWeight
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.item.UniformId
import at.orchaldir.gm.core.model.item.equipment.EquipmentIdMap
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.rpg.statblock.Statblock
import at.orchaldir.gm.core.model.rpg.statblock.StatblockLookup
import at.orchaldir.gm.core.selector.character.getArmors
import at.orchaldir.gm.core.selector.character.getMeleeAttacks
import at.orchaldir.gm.core.selector.character.getShields
import at.orchaldir.gm.core.selector.item.equipment.VOLUME_CONFIG
import at.orchaldir.gm.core.selector.item.equipment.calculatePrice
import at.orchaldir.gm.core.selector.item.equipment.calculateWeight
import at.orchaldir.gm.core.selector.item.equipment.getEquipmentMap
import at.orchaldir.gm.core.selector.item.equipment.getEquipmentMapForLookup
import at.orchaldir.gm.core.selector.rpg.statblock.resolveMeleeAttackMap
import at.orchaldir.gm.core.selector.rpg.statblock.resolveProtectionMap
import at.orchaldir.gm.core.selector.util.sortUniforms
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag
import kotlinx.html.br

// show

fun HtmlBlockTag.showEquipped(
    call: ApplicationCall,
    state: State,
    equipped: Equipped,
    lookup: StatblockLookup,
    showUndefined: Boolean = false,
) {
    when (equipped) {
        is UniqueEquipment -> +"${equipped.map.size()} items"
        is UseUniform -> link(call, state, equipped.uniform)
        is ModifyUniform -> {
            +"Modify "
            link(call, state, equipped.uniform)
        }

        is UseEquipmentFromTemplate -> {
            +"Use "
            optionalLink(call, state, lookup.template())
        }

        is ModifyEquipmentFromTemplate -> {
            +"Modify "
            optionalLink(call, state, lookup.template())
        }

        UndefinedEquipped -> if (showUndefined) {
            +"Undefined"
        }
    }
}

fun HtmlBlockTag.showEquippedDetails(
    call: ApplicationCall,
    state: State,
    equipped: Equipped,
    race: RaceId,
    lookup: StatblockLookup,
) = showEquippedDetails(
    call,
    state,
    equipped,
    state.getRaceStorage().getOrThrow(race).lifeStages.statblock(),
    lookup,
)

fun HtmlBlockTag.showEquippedDetails(
    call: ApplicationCall,
    state: State,
    equipped: Equipped,
    base: Statblock,
    lookup: StatblockLookup,
) {
    val equipmentMap = state.getEquipmentMap(equipped, lookup)

    showDetails("Equipped", true) {
        field("Type", equipped.getType())

        when (equipped) {
            is UniqueEquipment -> showEquipmentMap(call, state, "Equipment", equipped.map)
            is UseUniform -> fieldLink(call, state, equipped.uniform)
            is ModifyUniform -> {
                fieldLink(call, state, equipped.uniform)
                showEquipmentMapUpdate(
                    call,
                    state,
                    equipmentMap,
                    equipped.update,
                )
            }

            is UseEquipmentFromTemplate -> doNothing()
            is ModifyEquipmentFromTemplate -> showEquipmentMapUpdate(
                call,
                state,
                state.getEquipmentMapForLookup(lookup),
                equipped.update,
            )

            UndefinedEquipped -> doNothing()
        }

        fieldPrice(call, state, "Total Price", calculatePrice(state, VOLUME_CONFIG, equipmentMap))
        fieldWeight("Total Weight", calculateWeight(state, VOLUME_CONFIG, equipmentMap))

        val amorMap = getArmors(state, equipped, lookup)
        val meleeAttackMap = getMeleeAttacks(state, equipped, lookup)
        val shieldMap = getShields(state, equipped, lookup)

        val resolvedMeleeAttackMap = resolveMeleeAttackMap(state, base, lookup, meleeAttackMap)
        val resolvedProtectionMap = resolveProtectionMap(state, lookup, amorMap + shieldMap)

        showMeleeAttackTable(call, state, resolvedMeleeAttackMap)
        br { }
        showProtectionTable(call, state, resolvedProtectionMap)
    }
}

// select

fun HtmlBlockTag.editEquipped(
    call: ApplicationCall,
    state: State,
    param: String,
    equipped: Equipped,
    lookup: StatblockLookup,
    elementId: UniformId? = null,
) {
    val allowedTypes = if (lookup.hasTemplate()) {
        EquippedType.entries
    } else {
        EquippedType.entries - EquippedType.UseTemplate - EquippedType.ModifyTemplate
    }

    showDetails("Equipped", true) {
        selectValue("Type", param, allowedTypes, equipped.getType()) { type ->
            when (type) {
                EquippedType.Undefined -> false
                EquippedType.Unique -> false
                EquippedType.UseTemplate, EquippedType.ModifyTemplate -> state.getCharacterTemplateStorage().isEmpty()
                EquippedType.UseUniform, EquippedType.ModifyUniform -> state.getUniformStorage()
                    .isEmptyWithout(elementId)
            }
        }

        when (equipped) {
            is UniqueEquipment -> editEquipmentMap(
                state,
                equipped.map,
                combine(param, EQUIPMENT),
            )

            is UseUniform -> selectLookedUpUniform(state, param, equipped.uniform, elementId)

            is ModifyUniform -> {
                selectLookedUpUniform(state, param, equipped.uniform, elementId)
                editEquipmentMapUpdate(
                    call,
                    state,
                    state.getEquipmentMap(equipped.uniform),
                    equipped.update,
                    combine(param, UPDATE),
                )
            }

            is UseEquipmentFromTemplate -> doNothing()
            is ModifyEquipmentFromTemplate -> editEquipmentMapUpdate(
                call,
                state,
                state.getEquipmentMapForLookup(lookup),
                equipped.update,
                combine(param, UPDATE),
            )

            UndefinedEquipped -> doNothing()
        }
    }
}

private fun DETAILS.selectLookedUpUniform(
    state: State,
    param: String,
    lookedUpUniform: UniformId,
    element: UniformId? = null,
) {
    selectElement(
        state,
        combine(param, UNIFORM),
        state.sortUniforms().filter { it.id != element },
        lookedUpUniform,
    )
}

// parse

fun parseEquipped(
    parameters: Parameters,
    state: State,
    param: String,
    base: EquipmentIdMap,
) =
    when (parse(parameters, param, EquippedType.Undefined)) {
        EquippedType.Undefined -> UndefinedEquipped
        EquippedType.Unique -> UniqueEquipment(
            parseEquipmentMap(parameters, combine(param, EQUIPMENT)),
        )

        EquippedType.UseTemplate -> UseEquipmentFromTemplate
        EquippedType.ModifyTemplate -> ModifyEquipmentFromTemplate(
            parseEquipmentMapUpdate(parameters, combine(param, UPDATE), base),
        )

        EquippedType.UseUniform -> UseUniform(
            parseUniformId(parameters, combine(param, UNIFORM)),
        )

        EquippedType.ModifyUniform -> {
            val uniformId = parseUniformId(parameters, combine(param, UNIFORM))
            val uniform = state.getEquipmentMap(uniformId)

            ModifyUniform(
                uniformId,
                parseEquipmentMapUpdate(parameters, combine(param, UPDATE), uniform),
            )
        }

    }