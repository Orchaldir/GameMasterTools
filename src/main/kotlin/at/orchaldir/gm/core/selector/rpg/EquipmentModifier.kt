package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.EquipmentModifierId
import at.orchaldir.gm.core.selector.item.getArmors
import at.orchaldir.gm.core.selector.item.getShields

fun State.canDeleteEquipmentModifier(modifier: EquipmentModifierId) = DeleteResult(modifier)
    .addElements(getArmors(modifier))
    .addElements(getShields(modifier))
