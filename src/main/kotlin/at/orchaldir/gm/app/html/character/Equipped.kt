package at.orchaldir.gm.app.html.character

import at.orchaldir.gm.app.EQUIPMENT
import at.orchaldir.gm.app.UNIFORM
import at.orchaldir.gm.app.UPDATE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.item.parseUniformId
import at.orchaldir.gm.app.html.rpg.combat.showMeleeAttackTable
import at.orchaldir.gm.app.html.rpg.combat.showProtectionTable
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.rpg.statblock.Statblock
import at.orchaldir.gm.core.model.rpg.statblock.StatblockLookup
import at.orchaldir.gm.core.selector.character.getArmors
import at.orchaldir.gm.core.selector.character.getMeleeAttacks
import at.orchaldir.gm.core.selector.character.getShields
import at.orchaldir.gm.core.selector.item.getEquipmentMapOfTemplate
import at.orchaldir.gm.core.selector.rpg.statblock.resolveMeleeAttackMap
import at.orchaldir.gm.core.selector.rpg.statblock.resolveProtectionMap
import at.orchaldir.gm.core.selector.util.sortUniforms
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.br

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
        is UseEquipmentFromTemplate -> +"Use Template"
        is ModifyEquipmentFromTemplate -> +"Modify Template"
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
    showDetails("Equipped", true) {
        field("Type", equipped.getType())

        when (equipped) {
            is EquippedEquipment -> showEquipmentMap(call, state, "Equipment", equipped.map)
            is EquippedUniform -> fieldLink(call, state, equipped.uniform)
            is UseEquipmentFromTemplate -> doNothing()
            is ModifyEquipmentFromTemplate -> showEquipmentMapUpdate(
                call,
                state,
                state.getEquipmentMapOfTemplate(lookup),
                equipped.update,
            )
            UndefinedEquipped -> doNothing()
        }

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
                EquippedType.Equipment -> false
                EquippedType.UseTemplate, EquippedType.ModifyTemplate -> state.getCharacterTemplateStorage().isEmpty()
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

            is UseEquipmentFromTemplate -> doNothing()
            is ModifyEquipmentFromTemplate -> editEquipmentMapUpdate(
                call,
                state,
                state.getEquipmentMapOfTemplate(lookup),
                equipped.update,
                combine(param, UPDATE),
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

        EquippedType.UseTemplate -> UseEquipmentFromTemplate
        EquippedType.ModifyTemplate -> ModifyEquipmentFromTemplate(
            parseEquipmentMapUpdate(parameters, combine(param, UPDATE)),
        )

        EquippedType.Uniform -> EquippedUniform(
            parseUniformId(parameters, combine(param, UNIFORM)),
        )

    }