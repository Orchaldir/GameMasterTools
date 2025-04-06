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

fun visualizeLongHair(state: CharacterRenderState, hair: NormalHair, longHair: LongHairCut) {
    val config = state.config
    val options = config.getLineOptions(hair.color)
    val height = config.body.getDistanceFromNeckToBottom(state.aabb) * config.head.hair.getLength(longHair.length)

    when (longHair.style) {
        LongHairStyle.Straight -> visualizeStraightHair(state, options, height)
        LongHairStyle.U -> visualizeU(state, options, height)
        LongHairStyle.Wavy -> doNothing()
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
