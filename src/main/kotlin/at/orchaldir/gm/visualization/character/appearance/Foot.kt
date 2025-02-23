package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.ClawedFoot
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.NoBorder
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState

data class FootConfig(
    val radius: Factor,
    val clawLengthToWidth: Factor,
    val clawSize: SizeConfig<Factor>,
)

fun visualizeFeet(
    state: CharacterRenderState,
    body: Body,
    options: RenderOptions,
) {
    val layer = if (state.renderFront) {
        MAIN_LAYER
    } else {
        BEHIND_LAYER
    }
    visualizeFeet(state, body, options, layer, true)
}

fun visualizeFeet(
    state: CharacterRenderState,
    body: Body,
    options: RenderOptions,
    layerIndex: Int,
    renderClaws: Boolean = false,
) {
    val (left, right) = state.config.body.getMirroredLegPoint(state.aabb, body, END)
    val radius = state.aabb.convertHeight(state.config.body.getFootRadius(body))
    val offset = Orientation.fromDegree(0.0f)
    val angle = Orientation.fromDegree(180.0f)
    val layer = state.renderer.getLayer(layerIndex)

    layer.renderCircleArc(left, radius, offset, angle, options)
    layer.renderCircleArc(right, radius, offset, angle, options)

    if (renderClaws && body.foot is ClawedFoot) {
        val clawsLayer = layerIndex - if (state.renderFront) {
            0
        } else {
            1
        }

        visualizeClaws(state, body.foot, clawsLayer, left, radius)
        visualizeClaws(state, body.foot, clawsLayer, right, radius)
    }
}

fun visualizeClaws(
    state: CharacterRenderState,
    foot: ClawedFoot,
    layerIndex: Int,
    center: Point2d,
    radius: Distance,
) {
    val options = NoBorder(foot.color.toRender())
    val length = radius * 2
    val stepLength = length / foot.count
    val step = Point2d(stepLength, Distance(0))
    var position = center - step * ((foot.count - 1) / 2.0f)
    val clawLength = radius.toMeters() * 0.5f * when (foot.size) {
        Size.Small -> 0.75f
        Size.Medium -> 1.0f
        Size.Large -> 1.25f
    }
    val clawLengthHalf = clawLength / 2.0f
    val clawWidth = clawLength / 2.0f
    val clawWidthHalf = clawWidth / 2.0f

    (0..<foot.count).forEach {
        val points = listOf(
            position + Point2d(-clawWidthHalf, -clawLengthHalf),
            position + Point2d(0.0f, clawLengthHalf),
            position + Point2d(clawWidthHalf, -clawLengthHalf),
        )
        val polygon = Polygon2d(points)

        state.renderer.getLayer(layerIndex).renderPolygon(polygon, options)

        position += step
    }
}

