package at.orchaldir.gm.visualization.character.appearance.hair

import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.BEHIND_LAYER
import at.orchaldir.gm.visualization.renderBuilder
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
    val (left, right) = state.aabb.getMirroredPoints(state.config.head.hair.width, FULL)
    val builder = Polygon2dBuilder()
        .addMirroredPoints(state.aabb, state.config.head.hair.width, ZERO)
        .addPoints(left.addHeight(height), right.addHeight(height))

    renderBuilder(state.renderer, builder, options, state.getLayerIndex(BEHIND_LAYER))
}

private fun visualizeU(
    state: CharacterRenderState,
    options: RenderOptions,
    height: Distance,
) {
    val half = height / 2.0f
    val (left, right) = state.aabb.getMirroredPoints(FULL, FULL)
    val builder = Polygon2dBuilder()
        .addMirroredPoints(state.aabb, state.config.head.hair.width, ZERO, true)
        .addPoints(left.addHeight(half), right.addHeight(half))
        .addPoints(left.addHeight(height), right.addHeight(height))

    renderRoundedBuilder(state.renderer, builder, options, state.getLayerIndex(BEHIND_LAYER))
}

private fun visualizeWavy(
    state: CharacterRenderState,
    options: RenderOptions,
    height: Distance,
) {
    val width = Point2d(state.aabb.size.width, 0.0f)
    val step = width / 5.0f
    var isPositive = false
    val (topLeft, topRight) = state.aabb.getMirroredPoints(FULL, START)
    val bottomLeft = state.aabb.getPoint(START, END).addHeight(height)
    val bottomRight = bottomLeft + width
    val segments = 2 * (height.toMeters() / state.aabb.size.height + 1.0f).roundToInt()
    val splitter = SegmentSplitter.fromStartAndEnd(topLeft, bottomLeft, segments)
    val builder = Polygon2dBuilder()
        .addPoints(topLeft, topRight, true)

    splitter.getCenters().forEach { center ->
        if (isPositive) {
            builder.addLeftPoint(center + step)
        } else {
            builder.addLeftPoint(center - step)
        }

        isPositive = !isPositive
    }

    builder.addPoints(bottomLeft, bottomRight, true)

    renderRoundedBuilder(state.renderer, builder, options, state.getLayerIndex(BEHIND_LAYER))
}
