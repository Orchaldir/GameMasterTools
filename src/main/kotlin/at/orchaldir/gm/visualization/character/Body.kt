package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape
import at.orchaldir.gm.core.model.character.appearance.BodyShape.*
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.RenderOptions
import at.orchaldir.gm.visualization.RenderConfig
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.equipment.visualizeBodyEquipment

data class BodyConfig(
    val armWidth: Factor,
    val footRadius: Factor,
    val handRadius: Factor,
    val headHeight: Factor,
    val hipY: Factor,
    val hourglassWidth: Factor,
    val legWidth: Factor,
    val shoulderY: Factor,
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

    fun getArmStarts(aabb: AABB, body: Body): Pair<Point2d, Point2d> {
        val armWidth = aabb.convertWidth(getArmWidth(body))
        val offset = Point2d(armWidth.value, 0.0f)
        val shoulderWidth = getShoulderWidth(body.bodyShape)
        val torso = getTorsoAabb(aabb, body)
        val points = torso.getMirroredPoints(shoulderWidth, START)

        return points.copy(first = points.first.copy() - offset)
    }

    fun getArmWidth(body: Body) = getBodyWidth(body) * getShoulderWidth(body.bodyShape) * armWidth

    fun getArmHeight() = torsoHeight

    fun getArmSize(aabb: AABB, body: Body) = aabb.size.scale(getArmWidth(body), getArmHeight())

    fun getFootRadius(body: Body) = getBodyWidth(body) * footRadius

    fun getFootY(body: Body) = END - getFootRadius(body)

    fun getHandRadius(body: Body) = getBodyWidth(body) * handRadius

    fun getLegWidth(body: Body) = getBodyWidth(body) * legWidth

    fun getLegsWidth(body: Body) = getTorsoWidth(body) * HALF + getLegWidth(body)

    fun getLegsInnerWidth(body: Body) = getTorsoWidth(body) * HALF - getLegWidth(body)

    fun getLegHeight() = END - getLegY()

    fun getLegSize(aabb: AABB, body: Body) =
        aabb.size.scale(getLegWidth(body), getLegHeight())

    fun getLegY() = torsoY + torsoHeight

    fun getLegY(body: Body, factor: Factor): Factor {
        val topY = getLegY()
        val fullBottomY = getFootY(body)
        val fullHeight = fullBottomY - topY
        return fullBottomY - fullHeight * (FULL - factor)
    }

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
        val (left, right) = torso.getMirroredPoints(HALF, END)

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
        Fat -> widerWidth
        else -> FULL
    }

    fun getWaistWidth(bodyShape: BodyShape) = when (bodyShape) {
        Hourglass -> hourglassWidth
        else -> FULL
    }

    fun getShoulderWidth(bodyShape: BodyShape) = when (bodyShape) {
        Muscular -> widerWidth
        Rectangle, Hourglass -> FULL.interpolate(widerWidth, Factor(0.33f))
        else -> FULL
    }

}

fun visualizeBody(
    state: RenderState,
    body: Body,
) {
    val options = state.config.getOptions(body.skin)
    visualizeArms(state, body, options)
    visualizeHands(state, body, options)
    visualizeLegs(state, body, options)
    visualizeFeet(state, body, options)
    visualizeTorso(state, body, options)
    visualizeBodyEquipment(state, body)
}

fun visualizeTorso(state: RenderState, body: Body, options: RenderOptions) {
    val polygon = createTorso(state, body).build()

    state.renderer.renderPolygon(polygon, options)
}

fun createTorso(
    state: RenderState,
    body: Body,
    addTop: Boolean = true,
): Polygon2dBuilder {
    val builder = createHip(state.config, state.aabb, body)
    addTorso(state, body, builder, addTop)

    return builder
}

fun addTorso(
    state: RenderState,
    body: Body,
    builder: Polygon2dBuilder,
    addTop: Boolean = true,
) {
    val config = state.config.body
    val torso = config.getTorsoAabb(state.aabb, body)
    val waistWidth = config.getWaistWidth(body.bodyShape)
    val shoulderWidth = config.getShoulderWidth(body.bodyShape)
    val shoulderHeight = config.shoulderY

    builder.addMirroredPoints(torso, waistWidth, CENTER)
    builder.addMirroredPoints(torso, shoulderWidth, shoulderHeight)

    if (addTop) {
        builder.addMirroredPoints(torso, shoulderWidth, START)
    }
}

fun createHip(config: RenderConfig, aabb: AABB, body: Body): Polygon2dBuilder {
    val builder = Polygon2dBuilder()
    addHip(config, builder, aabb, body)
    return builder
}

fun addHip(
    config: RenderConfig,
    builder: Polygon2dBuilder,
    aabb: AABB,
    body: Body,
    padding: Factor = FULL,
    renderBottom: Boolean = true,
) {
    val torso = config.body.getTorsoAabb(aabb, body)
    val hipWidth = config.body.getHipWidth(body.bodyShape) * padding

    if (renderBottom) {
        builder.addMirroredPoints(torso, hipWidth, END)
    }
    builder.addMirroredPoints(torso, hipWidth, config.body.hipY)
}

fun visualizeArms(state: RenderState, body: Body, options: RenderOptions) {
    val size = state.config.body.getArmSize(state.aabb, body)
    val (left, right) = state.config.body.getMirroredArmPoint(state.aabb, body, CENTER)
    val leftAabb = AABB.fromCenter(left, size)
    val rightAabb = AABB.fromCenter(right, size)

    state.renderer.renderRectangle(leftAabb, options)
    state.renderer.renderRectangle(rightAabb, options)
}

fun visualizeHands(state: RenderState, body: Body, options: RenderOptions) {
    val (left, right) = state.config.body.getMirroredArmPoint(state.aabb, body, END)
    val radius = state.aabb.convertHeight(state.config.body.getHandRadius(body))
    val layer = if (state.renderFront) {
        ABOVE_EQUIPMENT_LAYER
    } else {
        EQUIPMENT_LAYER
    }

    state.renderer.renderCircle(left, radius, options, layer)
    state.renderer.renderCircle(right, radius, options, layer)
}

fun visualizeLegs(state: RenderState, body: Body, options: RenderOptions) {
    val size = state.config.body.getLegSize(state.aabb, body)
    val (left, right) = state.config.body.getMirroredLegPoint(state.aabb, body, CENTER)
    val leftAabb = AABB.fromCenter(left, size)
    val rightAabb = AABB.fromCenter(right, size)

    state.renderer.renderRectangle(leftAabb, options)
    state.renderer.renderRectangle(rightAabb, options)
}

fun visualizeFeet(
    state: RenderState,
    body: Body,
    options: RenderOptions,
) {
    val layer = if (state.renderFront) {
        MAIN_LAYER
    } else {
        BEHIND_LAYER
    }
    visualizeFeet(state, body, options, layer)
}

fun visualizeFeet(
    state: RenderState,
    body: Body,
    options: RenderOptions,
    layer: Int,
) {
    val (left, right) = state.config.body.getMirroredLegPoint(state.aabb, body, END)
    val radius = state.aabb.convertHeight(state.config.body.getFootRadius(body))
    val offset = Orientation.fromDegree(0.0f)
    val angle = Orientation.fromDegree(180.0f)

    state.renderer.renderCircleArc(left, radius, offset, angle, options, layer)
    state.renderer.renderCircleArc(right, radius, offset, angle, options, layer)
}

