package at.orchaldir.gm.app.html.character

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.EQUIPMENT
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.money.fieldPrice
import at.orchaldir.gm.app.html.rpg.combat.showMeleeAttackTable
import at.orchaldir.gm.app.html.rpg.combat.showProtectionTable
import at.orchaldir.gm.app.html.rpg.combat.showRangedAttackTable
import at.orchaldir.gm.app.html.util.color.parseOptionalColorSchemeId
import at.orchaldir.gm.app.html.util.math.fieldWeight
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.rpg.statblock.Statblock
import at.orchaldir.gm.core.model.util.OneOrNone
import at.orchaldir.gm.core.model.util.render.ColorSchemeId
import at.orchaldir.gm.core.selector.character.getMeleeAttacks
import at.orchaldir.gm.core.selector.character.getProtection
import at.orchaldir.gm.core.selector.character.getRangedAttacks
import at.orchaldir.gm.core.selector.item.equipment.VOLUME_CONFIG
import at.orchaldir.gm.core.selector.item.equipment.calculatePrice
import at.orchaldir.gm.core.selector.item.equipment.calculateWeight
import at.orchaldir.gm.core.selector.item.equipment.getEquipmentOf
import at.orchaldir.gm.core.selector.rpg.statblock.resolveMeleeAttackMap
import at.orchaldir.gm.core.selector.rpg.statblock.resolveProtectionMap
import at.orchaldir.gm.core.selector.rpg.statblock.resolveRangedAttackMap
import at.orchaldir.gm.core.selector.util.getColorSchemeIds
import at.orchaldir.gm.core.selector.util.getColorSchemes
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag
import kotlinx.html.br

// show

fun HtmlBlockTag.showEquipped(
    call: ApplicationCall,
    state: State,
    statblock: Statblock,
    equipmentMap: EquipmentIdMap,
    label: String = "Equipped"
) {
    showDetails(label, true) {
        showEquipmentMap(call, state, label, equipmentMap)

        showEquipmentMapData(call, state, statblock, equipmentMap)
    }
}

fun HtmlBlockTag.showEquipmentMap(
    call: ApplicationCall,
    state: State,
    label: String,
    equipmentMap: EquipmentIdMap,
) {
    fieldList(label, equipmentMap.getEquipmentWithSlotSets()) { (pair, slotSets) ->
        link(call, state, pair.first)
        if (pair.second != null) {
            +" ("
            link(call, state, pair.second!!)
            +")"
        }

        if (slotSets.size > 1) {
            showList(slotSets) { slots ->
                +slots.joinToString()
            }
        }
    }
}

fun DETAILS.showEquipmentMapData(
    call: ApplicationCall,
    state: State,
    statblock: Statblock,
    equipmentMap: EquipmentIdMap,
) {
    fieldPrice(call, state, "Total Price", calculatePrice(state, VOLUME_CONFIG, equipmentMap))
    fieldWeight("Total Weight", calculateWeight(state, VOLUME_CONFIG, equipmentMap))

    val meleeAttackMap = getMeleeAttacks(state, equipmentMap)
    val protectionMap = getProtection(state, equipmentMap)
    val rangedAttackMap = getRangedAttacks(state, equipmentMap)

    val resolvedMeleeAttackMap = resolveMeleeAttackMap(state, statblock, meleeAttackMap)
    val resolvedRangedAttackMap = resolveRangedAttackMap(state, statblock, rangedAttackMap)
    val resolvedProtectionMap = resolveProtectionMap(state, statblock, protectionMap)

    showMeleeAttackTable(call, state, resolvedMeleeAttackMap)
    br { }
    showRangedAttackTable(call, state, resolvedRangedAttackMap)
    br { }
    showProtectionTable(call, state, resolvedProtectionMap)
}

// edit

fun HtmlBlockTag.editEquipped(
    call: ApplicationCall,
    state: State,
    statblock: Statblock,
    equipmentMap: EquipmentIdMap,
    param: String = EQUIPMENT,
    label: String = "Equipment"
) {
    showDetails(label, true) {
        EquipmentAppearanceType.entries.forEach { selectEquipment(state, equipmentMap, it, param) }

        showEquipmentMapData(call, state, statblock, equipmentMap)
    }
}

fun HtmlBlockTag.editEquipmentMap(
    state: State,
    equipmentMap: EquipmentIdMap,
    param: String = EQUIPMENT,
) {
    EquipmentAppearanceType.entries.forEach { selectEquipment(state, equipmentMap, it, param) }
}

private fun HtmlBlockTag.selectEquipment(
    state: State,
    equipmentMap: EquipmentIdMap,
    type: EquipmentAppearanceType,
    param: String,
) {
    // ignore fashion for testing
    val options = OneOrNone(state.getEquipmentOf(type).map { it.id })

    if (options.isEmpty()) {
        return
    }

    showDetails(type.name, true) {
        type.slots().getAllBodySlotCombinations().forEach { bodySlots ->
            val isSlotFree = equipmentMap.isFree(bodySlots)
            val currentPair = equipmentMap.getEquipment(bodySlots)
            val currentId = currentPair?.first
            val optionalEquipment = state.getEquipmentStorage().getOptional(currentId)
            val isOccupyingSlot = optionalEquipment?.appearance?.isType(type) ?: false
            val isIounStoneSlotForbidden = when (type) {
                EquipmentAppearanceType.IounStone -> {
                    val bodySlot = bodySlots.first()
                    val bodySlotIndex = bodySlot.getIounStoneIndex()
                    equipmentMap.getMaxIounStoneSlot()?.let { maxSlot ->
                        bodySlotIndex > maxSlot.getIounStoneIndex() + 1
                    } ?: (bodySlot != BodySlot.IounStone0)
                }

                else -> false
            }
            val isAvailable = (isSlotFree || isOccupyingSlot) && !isIounStoneSlotForbidden
            val text = bodySlots.joinToString(" & ")

            if (isAvailable) {
                val slotsParam = param + bodySlots.joinToString("_")
                val currentSchema = currentPair?.second

                selectFromOneOrNone(
                    text,
                    slotsParam,
                    options,
                    false,
                ) { id ->
                    val equipment = state.getEquipmentStorage().getOrThrow(id)
                    label = equipment.name.text
                    value = id.value.toString()
                    selected = id == currentId
                }

                if (optionalEquipment != null) {
                    selectOptionalElement(
                        state,
                        "Color Scheme",
                        combine(COLOR, slotsParam),
                        state.getColorSchemes(optionalEquipment.colorSchemes),
                        currentSchema,
                    )
                }
            } else if (isIounStoneSlotForbidden) {
                field(text, "Ioun Stone Slots need to be filled in order")
            } else {
                field(text, "Slot(s) are occupied")
            }
        }
    }
}

// parse

fun parseEquipmentMap(
    state: State,
    parameters: Parameters,
    param: String = EQUIPMENT,
): EquipmentIdMap {
    val map = mutableMapOf<EquipmentIdPair, MutableSet<Set<BodySlot>>>()

    parameters.forEach { parameter, ids ->
        if (parameter.startsWith(param)) {
            val slotsString = parameter.removePrefix(param)
            val scheme = parseOptionalColorSchemeId(parameters, combine(COLOR, parameter))
            tryParse(state, map, slotsString, ids, scheme)
        }
    }

    return EquipmentMap.fromSlotAsValueMap(map)
}

private fun tryParse(
    state: State,
    map: MutableMap<EquipmentIdPair, MutableSet<Set<BodySlot>>>,
    slotsString: String,
    ids: List<String>,
    optionalScheme: ColorSchemeId?,
) {
    val filteredIds = ids.filter { it.isNotEmpty() }
    require(filteredIds.size <= 1) { "Slots $slotsString has too many items!" }
    val id = EquipmentId(filteredIds.firstOrNull()?.toInt() ?: return)
    val equipment = state.getEquipmentStorage().getOrThrow(id)
    val scheme = if (!equipment.colorSchemes.isEmpty() && optionalScheme == null) {
        state.getColorSchemeIds(equipment.colorSchemes).first()
    } else {
        optionalScheme
    }
    val pair = Pair(id, scheme)

    val slots = slotsString.split("_")
        .map { BodySlot.valueOf(it) }
        .toSet()

    map.computeIfAbsent(pair) { mutableSetOf() }
        .add(slots)
}