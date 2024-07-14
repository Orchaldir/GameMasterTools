package at.orchaldir.gm.visualization.character.beard

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.utils.math.CENTER
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.math.Polygon2dBuilder
import at.orchaldir.gm.visualization.RenderState

fun getFuManchu(state: RenderState, head: Head): Polygon2d {
    val aabb = state.aabb
    val config = state.config
    val mouthTopY = config.head.getMouthTopY(head.mouth)
    val thickness = config.head.beard.mediumThickness
    val mouthWidth = config.head.mouthConfig.getWidth(head.mouth)
    val deltaY = Factor(0.02f)
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

fun getHandlebar(state: RenderState, head: Head): Polygon2d {
    val aabb = state.aabb
    val config = state.config
    val mouthTopY = config.head.getMouthTopY(head.mouth)
    val thickness = config.head.beard.smallThickness
    val mouthWidth = config.head.mouthConfig.getWidth(head.mouth)
    val width = mouthWidth + thickness * 2.0f
    val centerY = mouthTopY - thickness
    val bottomY = mouthTopY + thickness
    val innerY = bottomY - thickness
    val topY = innerY - Factor(0.1f)
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

fun getPencil(state: RenderState, head: Head): Polygon2d {
    val height = state.config.head.beard.smallThickness
    val width = state.config.head.mouthConfig.getWidth(head.mouth)
    return getSimple(state, head, height, width, width).build()
}

fun getPyramid(state: RenderState, head: Head): Polygon2d {
    val height = state.config.head.beard.mediumThickness
    val width = state.config.head.mouthConfig.getWidth(head.mouth)
    return getSimple(state, head, height, height, width).build()
}

fun getToothbrush(state: RenderState, head: Head): Polygon2d {
    val height = state.config.head.beard.mediumThickness
    return getSimple(state, head, height, height, height).build()
}

fun getWalrus(state: RenderState, head: Head): Polygon2d {
    val height = state.config.head.beard.mediumThickness
    val width = state.config.head.mouthConfig.getWidth(head.mouth) + height
    val polygon = getSimple(state, head, height, width, width)

    polygon.createSharpCorners(1)

    return polygon.build()
}

private fun getSimple(
    state: RenderState,
    head: Head,
    height: Factor,
    topWidth: Factor,
    bottomWidth: Factor,
): Polygon2dBuilder {
    val mouthTopY = state.config.head.getMouthTopY(head.mouth)
    val bottomY = mouthTopY - state.config.head.beard.moustacheOffset
    val topY = bottomY - height
    val builder = Polygon2dBuilder()

    builder.addMirroredPoints(state.aabb, topWidth, topY)
    builder.addMirroredPoints(state.aabb, bottomWidth, bottomY)

    return builder
}