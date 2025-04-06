package at.orchaldir.gm.visualization.character.appearance.hair

import at.orchaldir.gm.core.model.character.appearance.hair.LongHairCut
import at.orchaldir.gm.core.model.character.appearance.hair.LongHairStyle
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHair
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.BEHIND_LAYER
import at.orchaldir.gm.visualization.renderRoundedBuilder
import kotlin.math.roundToInt

fun visualizeLongHair(state: CharacterRenderState, hair: NormalHair, longHair: LongHairCut) {
    val config = state.config
    val options = config.getLineOptions(hair.color)
    val height = config.body.getDistanceFromNeckToBottom(state.aabb) * config.head.hair.getLength(longHair.length)

    when (longHair.style) {
        LongHairStyle.Straight -> visualizeStraightHair(state, options, height)
        LongHairStyle.U -> visualizeU(state, options, height)
        LongHairStyle.Wavy -> visualizeWavy(state, options, height)
    }
}

private fun visualizeStraightHair(
    state: CharacterRenderState,
    options: RenderOptions,
    height: Distance,
) {
    val padding = state.config.head.hair.longPadding
    val width = FULL + padding * 2.0f
    val (left, right) = state.aabb.getMirroredPoints(width, FULL)
    val builder = Polygon2dBuilder()
        .addLeftPoint(state.aabb, CENTER, -padding)
        .addMirroredPoints(state.aabb, width, -padding)
        .addMirroredPoints(state.aabb, width, HALF)
        .addPoints(left.addHeight(height), right.addHeight(height), true)

    renderRoundedBuilder(state.renderer, builder, options, state.getLayerIndex(BEHIND_LAYER))
}

private fun visualizeU(
    state: CharacterRenderState,
    options: RenderOptions,
    height: Distance,
) {
    val padding = state.config.head.hair.longPadding
    val width = FULL + padding * 2.0f
    val half = height / 2.0f
    val (left, right) = state.aabb.getMirroredPoints(width, FULL)
    val builder = Polygon2dBuilder()
        .addLeftPoint(state.aabb, CENTER, -padding)
        .addMirroredPoints(state.aabb, width, -padding)
        .addMirroredPoints(state.aabb, width, HALF)
        .addPoints(left.addHeight(half), right.addHeight(half))
        .addPoints(left.addHeight(height), right.addHeight(height))

    renderRoundedBuilder(state.renderer, builder, options, state.getLayerIndex(BEHIND_LAYER))
}

private fun visualizeWavy(
    state: CharacterRenderState,
    options: RenderOptions,
    height: Distance,
) {
    val padding = state.config.head.hair.longPadding
    var isPositive = false
    val topCenter = state.aabb.getPoint(CENTER, -padding)
    val width = state.aabb.convertWidth(FULL + padding * 2.0f) / 2.0f
    val waveAmplitude = width / 3.0f
    val bottomCenter = state.aabb.getPoint(CENTER, END).addHeight(height)
    val segments = 2 * (height.toMeters() / state.aabb.size.height + 1.0f).roundToInt()
    val splitter = SegmentSplitter.fromStartAndEnd(topCenter, bottomCenter, segments)
    val orientation = Orientation.fromDegree(-90.0f)
    val builder = Polygon2dBuilder()
        .addLeftPoint(state.aabb, CENTER, -padding)
        .addLeftAndRightPoint(topCenter, orientation, width)

    splitter.getCenters().forEach { center ->
        builder.addLeftAndRightPoint(
            center,
            orientation,
            if (isPositive) {
                width
            } else {
                width + waveAmplitude
            }
        )

        isPositive = !isPositive
    }

    builder.addLeftAndRightPoint(bottomCenter, orientation, width)

    renderRoundedBuilder(state.renderer, builder, options, state.getLayerIndex(BEHIND_LAYER))
}
