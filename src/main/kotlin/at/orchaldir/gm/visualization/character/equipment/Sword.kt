package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.OneHandedSword
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.HELD_EQUIPMENT_LAYER

data class SwordConfig(
    val length: Factor,
    val width: Factor,
    val spearHeadBase: Factor,
    val boundPadding: Factor,
    val boundRowThickness: Factor,
    val socketedPadding: Factor,
) {
    fun getLength(aabb: AABB) = aabb.convertHeight(length)
    fun getWidth(aabb: AABB) = aabb.convertHeight(width)
}

fun visualizeSword(
    state: CharacterRenderState,
    body: Body,
    blade: Blade,
    hilt: SwordHilt,
    isOneHanded: Boolean,
    set: Set<BodySlot>,
) {

}
