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

    fun gripAabb(
        state: CharacterRenderState,
        body: Body,
        isOneHanded: Boolean,
        hand: Point2d,
    ): AABB {
        val handRadius = state.aabb.convertHeight(state.config.body.getHandRadius(body))
        val oneHandLength = handRadius * gripLength
        val length = oneHandLength * isOneHanded.convert(1, 2)
        val center = hand.addHeight(oneHandLength / 2)
        val size = Size2d(length * gripWidth, length)

        return AABB(center, size)
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
    val hand = state.getCenter(leftHand, rightHand, set, BodySlot.HeldInRightHand)
    val config = state.config.equipment.sword
    val gripAabb = config.gripAabb(state, body, isOneHanded, hand)
    val bladeSize = blade.size(state.aabb)
    val bladeAabb = AABB.fromBottom(gripAabb.getPoint(CENTER, START), bladeSize)
}
