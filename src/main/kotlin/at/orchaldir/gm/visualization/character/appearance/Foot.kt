package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.ClawedFoot
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.renderer.model.NoBorder
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState

data class FootConfig(
    val length: Factor,
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
    val offset = Orientation.fromDegrees(0)
    val angle = Orientation.fromDegrees(180)
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
    val step = Point2d(stepLength, fromMillimeters(0))
    var position = center - step * ((foot.count - 1) / 2.0f)
    val clawLength = (radius * state.config.body.foot.clawSize.convert(foot.size))
    val clawLengthHalf = clawLength / 2.0f
    val clawWidth = clawLength * state.config.body.foot.clawLengthToWidth
    val clawWidthHalf = clawWidth / 2.0f

    repeat(foot.count) {
        val points = listOf(
            position + Point2d(-clawWidthHalf, -clawLengthHalf),
            position + Point2d.yAxis(clawLengthHalf),
            position + Point2d(clawWidthHalf, -clawLengthHalf),
        )
        val polygon = Polygon2d(points)

        state.renderer.getLayer(layerIndex).renderPolygon(polygon, options)

        position += step
    }
}

