package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.style.Blade
import at.orchaldir.gm.core.model.item.equipment.style.BladeShape
import at.orchaldir.gm.core.model.item.equipment.style.SimpleBlade
import at.orchaldir.gm.core.model.item.equipment.style.SwordHilt
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.utils.convert
import at.orchaldir.gm.utils.isEven
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.ICharacterConfig
import at.orchaldir.gm.visualization.character.appearance.HELD_EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.equipment.part.GripConfig

data class SwordConfig(
    val flameStep: Factor,
    val flameOffset: Factor,
    val grip: GripConfig,
    val gripLength: Factor,
    val gripWidth: Factor,
    val pommelSizes: SizeConfig<Factor>,
    val straightTopY: Factor,
) {

    fun gripAabb(
        config: ICharacterConfig<Body>,
        isOneHanded: Boolean,
        hand: Point2d,
    ): AABB {
        val handRadius = config.body().getHandRadius(config)
        val oneHandLength = handRadius * gripLength
        val length = oneHandLength * isOneHanded.convert(1f, 1.5f)
        val center = hand.minusHeight(oneHandLength / 2)
        val size = Size2d(oneHandLength * gripWidth, length)

        return AABB.fromTop(center, size)
    }

}

fun visualizeSword(
    state: CharacterRenderState<Body>,
    blade: Blade,
    hilt: SwordHilt,
    isOneHanded: Boolean,
    set: Set<BodySlot>,
) {
    val renderer = state.getLayer(HELD_EQUIPMENT_LAYER)
    val (leftHand, rightHand) = state.config.body.getMirroredArmPoint(state, END)
    val hand = state.getCenter(leftHand, rightHand, set, BodySlot.HeldInRightHand)
    val config = state.config.equipment.sword
    val gripAabb = config.gripAabb(state, isOneHanded, hand)

    val bladeBottom = visualizeSwordHilt(state, renderer, config, hilt, gripAabb)

    val bladeSize = blade.size(state.fullAABB.size.height, gripAabb)
    val bladeAabb = AABB.fromBottom(bladeBottom, bladeSize)

    visualizeBlade(state, renderer, config, blade, bladeAabb)
}

fun visualizeBlade(
    state: CharacterRenderState<Body>,
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
    state: CharacterRenderState<Body>,
    renderer: LayerRenderer,
    config: SwordConfig,
    blade: SimpleBlade,
    aabb: AABB,
) {
    val color = blade.part.getColor(state.state, state.colors)
    val options = state.config.getLineOptions(color)
    val polygon = createSimplyBladePolygon(state, config, blade, aabb)

    renderer.renderRoundedPolygon(polygon, options)
}

private fun createSimplyBladePolygon(
    state: CharacterRenderState<Body>,
    config: SwordConfig,
    blade: SimpleBlade,
    aabb: AABB,
): Polygon2d {
    val builder = Polygon2dBuilder()

    when (blade.shape) {
        BladeShape.Flame -> {
            val remainingHeightFactor = FULL - config.straightTopY * 3
            val remainingHeight = aabb.size.height * remainingHeightFactor
            val rowHeight = state.fullAABB.size.height * config.flameStep
            val rows = (remainingHeight.toMeters() / rowHeight.toMeters()).toInt()
            val step = remainingHeightFactor / rows
            val offset = config.flameOffset

            builder
                .addLeftPoint(aabb, CENTER, START, true)
                .addMirroredPoints(aabb, FULL, config.straightTopY, true)
                .addMirroredPoints(aabb, FULL, config.straightTopY * 2)

            var y = config.straightTopY * 2 + step

            repeat(rows - 1) { row ->
                val x = if (row.isEven()) {
                    offset
                } else {
                    -offset
                }

                builder.addHorizontalPoints(aabb, FULL, CENTER + x, y)

                y += step
            }

            builder
                .addMirroredPoints(aabb, FULL, y)
                .addMirroredPoints(aabb, FULL, END, true)
        }

        BladeShape.Leave -> builder
            .addLeftPoint(aabb, CENTER, START, true)
            .addMirroredPoints(aabb, FULL, THIRD)
            .addMirroredPoints(aabb, HALF, TWO_THIRD)
            .addMirroredPoints(aabb, FULL, END, true)

        BladeShape.Straight -> builder
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
