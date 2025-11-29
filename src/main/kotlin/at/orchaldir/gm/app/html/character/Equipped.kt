package at.orchaldir.gm.app.html.character

import at.orchaldir.gm.app.EQUIPMENT
import at.orchaldir.gm.app.UNIFORM
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.item.parseUniformId
import at.orchaldir.gm.app.html.rpg.combat.showMeleeAttackTable
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.rpg.CharacterStatblock
import at.orchaldir.gm.core.selector.character.getMeleeAttacks
import at.orchaldir.gm.core.selector.rpg.resolveMeleeAttackMap
import at.orchaldir.gm.core.selector.util.sortUniforms
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showEquipped(
    call: ApplicationCall,
    state: State,
    equipped: Equipped,
    showUndefined: Boolean = false,
) {
    when (equipped) {
        is EquippedEquipment -> +"${equipped.map.size()} items"
        is EquippedUniform -> link(call, state, equipped.uniform)
        UndefinedEquipped -> if (showUndefined) {
            +"Undefined"
        }
    }
}

fun HtmlBlockTag.showEquippedDetails(
    call: ApplicationCall,
    state: State,
    equipped: Equipped,
    statblock: CharacterStatblock,
) {
    showDetails("Equipped", true) {
        field("Type", equipped.getType())

        when (equipped) {
            is EquippedEquipment -> {
                showEquipmentMap(call, state, "Equipment", equipped.map)
            }

            is EquippedUniform -> fieldLink(call, state, equipped.uniform)
            UndefinedEquipped -> doNothing()
        }

        val meleeAttackMap = getMeleeAttacks(state, equipped)
        val resolvedMeleeAttackMap = resolveMeleeAttackMap(state, statblock, meleeAttackMap)

        showMeleeAttackTable(call, state, resolvedMeleeAttackMap)
    }
}

// select

fun HtmlBlockTag.editEquipped(
    state: State,
    param: String,
    equipped: Equipped,
) {
    showDetails("Equipped", true) {
        selectValue("Type", param, EquippedType.entries, equipped.getType()) { type ->
            when (type) {
                EquippedType.Undefined -> false
                EquippedType.Equipment -> false
                EquippedType.Uniform -> state.getUniformStorage().isEmpty()
            }
        }

        when (equipped) {
            is EquippedEquipment -> editEquipmentMap(
                state,
                equipped.map,
                combine(param, EQUIPMENT),
            )
            is EquippedUniform -> selectElement(
                state,
                combine(param, UNIFORM),
                state.sortUniforms(),
                equipped.uniform,
            )
            UndefinedEquipped -> doNothing()
        }
    }
}

// parse

fun parseEquipped(parameters: Parameters, state: State, param: String) =
    when (parse(parameters, param, EquippedType.Undefined)) {
        EquippedType.Undefined -> UndefinedEquipped
        EquippedType.Equipment -> EquippedEquipment(
            parseEquipmentMap(parameters, combine(param, EQUIPMENT)),
        )
        EquippedType.Uniform -> EquippedUniform(
            parseUniformId(parameters, combine(param, UNIFORM)),
        )
    }