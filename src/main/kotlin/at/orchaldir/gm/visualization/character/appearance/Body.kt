package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape
import at.orchaldir.gm.core.model.character.appearance.BodyShape.*
import at.orchaldir.gm.core.model.character.appearance.Skin
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.CharacterRenderConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.equipment.visualizeBodyEquipment

data class BodyConfig(
    val armWidth: Factor,
    val foot: FootConfig,
    val handRadius: Factor,
    val headHeight: Factor,
    val hipY: Factor,
    val hourglassWidth: Factor,
    val legWidth: Factor,
    val shoulderY: Factor,
    val shoulderWidth: Factor,
    val tail: TailConfig,
    val torsoHeight: Factor,
    val torsoWidth: Factor,
    val torsoY: Factor,
    val widerWidth: Factor,
    val width: SizeConfig<Factor>,
) {
    fun getBodyWidth(body: Body) = width.convert(body.width)

    fun getHeadAabb(aabb: AABB): AABB {
        val startX = getStartX(headHeight)
        val start = aabb.getPoint(startX, START)
        val size = aabb.size * headHeight

        return AABB(start, size)
    }

    fun getDistanceFromNeckToBottom(head: AABB) =
        head.size.height * (FULL - headHeight) / headHeight

    fun getArmStarts(aabb: AABB, body: Body): Pair<Point2d, Point2d> {
        val armWidth = aabb.convertWidth(getArmWidth(body))
        val offset = Point2d.xAxis(armWidth)
        val shoulderWidth = getShoulderWidth(body.bodyShape)
        val torso = getTorsoAabb(aabb, body)
        val points = torso.getMirroredPoints(shoulderWidth, START)

        return points.copy(first = points.first - offset)
    }

    fun getArmWidth(body: Body) = getBodyWidth(body) * getShoulderWidth(body.bodyShape) * armWidth

    fun getArmHeight() = torsoHeight

    fun getArmSize(aabb: AABB, body: Body) = aabb.size.scale(getArmWidth(body), getArmHeight())

    fun getArmsSize(aabb: AABB, body: Body) = aabb.size.scale(
        getTorsoWidth(body) * getShoulderWidth(body.bodyShape) + getArmWidth(body) * 2,
        getArmHeight(),
    )

    fun getArmsAabb(aabb: AABB, body: Body) = AABB
        .fromTop(aabb.getPoint(CENTER, torsoY), getArmsSize(aabb, body))

    fun getFootRadius(body: Body) = getBodyWidth(body) * foot.radius

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

    fun getShoeHeight(body: Body) = getFootRadius(body) / getLegHeight()

    fun getMirroredArmPoint(aabb: AABB, body: Body, vertical: Factor): Pair<Point2d, Point2d> {
        val torso = getTorsoAabb(aabb, body)
        val size = getArmSize(aabb, body)
        val offset = Point2d.xAxis(size.width / 2.0f)
        val shoulderWidth = getShoulderWidth(body.bodyShape)
        val (left, right) = torso.getMirroredPoints(shoulderWidth, vertical)

        return Pair(left - offset, right + offset)
    }

    fun getMirroredLegPoint(aabb: AABB, body: Body, vertical: Factor): Pair<Point2d, Point2d> {
        val torso = getTorsoAabb(aabb, body)
        val size = getLegSize(aabb, body)
        val offset = Point2d.yAxis(size.height * vertical)
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
        Rectangle, Hourglass -> shoulderWidth
        else -> FULL
    }

    fun getMaxWidth(bodyShape: BodyShape) = when (bodyShape) {
        Rectangle, Hourglass -> shoulderWidth
        else -> widerWidth
    }

}

fun visualizeBody(
    state: CharacterRenderState,
    body: Body,
    skin: Skin,
) {
    val options = state.config.getOptions(state.state, skin)

    visualizeArms(state, body, options)
    visualizeHands(state, body, options)
    visualizeLegs(state, body, options)
    visualizeFeet(state, body, options)
    visualizeTorso(state, body, options)
    visualizeBodyEquipment(state, body)
}

fun visualizeTorso(state: CharacterRenderState, body: Body, options: RenderOptions) {
    val polygon = createTorso(state, body).build()

    state.renderer.getLayer().renderPolygon(polygon, options)
}

fun createTorso(
    state: CharacterRenderState,
    body: Body,
    addTop: Boolean = true,
): Polygon2dBuilder {
    val builder = createHip(state.config, state.aabb, body)
    addTorso(state, body, builder, addTop)

    return builder
}

fun addTorso(
    state: CharacterRenderState,
    body: Body,
    builder: Polygon2dBuilder,
    addTop: Boolean = true,
    paddedWidth: Factor = FULL,
) {
    val config = state.config.body
    val torso = config.getTorsoAabb(state.aabb, body)
    val waistWidth = config.getWaistWidth(body.bodyShape) * paddedWidth
    val shoulderWidth = config.getShoulderWidth(body.bodyShape)
    val shoulderHeight = config.shoulderY

    builder.addMirroredPoints(torso, waistWidth, CENTER)
    builder.addMirroredPoints(torso, shoulderWidth, shoulderHeight)

    if (addTop) {
        builder.addMirroredPoints(torso, shoulderWidth, START)
    }
}

fun createHip(config: CharacterRenderConfig, aabb: AABB, body: Body): Polygon2dBuilder {
    val builder = Polygon2dBuilder()
    addHip(config, builder, aabb, body)
    return builder
}

fun addHip(
    config: CharacterRenderConfig,
    builder: Polygon2dBuilder,
    aabb: AABB,
    body: Body,
    paddedWidth: Factor = FULL,
    addBottom: Boolean = true,
) {
    val torso = config.body.getTorsoAabb(aabb, body)
    val hipWidth = config.body.getHipWidth(body.bodyShape) * paddedWidth

    if (addBottom) {
        builder.addMirroredPoints(torso, hipWidth, END)
    }
    builder.addMirroredPoints(torso, hipWidth, config.body.hipY)
}

fun visualizeArms(state: CharacterRenderState, body: Body, options: RenderOptions) {
    val size = state.config.body.getArmSize(state.aabb, body)
    val (left, right) = state.config.body.getMirroredArmPoint(state.aabb, body, CENTER)
    val leftAabb = AABB.fromCenter(left, size)
    val rightAabb = AABB.fromCenter(right, size)

    state.renderer.getLayer(getArmLayer(MAIN_LAYER, state.renderFront))
        .renderRectangle(leftAabb, options)
        .renderRectangle(rightAabb, options)
}

fun visualizeHands(state: CharacterRenderState, body: Body, options: RenderOptions) {
    val (left, right) = state.config.body.getMirroredArmPoint(state.aabb, body, END)
    val radius = state.aabb.convertHeight(state.config.body.getHandRadius(body))
    val layer = state.renderer.getLayer(getArmLayer(HAND_LAYER, state.renderFront))

    layer.renderCircle(left, radius, options)
    layer.renderCircle(right, radius, options)
}

fun visualizeLegs(state: CharacterRenderState, body: Body, options: RenderOptions) {
    val size = state.config.body.getLegSize(state.aabb, body)
    val (left, right) = state.config.body.getMirroredLegPoint(state.aabb, body, CENTER)
    val leftAabb = AABB.fromCenter(left, size)
    val rightAabb = AABB.fromCenter(right, size)

    state.renderer.getLayer()
        .renderRectangle(leftAabb, options)
        .renderRectangle(rightAabb, options)
}
