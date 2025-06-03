package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.style.Blade
import at.orchaldir.gm.core.model.item.equipment.style.BladeShape
import at.orchaldir.gm.core.model.item.equipment.style.SimpleBlade
import at.orchaldir.gm.core.model.item.equipment.style.SwordHilt
import at.orchaldir.gm.utils.convert
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.TEXT_LAYER

data class SwordConfig(
    val gripLength: Factor,
    val gripWidth: Factor,
    val straightTopY: Factor,
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
        val center = hand.minusHeight(oneHandLength / 2)
        val size = Size2d(length * gripWidth, length)

        return AABB.fromTop(center, size)
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
    val renderer = state.getLayer(TEXT_LAYER)
    val (leftHand, rightHand) = state.config.body.getMirroredArmPoint(state.aabb, body, END)
    val hand = state.getCenter(leftHand, rightHand, set, BodySlot.HeldInRightHand)
    val config = state.config.equipment.sword
    val gripAabb = config.gripAabb(state, body, isOneHanded, hand)
    val bladeSize = blade.size(state.aabb)
    val bladeAabb = AABB.fromBottom(gripAabb.getPoint(CENTER, START), bladeSize)

    visualizeBlade(state, renderer, config, blade, bladeAabb)
    visualizeSwordHilt(state, renderer, config, hilt, gripAabb)
}

fun visualizeBlade(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    config: SwordConfig,
    blade: Blade,
    aabb: AABB,
) {
    when (blade) {
        is SimpleBlade -> visualizeSimpleBlade(state, renderer, config, blade, aabb)
    }
}

private fun visualizeSimpleBlade(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    config: SwordConfig,
    blade: SimpleBlade,
    aabb: AABB,
) {
    val color = blade.part.getColor(state.state())
    val options = FillAndBorder(color.toRender(), state.lineOptions())
    val polygon = createSimplyBladePolygon(config, blade, aabb)

    renderer.renderRoundedPolygon(polygon, options)
}

private fun createSimplyBladePolygon(
    config: SwordConfig,
    blade: SimpleBlade,
    aabb: AABB,
): Polygon2d {
    val builder = Polygon2dBuilder()

    when (blade.shape) {
        BladeShape.Leave -> builder
            .addLeftPoint(aabb, CENTER, START, true)
            .addMirroredPoints(aabb, FULL, THIRD)
            .addMirroredPoints(aabb, HALF, TWO_THIRD)
            .addMirroredPoints(aabb, FULL, END, true)

        BladeShape.Straight, BladeShape.Flame -> builder
            .addLeftPoint(aabb, CENTER, START, true)
            .addMirroredPoints(aabb, FULL, config.straightTopY, true)
            .addMirroredPoints(aabb, FULL, END, true)

        BladeShape.Triangle -> builder
            .addLeftPoint(aabb, CENTER, START, true)
            .addMirroredPoints(aabb, HALF, config.straightTopY, true)
            .addMirroredPoints(aabb, FULL, END, true)
    }

    return builder.build()
}
