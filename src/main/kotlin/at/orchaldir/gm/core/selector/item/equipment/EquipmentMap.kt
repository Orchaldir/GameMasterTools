package at.orchaldir.gm.core.selector.item.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.item.Uniform
import at.orchaldir.gm.core.model.item.UniformId
import at.orchaldir.gm.core.model.item.equipment.EquipmentIdMap
import at.orchaldir.gm.core.model.item.equipment.EquipmentMapUpdate
import at.orchaldir.gm.core.model.item.equipment.convert
import at.orchaldir.gm.core.model.rpg.statblock.StatblockLookup
import at.orchaldir.gm.core.model.rpg.statblock.UndefinedStatblockLookup
import at.orchaldir.gm.core.model.util.render.UndefinedColors

fun State.getEquipmentElementMap(character: CharacterId) =
    getEquipmentElementMap(getCharacterStorage().getOrThrow(character))

fun State.getEquipmentElementMap(character: Character) = getEquipmentElementMap(character.equipped, character.statblock)
fun State.getEquipmentElementMap(template: CharacterTemplate) = getEquipmentElementMap(template.equipped, template.statblock)
fun State.getEquipmentElementMap(uniform: Uniform) = getEquipmentElementMap(uniform.equipped, UndefinedStatblockLookup)

fun State.getEquipmentElementMap(
    equipped: Equipped,
    lookup: StatblockLookup,
) = resolveEquipmentMap(getEquipmentIdMap(equipped, lookup))

fun State.getEquipmentIdMap(
    equipped: Equipped,
    lookup: StatblockLookup,
): EquipmentIdMap = when (equipped) {
    is UniqueEquipment -> equipped.map
    is UseUniform -> getEquipmentIdMap(equipped.uniform)
    is ModifyUniform -> {
        val uniform = getEquipmentIdMap(equipped.uniform)
        equipped.update.applyTo(uniform)
    }

    is UseEquipmentFromTemplate -> getEquipmentIdMapForLookup(lookup)
    is ModifyEquipmentFromTemplate -> {
        getEquipmentIdMap(equipped.update, lookup)
    }

    UndefinedEquipped -> EquipmentIdMap()
}

fun State.getEquipmentIdMap(
    update: EquipmentMapUpdate,
    lookup: StatblockLookup,
) = update.applyTo(getEquipmentIdMapForLookup(lookup))

fun State.getEquipmentIdMapForLookup(lookup: StatblockLookup): EquipmentIdMap {
    val templateId = lookup.template() ?: return EquipmentIdMap()
    val template = getCharacterTemplateStorage().getOrThrow(templateId)
    return getEquipmentIdMap(template.equipped, template.statblock)
}

fun State.getEquipmentIdMap(character: Character) =
    getEquipmentIdMap(character.equipped, character.statblock)

fun State.getEquipmentIdMap(template: CharacterTemplate) =
    getEquipmentIdMap(template.equipped, template.statblock)

fun State.getEquipmentIdMap(uniformId: UniformId) =
    getEquipmentIdMap(getUniformStorage().getOrThrow(uniformId))

fun State.getEquipmentIdMap(uniform: Uniform) =
    getEquipmentIdMap(uniform.equipped, UndefinedStatblockLookup)

fun State.resolveEquipmentMap(idMap: EquipmentIdMap) = idMap.convert { pair ->
    Pair(
        getEquipmentStorage().getOrThrow(pair.first).data,
        getColorSchemeStorage().getOptional(pair.second)?.data ?: UndefinedColors,
    )
}
