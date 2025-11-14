package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.HELD_EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.equipment.part.visualizeHeadFixation

data class ClubConfig(
    val broadButtHeight: SizeConfig<Factor>,
    val broadWidth: Factor,
    val broadHeight: SizeConfig<Factor>,
    val crescentButtHeight: SizeConfig<Factor>,
    val crescentWidth: Factor,
    val daggerButtHeight: SizeConfig<Factor>,
    val daggerLength: Factor,
    val daggerWidth: Factor,
    val oneHandedHeight: Factor,
    val twoHandedHeight: Factor,
    val shaftThickness: Factor,
) {
    fun shaftAabb(
        state: CharacterRenderState,
        body: Body,
        isOneHanded: Boolean,
        hand: Point2d,
    ): AABB {
        val handRadius = state.aabb.convertHeight(state.config.body.getHandRadius(body))
        val bottom = hand.addHeight(handRadius * 2)
        val heightFactor = if (isOneHanded) {
            oneHandedHeight
        } else {
            twoHandedHeight
        }
        val height = state.aabb.convertHeight(heightFactor)
        val width = state.aabb.convertHeight(shaftThickness)

        return AABB.fromBottom(bottom, Size2d(width, height))
    }

    fun createBroadClubBladeAabb(
        size: Size,
        shaftAabb: AABB,
    ) = createAabb(size, shaftAabb, broadButtHeight, broadWidth)

    fun createCrescentClubBladeAabb(
        size: Size,
        shaftAabb: AABB,
    ) = createAabb(size, shaftAabb, crescentButtHeight, crescentWidth)

    fun createDaggerClubBladeAabb(
        size: Size,
        shaftAabb: AABB,
    ) = createAabb(size, shaftAabb, daggerButtHeight, daggerLength)

    private fun createAabb(
        size: Size,
        shaftAabb: AABB,
        baseHeight: SizeConfig<Factor>,
        aabbWidth: Factor,
    ): AABB {
        val heightFactor = baseHeight.convert(size)
        val height = shaftAabb.size.height * heightFactor
        val width = height * aabbWidth
        val center = shaftAabb.getPoint(START, heightFactor / 2)
            .minusWidth(width / 2)

        return AABB.fromCenter(center, Size2d(width, height))
    }
}

fun visualizeClub(
    state: CharacterRenderState,
    body: Body,
    head: ClubHead,
    shaft: Shaft,
    fixation: HeadFixation,
    isOneHanded: Boolean,
    set: Set<BodySlot>,
) {
    val renderer = state.getLayer(HELD_EQUIPMENT_LAYER)
    val (leftHand, rightHand) = state.config.body.getMirroredArmPoint(state.aabb, body, END)
    val hand = state.getCenter(leftHand, rightHand, set, BodySlot.HeldInRightHand)
    val config = state.config.equipment.axe
    val shaftAabb = config.shaftAabb(state, body, isOneHanded, hand)

    visualizeClubHead(state, renderer, shaftAabb, head)
    visualizePolearmShaft(state, renderer, shaftAabb, shaft, NoPolearmHead)
}

fun visualizeClubHead(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    shaftAabb: AABB,
    head: ClubHead,
) = when (head) {
    is SimpleClubHead -> visualizeSimpleClubHead(state, renderer, shaftAabb, head)
}

private fun visualizeSimpleClubHead(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    shaftAabb: AABB,
    head: SimpleClubHead,
) {

}
