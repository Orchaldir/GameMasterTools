package at.orchaldir.gm.visualization.character.beard

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.visualization.RenderConfig

fun getPencil(config: RenderConfig, aabb: AABB, head: Head): Polygon2d {
    val height = Factor(0.03f)
    val width = config.head.mouth.getWidth(head.mouth)
    return getSimple(config, aabb, height, height, width, width).build()
}

fun getPyramid(config: RenderConfig, aabb: AABB, head: Head): Polygon2d {
    val height = Factor(0.08f)
    val width = config.head.mouth.getWidth(head.mouth)
    return getSimple(config, aabb, height, Factor(0.01f), height, width).build()
}

fun getToothbrush(config: RenderConfig, aabb: AABB): Polygon2d {
    val height = Factor(0.08f)
    return getSimple(config, aabb, height, Factor(0.01f), height, height).build()
}

fun getWalrus(config: RenderConfig, aabb: AABB, head: Head): Polygon2d {
    val height = Factor(0.1f)
    val width = config.head.mouth.getWidth(head.mouth) + height
    val polygon = getSimple(config, aabb, height, Factor(0.01f), width, width)

    polygon.createSharpCorner(1)
    polygon.createSharpCorner(3)

    return polygon.build()
}

fun getSimple(
    config: RenderConfig,
    aabb: AABB,
    height: Factor,
    offset: Factor,
    topWidth: Factor,
    bottomWidth: Factor,
): Polygon2dBuilder {
    val bottomY = config.head.mouthY - offset
    val topY = bottomY - height
    val (topLeft, topRight) = aabb.getMirroredPoints(topWidth, topY)
    val (bottomLeft, bottomRight) = aabb.getMirroredPoints(bottomWidth, bottomY)
    val corners = mutableListOf(topLeft, bottomLeft, bottomRight, topRight)

    return Polygon2dBuilder(corners)
}