package at.orchaldir.gm.visualization.character.appearance.beard

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.utils.math.CENTER
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.math.Polygon2dBuilder
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun getFuManchu(state: CharacterRenderState<Head>): Polygon2d {
    val aabb = state.headAABB()
    val config = state.config
    val mouthTopY = config.head.mouth.getTopY(state)
    val thickness = config.head.beard.mediumThickness
    val mouthWidth = config.head.mouth.getWidth(state)
    val deltaY = fromPercentage(2)
    val outerWidth = mouthWidth + thickness * 2.0f
    val topY = mouthTopY - thickness - deltaY
    val bottomY = config.head.getGoateeBottomY()
    val (topLeft, topRight) = aabb.getMirroredPoints(outerWidth, topY)
    val (mouthLeft, mouthRight) =
        aabb.getMirroredPoints(mouthWidth, mouthTopY - deltaY)
    val (outerLeft, outerRight) = aabb.getMirroredPoints(outerWidth, bottomY)
    val (bottomLeft, bottomRight) = aabb.getMirroredPoints(mouthWidth, bottomY)
    val corners = listOf(
        topLeft,
        outerLeft,
        bottomLeft,
        mouthLeft,
        mouthRight,
        bottomRight,
        outerRight,
        topRight,
    )

    return Polygon2d(corners)
}

fun getHandlebar(state: CharacterRenderState<Head>): Polygon2d {
    val aabb = state.headAABB()
    val config = state.config
    val mouthTopY = config.head.mouth.getTopY(state)
    val thickness = config.head.beard.smallThickness
    val mouthWidth = config.head.mouth.getWidth(state)
    val width = mouthWidth + thickness * 2.0f
    val centerY = mouthTopY - thickness
    val bottomY = mouthTopY + thickness
    val innerY = bottomY - thickness
    val topY = innerY - fromPercentage(10)
    val bottom = aabb.getPoint(CENTER, centerY)
    val top = aabb.getPoint(CENTER, centerY - thickness)
    val (topLeft, topRight) = aabb.getMirroredPoints(width, topY)
    val (innerLeft, innerRight) = aabb.getMirroredPoints(mouthWidth, innerY)
    val (bottomLeft, bottomRight) = aabb.getMirroredPoints(width, bottomY)
    val corners = listOf(
        top,
        innerLeft,
        topLeft,
        bottomLeft,
        bottom,
        bottomRight,
        topRight,
        innerRight,
    )

    return Polygon2d(corners)
}

fun getPencil(state: CharacterRenderState<Head>): Polygon2d {
    val height = state.config.head.beard.smallThickness
    val width = state.config.head.mouth.getWidth(state)
    return getSimple(state, height, width, width).build()
}

fun getPyramid(state: CharacterRenderState<Head>): Polygon2d {
    val height = state.config.head.beard.mediumThickness
    val width = state.config.head.mouth.getWidth(state)
    return getSimple(state, height, height, width).build()
}

fun getToothbrush(state: CharacterRenderState<Head>): Polygon2d {
    val height = state.config.head.beard.mediumThickness
    return getSimple(state, height, height, height).build()
}

fun getWalrus(state: CharacterRenderState<Head>): Polygon2d {
    val height = state.config.head.beard.mediumThickness
    val width = state.config.head.mouth.getWidth(state) + height
    val polygon = getSimple(state, height, width, width)

    polygon.createSharpCorners(1)

    return polygon.build()
}

private fun getSimple(
    state: CharacterRenderState<Head>,
    height: Factor,
    topWidth: Factor,
    bottomWidth: Factor,
): Polygon2dBuilder {
    val mouthTopY = state.config.head.mouth.getTopY(state)
    val bottomY = mouthTopY - state.config.head.beard.moustacheOffset
    val topY = bottomY - height
    val builder = Polygon2dBuilder()
    val aabb = state.headAABB()

    builder.addMirroredPoints(aabb, topWidth, topY)
    builder.addMirroredPoints(aabb, bottomWidth, bottomY)

    return builder
}