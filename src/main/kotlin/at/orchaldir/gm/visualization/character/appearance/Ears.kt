package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.renderMirroredPolygons

data class EarConfig(
    private val roundRadius: SizeConfig<Factor>,
    val pointedLength: Factor,
) {
    fun getRoundRadius(aabb: AABB, size: Size) = aabb.convertHeight(roundRadius.convert(size))
    fun getRoundRadius(headHeight: Distance, size: Size) = headHeight * roundRadius.convert(size)
    fun getSidewaysLength(headHeight: Distance, size: Size) = getRoundRadius(headHeight, size) * pointedLength
    fun getUpwardsLength(headHeight: Distance, size: Size) = getRoundRadius(headHeight, size) * (pointedLength + FULL)
}

fun visualizeEars(state: CharacterRenderState, head: Head, skin: Skin) {
    when (head.ears) {
        NoEars -> doNothing()
        is NormalEars -> visualizeNormalEars(state, head.ears.shape, head.ears.size, skin)
    }
}

private fun visualizeNormalEars(
    state: CharacterRenderState,
    shape: EarShape,
    size: Size,
    skin: Skin,
) {
    val option = state.config.getOptions(state.state, skin)

    when (shape) {
        EarShape.PointedSideways -> visualizePointedSideways(state, size, option)
        EarShape.PointedUpwards -> visualizePointedUpwards(state, size, option)
        EarShape.Round -> visualizeRoundEars(state, size, option)
    }
}

private fun visualizeRoundEars(
    state: CharacterRenderState,
    size: Size,
    option: RenderOptions,
) {
    val (left, right) = state.aabb.getMirroredPoints(FULL, state.config.head.earY)
    val radius = state.config.head.ears.getRoundRadius(state.aabb, size)

    state.renderer.getLayer()
        .renderCircle(left, radius, option)
        .renderCircle(right, radius, option)
}

private fun visualizePointedSideways(
    state: CharacterRenderState,
    size: Size,
    option: RenderOptions,
) {
    val center = state.aabb.getPoint(FULL, state.config.head.earY)
    val radius = state.config.head.ears.getRoundRadius(state.aabb, size)
    val offset = Point2d.yAxis(radius)
    val top = center - offset
    val bottom = center + offset
    val length = radius * state.config.head.ears.pointedLength
    val tip = top + Point2d.xAxis(length)

    renderMirroredPolygons(state.renderer, option, state.aabb, listOf(top, bottom, tip))
}

private fun visualizePointedUpwards(
    state: CharacterRenderState,
    size: Size,
    option: RenderOptions,
) {
    val center = state.aabb.getPoint(FULL, state.config.head.earY)
    val radius = state.config.head.ears.getRoundRadius(state.aabb, size)
    val offset = Point2d.yAxis(radius)
    val top = center - offset
    val bottom = center + offset
    val length = radius * state.config.head.ears.pointedLength
    val outerTop = top + Point2d(radius, -length)
    val outerBottom = bottom + Point2d(radius, -radius)

    renderMirroredPolygons(state.renderer, option, state.aabb, listOf(top, bottom, outerBottom, outerTop))
}
