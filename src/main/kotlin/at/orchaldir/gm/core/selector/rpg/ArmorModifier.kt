package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.ArmorModifierId
import at.orchaldir.gm.core.selector.item.getArmors

fun State.canDeleteArmorModifier(modifier: ArmorModifierId) = DeleteResult(modifier)
    .addElements(getArmors(modifier))
