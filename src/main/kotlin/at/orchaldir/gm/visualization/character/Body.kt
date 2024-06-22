package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.RenderOptions
import at.orchaldir.gm.utils.renderer.Renderer
import at.orchaldir.gm.visualization.RenderConfig
import at.orchaldir.gm.visualization.SizeConfig

data class BodyConfig(
    val headHeight: Factor,
    val footRadius: Factor,
    val legWidth: Factor,
    val torsoHeight: Factor,
    val torsoWidth: Factor,
    val torsoY: Factor,
    val width: SizeConfig,
) {
    fun getBodyWidth(body: Body) = Factor(width.convert(body.width))

    fun getHeadAabb(aabb: AABB): AABB {
        val startX = getStartX(headHeight)
        val start = aabb.getPoint(startX, START)
        val size = aabb.size * headHeight

        return AABB(start, size)
    }

    fun getFootRadius(body: Body) = getBodyWidth(body) * footRadius

    fun getLegWidth(body: Body) = getBodyWidth(body) * legWidth

    fun getLegHeight() = END - getLegY()

    fun getLegSize(aabb: AABB, body: Body) = aabb.size.scale(getLegWidth(body), getLegHeight())

    fun getLegY() = torsoY + torsoHeight

    fun getMirroredLegPoint(aabb: AABB, body: Body, vertical: Factor): Pair<Point2d, Point2d> {
        val torso = getTorsoAabb(aabb, body)
        val size = getLegSize(aabb, body)
        val offset = Point2d(0.0f, size.height * vertical.value)
        val (left, right) = torso.getMirroredPoints(CENTER, END)

        return Pair(left + offset, right + offset)
    }

    fun getTorsoAabb(aabb: AABB, body: Body): AABB {
        val width = getTorsoWidth(body)
        val startX = getStartX(width)
        val start = aabb.getPoint(startX, torsoY)
        val size = aabb.size.scale(width, torsoHeight)

        return AABB(start, size)
    }

    fun getTorsoWidth(body: Body) = getBodyWidth(body) * torsoWidth

}

fun visualizeBody(renderer: Renderer, config: RenderConfig, aabb: AABB, body: Body) {
    val options = config.getOptions(body.skin)
    visualizeTorso(renderer, config, aabb, body, options)
    visualizeLegs(renderer, config, aabb, body, options)
    visualizeFeet(renderer, config, aabb, body, options)
}

fun visualizeTorso(renderer: Renderer, config: RenderConfig, aabb: AABB, body: Body, options: RenderOptions) {
    val torso = config.body.getTorsoAabb(aabb, body)

    renderer.renderRectangle(torso, options)
}

fun visualizeLegs(renderer: Renderer, config: RenderConfig, aabb: AABB, body: Body, options: RenderOptions) {
    val size = config.body.getLegSize(aabb, body)
    val (left, right) = config.body.getMirroredLegPoint(aabb, body, CENTER)
    val leftAabb = AABB.fromCenter(left, size)
    val rightAabb = AABB.fromCenter(right, size)

    renderer.renderRectangle(leftAabb, options)
    renderer.renderRectangle(rightAabb, options)
}

fun visualizeFeet(renderer: Renderer, config: RenderConfig, aabb: AABB, body: Body, options: RenderOptions) {
    val (left, right) = config.body.getMirroredLegPoint(aabb, body, END)
    val footRadius = aabb.convertHeight(config.body.getFootRadius(body))
    val offset = Orientation.fromDegree(0.0f)
    val angle = Orientation.fromDegree(180.0f)

    renderer.renderCircleArc(left, footRadius, offset, angle, options)
    renderer.renderCircleArc(right, footRadius, offset, angle, options)
}

