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
    fun getBodyWidth(config: ICharacterConfig<Body>) = width.convert(config.get().width)

    fun getHeadAabb(fullAabb: AABB): AABB {
        val startX = getStartX(headHeight)
        val start = fullAabb.getPoint(startX, START)
        val size = fullAabb.size * headHeight

        return AABB(start, size)
    }

    fun getDistanceFromNeckToBottom(head: AABB) =
        head.size.height * (FULL - headHeight) / headHeight

    fun getArmStarts(config: ICharacterConfig<Body>): Pair<Point2d, Point2d> {
        val armWidth = config.fullAABB().convertWidth(getArmWidth(config))
        val offset = Point2d.xAxis(armWidth)
        val shoulderWidth = getShoulderWidth(config)
        val torso = config.torsoAABB()
        val points = torso.getMirroredPoints(shoulderWidth, START)

        return points.copy(first = points.first - offset)
    }

    fun getArmWidth(config: ICharacterConfig<Body>) = getBodyWidth(config) * getShoulderWidth(config) * armWidth

    fun getArmHeight() = torsoHeight

    fun getArmSize(config: ICharacterConfig<Body>) = config.fullAABB().size
        .scale(getArmWidth(config), getArmHeight())

    fun getArmsSize(config: ICharacterConfig<Body>) = config.fullAABB().size.scale(
        getTorsoWidth(config) * getShoulderWidth(config) + getArmWidth(config) * 2,
        getArmHeight(),
    )

    fun getArmsAabb(config: ICharacterConfig<Body>) = AABB
        .fromTop(config.fullAABB().getPoint(CENTER, torsoY), getArmsSize(config))

    fun getFootLength(config: ICharacterConfig<Body>) = config.torsoAABB()
        .convertHeight(getBodyWidth(config) * foot.length)

    fun getFootRadius(config: ICharacterConfig<Body>) = config.fullAABB()
        .convertHeight(getFootRadiusFactor(config))

    fun getFootRadiusFactor(config: ICharacterConfig<Body>) = getBodyWidth(config) * foot.radius

    fun getFootY(config: ICharacterConfig<Body>) = END - getFootRadiusFactor(config)

    fun getHandRadius(config: ICharacterConfig<Body>) = config.fullAABB()
        .convertHeight(getHandRadiusFactor(config))

    fun getHandRadiusFactor(config: ICharacterConfig<Body>) = getBodyWidth(config) * handRadius

    fun getLegWidth(config: ICharacterConfig<Body>) = getBodyWidth(config) * legWidth

    fun getLegsWidth(config: ICharacterConfig<Body>) = getTorsoWidth(config) * HALF + getLegWidth(config)

    fun getLegsInnerWidth(config: ICharacterConfig<Body>) = getTorsoWidth(config) * HALF - getLegWidth(config)

    fun getLegHeight() = END - getLegY()

    fun getLegSize(config: ICharacterConfig<Body>) =
        config.fullAABB().size.scale(getLegWidth(config), getLegHeight())

    fun getLegY() = torsoY + torsoHeight

    fun getLegY(config: ICharacterConfig<Body>, factor: Factor): Factor {
        val topY = getLegY()
        val fullBottomY = getFootY(config)
        val fullHeight = fullBottomY - topY
        return fullBottomY - fullHeight * (FULL - factor)
    }

    fun getShoeHeight(config: ICharacterConfig<Body>) = getFootRadiusFactor(config) / getLegHeight()

    fun getMirroredArmPoint(config: ICharacterConfig<Body>, vertical: Factor): Pair<Point2d, Point2d> {
        val torso = config.torsoAABB()
        val size = getArmSize(config)
        val offset = Point2d.xAxis(size.width / 2.0f)
        val shoulderWidth = getShoulderWidth(config)
        val (left, right) = torso.getMirroredPoints(shoulderWidth, vertical)

        return Pair(left - offset, right + offset)
    }

    fun getMirroredLegPoint(config: ICharacterConfig<Body>, vertical: Factor): Pair<Point2d, Point2d> {
        val torso = config.torsoAABB()
        val size = getLegSize(config)
        val offset = Point2d.yAxis(size.height * vertical)
        val (left, right) = torso.getMirroredPoints(HALF, END)

        return Pair(left + offset, right + offset)
    }

    fun getTorsoCircumferenceFactor() = (FULL + torsoThicknessRelativeToWidth) * 2

    fun getTorsoAabb(fullAABB: AABB, body: Body): AABB {
        val config = object : ICharacterConfig<Body> {
            override fun get() = body

            override fun fullAABB() = TODO()
            override fun headAABB() = TODO()
            override fun torsoAABB() = TODO()
            override fun body() = TODO()
            override fun equipment() = TODO()
            override fun head() = TODO()
        }
        val width = getTorsoWidth(config)
        val startX = getStartX(width)
        val start = fullAABB.getPoint(startX, torsoY)
        val size = fullAABB.size.scale(width, torsoHeight)

        return AABB(start, size)
    }

    fun getTorsoWidth(config: ICharacterConfig<Body>) = getBodyWidth(config) * torsoWidth

    fun getHipWidth(config: ICharacterConfig<Body>) = when (config.get().bodyShape) {
        Fat -> widerWidth
        else -> FULL
    }

    fun getWaistWidth(config: ICharacterConfig<Body>) = when (config.get().bodyShape) {
        Hourglass -> hourglassWidth
        else -> FULL
    }

    fun getShoulderWidth(config: ICharacterConfig<Body>) = when (config.get().bodyShape) {
        Muscular -> widerWidth
        Rectangle, Hourglass -> shoulderWidth
        else -> FULL
    }

    fun getMaxWidth(config: ICharacterConfig<Body>) = when (config.get().bodyShape) {
        Rectangle, Hourglass -> shoulderWidth
        else -> widerWidth
    }

}

fun visualizeBody(
    state: CharacterRenderState<Body>,
    skin: Skin,
) {
    val options = state.config.getOptions(state.state, skin)

    visualizeArms(state, options)
    visualizeHands(state, options)
    visualizeLegs(state, options)
    visualizeFeet(state, options)
    visualizeTorso(state, options)
    visualizeBodyEquipment(state)
}

fun visualizeTorso(state: CharacterRenderState<Body>, options: RenderOptions) {
    val polygon = createTorso(state).build()

    state.renderer.getLayer().renderPolygon(polygon, options)
}

fun createTorso(
    state: CharacterRenderState<Body>,
    addTop: Boolean = true,
): Polygon2dBuilder {
    val builder = createHip(state)
    addTorso(state, builder, addTop)

    return builder
}

fun addTorso(
    config: ICharacterConfig<Body>,
    builder: Polygon2dBuilder,
    addTop: Boolean = true,
    paddedWidth: Factor = FULL,
) {
    val torso = config.torsoAABB()
    val bodyConfig = config.body()
    val waistWidth = bodyConfig.getWaistWidth(config) * paddedWidth
    val shoulderWidth = bodyConfig.getShoulderWidth(config)
    val shoulderHeight = bodyConfig.shoulderY

    builder.addMirroredPoints(torso, waistWidth, CENTER)
    builder.addMirroredPoints(torso, shoulderWidth, shoulderHeight)

    if (addTop) {
        builder.addMirroredPoints(torso, shoulderWidth, START)
    }
}

fun createHip(config: ICharacterConfig<Body>): Polygon2dBuilder {
    val builder = Polygon2dBuilder()
    addHip(config, builder)
    return builder
}

fun addHip(
    config: ICharacterConfig<Body>,
    builder: Polygon2dBuilder,
    paddedWidth: Factor = FULL,
    addBottom: Boolean = true,
) {
    val torso = config.torsoAABB()
    val hipWidth = config.body().getHipWidth(config) * paddedWidth

    if (addBottom) {
        builder.addMirroredPoints(torso, hipWidth, END)
    }
    builder.addMirroredPoints(torso, hipWidth, config.body().hipY)
}

fun visualizeArms(state: CharacterRenderState<Body>, options: RenderOptions) {
    val size = state.config.body.getArmSize(state)
    val (left, right) = state.config.body.getMirroredArmPoint(state, CENTER)
    val leftAabb = AABB.fromCenter(left, size)
    val rightAabb = AABB.fromCenter(right, size)

    state.renderer.getLayer(getArmLayer(MAIN_LAYER, state.renderFront))
        .renderRectangle(leftAabb, options)
        .renderRectangle(rightAabb, options)
}

fun visualizeHands(state: CharacterRenderState<Body>,options: RenderOptions) {
    val (left, right) = state.config.body.getMirroredArmPoint(state, END)
    val radius = state.config.body.getHandRadius(state)
    val layer = state.renderer.getLayer(getArmLayer(HAND_LAYER, state.renderFront))

    layer.renderCircle(left, radius, options)
    layer.renderCircle(right, radius, options)
}

fun visualizeLegs(state: CharacterRenderState<Body>,options: RenderOptions) {
    val size = state.config.body.getLegSize(state)
    val (left, right) = state.config.body.getMirroredLegPoint(state, CENTER)
    val leftAabb = AABB.fromCenter(left, size)
    val rightAabb = AABB.fromCenter(right, size)

    state.renderer.getLayer()
        .renderRectangle(leftAabb, options)
        .renderRectangle(rightAabb, options)
}
