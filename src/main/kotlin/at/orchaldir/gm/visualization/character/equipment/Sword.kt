package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.utils.convert
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.visualization.character.CharacterRenderState

data class SwordConfig(
    val gripLength: Factor,
    val gripWidth: Factor,
) {

    fun gripSize(handRadius: Distance, isOneHanded: Boolean): Size2d {
        val length = handRadius * gripLength *
                isOneHanded.convert(1, 2)

        return Size2d(length * gripWidth, length)
    }


}

fun visualizeSword(
    state: CharacterRenderState,
    body: Body,
    blade: Blade,
    hilt: SwordHilt,
    isOneHanded: Boolean,
    set: Set<BodySlot>,
) {
    val (leftHand, rightHand) = state.config.body.getMirroredArmPoint(state.aabb, body, END)
    val handRadius = state.aabb.convertHeight(state.config.body.getHandRadius(body))
    val config = state.config.equipment.sword
    val bladeSize = blade.size(state.aabb)
    val gripSize = config.gripSize(handRadius, isOneHanded)
    val center = state.getCenter(leftHand, rightHand, set, BodySlot.HeldInRightHand)
}
