package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.renderMirroredPolygons

data class EarConfig(
    private val roundRadius: SizeConfig<Factor>,
    val pointedLength: Factor,
) {
    fun getRoundRadius(aabb: AABB, size: Size) = aabb.convertHeight(roundRadius.convert(size))
}

fun visualizeEars(state: RenderState, head: Head) {
    when (head.ears) {
        NoEars -> doNothing()
        is NormalEars -> visualizeNormalEars(state, head.ears.shape, head.ears.size, head.skin)
    }
}

private fun visualizeNormalEars(
    state: RenderState,
    shape: EarShape,
    size: Size,
    skin: Skin,
) {
    val option = state.config.getOptions(skin)

    when (shape) {
        EarShape.PointedSideways -> visualizePointedSideways(state, size, option)
        EarShape.PointedUpwards -> visualizePointedUpwards(state, size, option)
        EarShape.Round -> visualizeRoundEars(state, size, option)
    }
}

private fun visualizeRoundEars(
    state: RenderState,
    size: Size,
    option: RenderOptions,
) {
    val (left, right) = state.aabb.getMirroredPoints(Factor(1.0f), state.config.head.earY)
    val radius = state.config.head.ears.getRoundRadius(state.aabb, size)

    state.renderer.getLayer()
        .renderCircle(left, radius, option)
        .renderCircle(right, radius, option)
}

private fun visualizePointedSideways(
    state: RenderState,
    size: Size,
    option: RenderOptions,
) {
    val center = state.aabb.getPoint(Factor(1.0f), state.config.head.earY)
    val radius = state.config.head.ears.getRoundRadius(state.aabb, size)
    val offset = Point2d(0.0f, radius.toMeters())
    val top = center - offset
    val bottom = center + offset
    val length = radius.toMeters() * state.config.head.ears.pointedLength.value
    val tip = top + Point2d(length, 0.0f)

    renderMirroredPolygons(state.renderer.getLayer(), option, state.aabb, listOf(top, bottom, tip))
}

private fun visualizePointedUpwards(
    state: RenderState,
    size: Size,
    option: RenderOptions,
) {
    val center = state.aabb.getPoint(Factor(1.0f), state.config.head.earY)
    val radius = state.config.head.ears.getRoundRadius(state.aabb, size).toMeters()
    val offset = Point2d(0.0f, radius)
    val top = center - offset
    val bottom = center + offset
    val length = radius * state.config.head.ears.pointedLength.value
    val outerTop = top + Point2d(radius, -length)
    val outerBottom = bottom + Point2d(radius, -radius)

    renderMirroredPolygons(state.renderer.getLayer(), option, state.aabb, listOf(top, bottom, outerBottom, outerTop))
}
