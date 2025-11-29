package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.EquipmentIdMap
import at.orchaldir.gm.core.model.item.equipment.getAllBodySlotCombinations
import at.orchaldir.gm.core.model.rpg.CharacterStatblock
import at.orchaldir.gm.core.model.rpg.UndefinedCharacterStatblock
import at.orchaldir.gm.core.model.rpg.UniqueCharacterStatblock
import at.orchaldir.gm.core.model.rpg.UseStatblockOfTemplate
import at.orchaldir.gm.core.model.util.VALID_CAUSES_FOR_CHARACTERS
import at.orchaldir.gm.core.model.util.VALID_VITAL_STATUS_FOR_CHARACTERS
import at.orchaldir.gm.core.reducer.rpg.validateStatblock
import at.orchaldir.gm.core.reducer.util.*
import at.orchaldir.gm.utils.doNothing

fun validateEquipped(
    state: State,
    equipped: Equipped,
) = when (equipped) {
    is EquippedEquipment -> validateCharacterEquipment(state, equipped.map)
    is EquippedUniform -> state.getUniformStorage().require(equipped.uniform)
    UndefinedEquipped -> doNothing()
}

fun validateCharacterEquipment(
    state: State,
    equipmentMap: EquipmentIdMap,
) {
    val occupySlots = mutableSetOf<BodySlot>()

    equipmentMap.getEquipmentWithSlotSets().forEach { (pair, slotSets) ->
        val equipment = state.getEquipmentStorage().getOrThrow(pair.first)
        val allowedSlotSets = equipment.data.slots().getAllBodySlotCombinations()

        state.getColorSchemeStorage().requireOptional(pair.second)

        slotSets.forEach { slotSet ->
            // Not sure why allowedSlotSets.contains(slotSet) doesn't work
            val contained = allowedSlotSets.any { allowedSet -> allowedSet == slotSet }
            require(contained) { "Equipment ${equipment.id.value} uses wrong slots!" }

            slotSet.forEach { slot -> require(!occupySlots.contains(slot)) { "Body slot $slot is occupied multiple times!" } }

            occupySlots.addAll(slotSet)
        }
    }
}