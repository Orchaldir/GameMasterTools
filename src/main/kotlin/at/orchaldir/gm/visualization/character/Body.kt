package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape
import at.orchaldir.gm.core.model.item.Equipment
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.RenderOptions
import at.orchaldir.gm.utils.renderer.Renderer
import at.orchaldir.gm.visualization.RenderConfig
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.equipment.visualizeBodyEquipment

data class BodyConfig(
    val armWidth: Factor,
    val footRadius: Factor,
    val handRadius: Factor,
    val headHeight: Factor,
    val hourglassWidth: Factor,
    val legWidth: Factor,
    val torsoHeight: Factor,
    val torsoWidth: Factor,
    val torsoY: Factor,
    val widerWidth: Factor,
    val width: SizeConfig,
) {
    fun getBodyWidth(body: Body) = Factor(width.convert(body.width))

    fun getHeadAabb(aabb: AABB): AABB {
        val startX = getStartX(headHeight)
        val start = aabb.getPoint(startX, START)
        val size = aabb.size * headHeight

        return AABB(start, size)
    }

    fun getArmWidth(body: Body) = getBodyWidth(body) * getShoulderWidth(body.bodyShape) * armWidth

    fun getArmHeight() = torsoHeight

    fun getArmSize(aabb: AABB, body: Body) = aabb.size.scale(getArmWidth(body), getArmHeight())

    fun getFootRadius(body: Body) = getBodyWidth(body) * footRadius

    fun getHandRadius(body: Body) = getBodyWidth(body) * handRadius

    fun getLegWidth(body: Body) = getBodyWidth(body) * legWidth

    fun getLegHeight() = END - getLegY()

    fun getLegSize(aabb: AABB, body: Body) = aabb.size.scale(getLegWidth(body), getLegHeight())

    fun getLegY() = torsoY + torsoHeight

    fun getMirroredArmPoint(aabb: AABB, body: Body, vertical: Factor): Pair<Point2d, Point2d> {
        val torso = getTorsoAabb(aabb, body)
        val size = getArmSize(aabb, body)
        val offset = Point2d(size.width / 2.0f, 0.0f)
        val shoulderWidth = getShoulderWidth(body.bodyShape)
        val (left, right) = torso.getMirroredPoints(shoulderWidth, vertical)

        return Pair(left - offset, right + offset)
    }

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

    fun getHipWidth(bodyShape: BodyShape) = when (bodyShape) {
        BodyShape.Fat -> widerWidth
        else -> FULL
    }

    fun getWaistWidth(bodyShape: BodyShape) = when (bodyShape) {
        BodyShape.Hourglass -> hourglassWidth
        else -> FULL
    }

    fun getShoulderWidth(bodyShape: BodyShape) = when (bodyShape) {
        BodyShape.Muscular -> widerWidth
        BodyShape.Rectangle -> FULL.interpolate(widerWidth, 0.33f)
        else -> FULL
    }

}

fun visualizeBody(
    renderer: Renderer,
    config: RenderConfig,
    aabb: AABB,
    body: Body,
    equipment: List<Equipment>,
) {
    val options = config.getOptions(body.skin)
    visualizeArms(renderer, config, aabb, body, options)
    visualizeHands(renderer, config, aabb, body, options)
    visualizeLegs(renderer, config, aabb, body, options)
    visualizeFeet(renderer, config, aabb, body, options)
    visualizeTorso(renderer, config, aabb, body, options)
    visualizeBodyEquipment(renderer, config, aabb, body, equipment)
}

fun visualizeTorso(renderer: Renderer, config: RenderConfig, aabb: AABB, body: Body, options: RenderOptions) {
    val polygon = createTorsoPolygon(config, aabb, body)

    renderer.renderPolygon(polygon, options)
}

fun createTorsoPolygon(config: RenderConfig, aabb: AABB, body: Body): Polygon2d {
    val torso = config.body.getTorsoAabb(aabb, body)
    val builder = Polygon2dBuilder()
    val hipWidth = config.body.getHipWidth(body.bodyShape)
    val waistWidth = config.body.getWaistWidth(body.bodyShape)
    val shoulderWidth = config.body.getShoulderWidth(body.bodyShape)

    builder.addMirroredPoints(torso, hipWidth, END)
    builder.addMirroredPoints(torso, hipWidth, Factor(0.75f))
    builder.addMirroredPoints(torso, waistWidth, CENTER)
    builder.addMirroredPoints(torso, shoulderWidth, Factor(0.25f))
    builder.addMirroredPoints(torso, shoulderWidth, START)

    return builder.build()
}

fun visualizeArms(renderer: Renderer, config: RenderConfig, aabb: AABB, body: Body, options: RenderOptions) {
    val size = config.body.getArmSize(aabb, body)
    val (left, right) = config.body.getMirroredArmPoint(aabb, body, CENTER)
    val leftAabb = AABB.fromCenter(left, size)
    val rightAabb = AABB.fromCenter(right, size)

    renderer.renderRectangle(leftAabb, options)
    renderer.renderRectangle(rightAabb, options)
}

fun visualizeHands(renderer: Renderer, config: RenderConfig, aabb: AABB, body: Body, options: RenderOptions) {
    val (left, right) = config.body.getMirroredArmPoint(aabb, body, END)
    val radius = aabb.convertHeight(config.body.getHandRadius(body))

    renderer.renderCircle(left, radius, options)
    renderer.renderCircle(right, radius, options)
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
    val radius = aabb.convertHeight(config.body.getFootRadius(body))
    val offset = Orientation.fromDegree(0.0f)
    val angle = Orientation.fromDegree(180.0f)

    renderer.renderCircleArc(left, radius, offset, angle, options)
    renderer.renderCircleArc(right, radius, offset, angle, options)
}

