package at.orchaldir.gm.visualization.character.beard

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.visualization.RenderConfig

fun getFuManchu(config: RenderConfig, aabb: AABB, head: Head): Polygon2d {
    val mouthTopY = config.head.getMouthTopY(head.mouth)
    val thickness = Factor(0.05f)
    val mouthWidth = config.head.mouthConfig.getWidth(head.mouth)
    val deltaY = Factor(0.02f)
    val outerWidth = mouthWidth + thickness * 2.0f
    val topY = mouthTopY - thickness - deltaY
    val bottomY = Factor(1.1f)
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

fun getHandlebar(config: RenderConfig, aabb: AABB, head: Head): Polygon2d {
    val mouthTopY = config.head.getMouthTopY(head.mouth)
    val thickness = Factor(0.05f)
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

fun getPencil(config: RenderConfig, aabb: AABB, head: Head): Polygon2d {
    val height = Factor(0.03f)
    val width = config.head.mouthConfig.getWidth(head.mouth)
    return getSimple(config, aabb, head, height, height, width, width).build()
}

fun getPyramid(config: RenderConfig, aabb: AABB, head: Head): Polygon2d {
    val height = Factor(0.08f)
    val width = config.head.mouthConfig.getWidth(head.mouth)
    return getSimple(config, aabb, head, height, Factor(0.01f), height, width).build()
}

fun getToothbrush(config: RenderConfig, aabb: AABB, head: Head): Polygon2d {
    val height = Factor(0.08f)
    return getSimple(config, aabb, head, height, Factor(0.01f), height, height).build()
}

fun getWalrus(config: RenderConfig, aabb: AABB, head: Head): Polygon2d {
    val height = Factor(0.1f)
    val width = config.head.mouthConfig.getWidth(head.mouth) + height
    val polygon = getSimple(config, aabb, head, height, Factor(0.01f), width, width)

    polygon.createSharpCorner(1)
    polygon.createSharpCorner(3)

    return polygon.build()
}

private fun getSimple(
    config: RenderConfig,
    aabb: AABB,
    head: Head,
    height: Factor,
    offset: Factor,
    topWidth: Factor,
    bottomWidth: Factor,
): Polygon2dBuilder {
    val mouthTopY = config.head.getMouthTopY(head.mouth)
    val bottomY = mouthTopY - offset
    val topY = bottomY - height
    val (topLeft, topRight) = aabb.getMirroredPoints(topWidth, topY)
    val (bottomLeft, bottomRight) = aabb.getMirroredPoints(bottomWidth, bottomY)
    val corners = mutableListOf(topLeft, bottomLeft, bottomRight, topRight)

    return Polygon2dBuilder(corners)
}