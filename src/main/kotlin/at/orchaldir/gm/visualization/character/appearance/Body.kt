package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape
import at.orchaldir.gm.core.model.character.appearance.BodyShape.*
import at.orchaldir.gm.core.model.character.appearance.Skin
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.ICharacterConfig
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
    val torsoThicknessRelativeToWidth: Factor,
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

    fun getArmStarts(config: ICharacterConfig, body: Body): Pair<Point2d, Point2d> {
        val armWidth = config.fullAABB().convertWidth(getArmWidth(body))
        val offset = Point2d.xAxis(armWidth)
        val shoulderWidth = getShoulderWidth(body.bodyShape)
        val torso = config.torsoAABB()
        val points = torso.getMirroredPoints(shoulderWidth, START)

        return points.copy(first = points.first - offset)
    }

    fun getArmWidth(body: Body) = getBodyWidth(body) * getShoulderWidth(body.bodyShape) * armWidth

    fun getArmHeight() = torsoHeight

    fun getArmSize(config: ICharacterConfig, body: Body) = config.fullAABB().size
        .scale(getArmWidth(body), getArmHeight())

    fun getArmsSize(aabb: AABB, body: Body) = aabb.size.scale(
        getTorsoWidth(body) * getShoulderWidth(body.bodyShape) + getArmWidth(body) * 2,
        getArmHeight(),
    )

    fun getArmsAabb(aabb: AABB, body: Body) = AABB
        .fromTop(aabb.getPoint(CENTER, torsoY), getArmsSize(aabb, body))

    fun getFootLength(config: ICharacterConfig, body: Body) = config.torsoAABB()
        .convertHeight(getBodyWidth(body) * foot.length)

    fun getFootRadius(config: ICharacterConfig, body: Body) = config.fullAABB()
        .convertHeight(getFootRadiusFactor(body))
    fun getFootRadiusFactor(body: Body) = getBodyWidth(body) * foot.radius

    fun getFootY(body: Body) = END - getFootRadiusFactor(body)

    fun getHandRadius(config: ICharacterConfig, body: Body) = config.fullAABB()
        .convertHeight(getHandRadiusFactor(body))
    fun getHandRadiusFactor(body: Body) = getBodyWidth(body) * handRadius

    fun getLegWidth(body: Body) = getBodyWidth(body) * legWidth

    fun getLegsWidth(body: Body) = getTorsoWidth(body) * HALF + getLegWidth(body)

    fun getLegsInnerWidth(body: Body) = getTorsoWidth(body) * HALF - getLegWidth(body)

    fun getLegHeight() = END - getLegY()

    fun getLegSize(config: ICharacterConfig, body: Body) =
        config.fullAABB().size.scale(getLegWidth(body), getLegHeight())

    fun getLegY() = torsoY + torsoHeight

    fun getLegY(body: Body, factor: Factor): Factor {
        val topY = getLegY()
        val fullBottomY = getFootY(body)
        val fullHeight = fullBottomY - topY
        return fullBottomY - fullHeight * (FULL - factor)
    }

    fun getShoeHeight(body: Body) = getFootRadiusFactor(body) / getLegHeight()

    fun getMirroredArmPoint(config: ICharacterConfig, body: Body, vertical: Factor): Pair<Point2d, Point2d> {
        val torso = config.torsoAABB()
        val size = getArmSize(config, body)
        val offset = Point2d.xAxis(size.width / 2.0f)
        val shoulderWidth = getShoulderWidth(body.bodyShape)
        val (left, right) = torso.getMirroredPoints(shoulderWidth, vertical)

        return Pair(left - offset, right + offset)
    }

    fun getMirroredLegPoint(config: ICharacterConfig, body: Body, vertical: Factor): Pair<Point2d, Point2d> {
        val torso =config.torsoAABB()
        val size = getLegSize(config, body)
        val offset = Point2d.yAxis(size.height * vertical)
        val (left, right) = torso.getMirroredPoints(HALF, END)

        return Pair(left + offset, right + offset)
    }

    fun getTorsoCircumferenceFactor() = (FULL + torsoThicknessRelativeToWidth) * 2

    fun getTorsoAabb(fullAABB: AABB, body: Body): AABB {
        val width = getTorsoWidth(body)
        val startX = getStartX(width)
        val start = fullAABB.getPoint(startX, torsoY)
        val size = fullAABB.size.scale(width, torsoHeight)

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
    val builder = createHip(state, body)
    addTorso(state, body, builder, addTop)

    return builder
}

fun addTorso(
    config: ICharacterConfig,
    body: Body,
    builder: Polygon2dBuilder,
    addTop: Boolean = true,
    paddedWidth: Factor = FULL,
) {
    val torso = config.torsoAABB()
    val config = config.body()
    val waistWidth = config.getWaistWidth(body.bodyShape) * paddedWidth
    val shoulderWidth = config.getShoulderWidth(body.bodyShape)
    val shoulderHeight = config.shoulderY

    builder.addMirroredPoints(torso, waistWidth, CENTER)
    builder.addMirroredPoints(torso, shoulderWidth, shoulderHeight)

    if (addTop) {
        builder.addMirroredPoints(torso, shoulderWidth, START)
    }
}

fun createHip(config: ICharacterConfig, body: Body): Polygon2dBuilder {
    val builder = Polygon2dBuilder()
    addHip(config, builder, body)
    return builder
}

fun addHip(
    config: ICharacterConfig,
    builder: Polygon2dBuilder,
    body: Body,
    paddedWidth: Factor = FULL,
    addBottom: Boolean = true,
) {
    val torso = config.torsoAABB()
    val hipWidth = config.body().getHipWidth(body.bodyShape) * paddedWidth

    if (addBottom) {
        builder.addMirroredPoints(torso, hipWidth, END)
    }
    builder.addMirroredPoints(torso, hipWidth, config.body().hipY)
}

fun visualizeArms(state: CharacterRenderState, body: Body, options: RenderOptions) {
    val size = state.config.body.getArmSize(state, body)
    val (left, right) = state.config.body.getMirroredArmPoint(state, body, CENTER)
    val leftAabb = AABB.fromCenter(left, size)
    val rightAabb = AABB.fromCenter(right, size)

    state.renderer.getLayer(getArmLayer(MAIN_LAYER, state.renderFront))
        .renderRectangle(leftAabb, options)
        .renderRectangle(rightAabb, options)
}

fun visualizeHands(state: CharacterRenderState, body: Body, options: RenderOptions) {
    val (left, right) = state.config.body.getMirroredArmPoint(state, body, END)
    val radius = state.config.body.getHandRadius(state, body)
    val layer = state.renderer.getLayer(getArmLayer(HAND_LAYER, state.renderFront))

    layer.renderCircle(left, radius, options)
    layer.renderCircle(right, radius, options)
}

fun visualizeLegs(state: CharacterRenderState, body: Body, options: RenderOptions) {
    val size = state.config.body.getLegSize(state, body)
    val (left, right) = state.config.body.getMirroredLegPoint(state, body, CENTER)
    val leftAabb = AABB.fromCenter(left, size)
    val rightAabb = AABB.fromCenter(right, size)

    state.renderer.getLayer()
        .renderRectangle(leftAabb, options)
        .renderRectangle(rightAabb, options)
}
