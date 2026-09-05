package at.orchaldir.gm.core.selector.rpg.combat

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentModifierCategory
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentModifierId
import at.orchaldir.gm.core.selector.item.ammunition.getAmmunition
import at.orchaldir.gm.core.selector.item.equipment.getEquipment

fun State.canDeleteEquipmentModifier(modifier: EquipmentModifierId) = DeleteResult(modifier)
    .addElements(getAmmunition(modifier))
    .addElements(getEquipment(modifier))

fun State.getEquipmentModifier(category: EquipmentModifierCategory) = getEquipmentModifierStorage()
    .getAll()
    .filter { it.category.contains(category) }
