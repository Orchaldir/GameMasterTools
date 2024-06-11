package at.orchaldir.gm.visualization.character.beard

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.visualization.RenderConfig

fun getGoatPatch(config: RenderConfig, aabb: AABB, head: Head): Polygon2d {
    val width = config.head.mouth.getWidth(head.mouth) * 0.8f
    return getSharpMouthAndBottom(config, aabb, width)
}

fun getGoatee(config: RenderConfig, aabb: AABB, head: Head): Polygon2d {
    val width = config.head.mouth.getWidth(head.mouth) * 0.8f
    return fromTopAndBottom(aabb, Factor(0.95f), Factor(1.1f), width, width).build()
}

fun getSoulPatch(config: RenderConfig, aabb: AABB): Polygon2d {
    val height = Factor(0.1f)
    return fromMouthAndBottom(config, aabb, config.head.mouthY + height, height, height).build()
}

fun getVanDyke(config: RenderConfig, aabb: AABB): Polygon2d {
    return getSharpMouthAndBottom(config, aabb, Factor(0.1f))
}

private fun getSharpMouthAndBottom(
    config: RenderConfig,
    aabb: AABB,
    width: Factor,
): Polygon2d {
    val builder = fromMouthAndBottom(config, aabb, Factor(1.05f), width, width)

    builder.createSharpCorner(0)
    builder.createSharpCorner(4)

    return builder.build()
}

private fun fromMouthAndBottom(
    config: RenderConfig,
    aabb: AABB,
    bottomY: Factor,
    topWidth: Factor,
    bottomWidth: Factor,
) = fromTopAndBottom(aabb, config.head.mouthY, bottomY, topWidth, bottomWidth)

private fun fromTopAndBottom(
    aabb: AABB,
    topY: Factor,
    bottomY: Factor,
    topWidth: Factor,
    bottomWidth: Factor,
): Polygon2dBuilder {
    val (topLeft, topRight) = aabb.getMirroredPoints(topWidth, topY)
    val (bottomLeft, bottomRight) = aabb.getMirroredPoints(bottomWidth, bottomY)

    return Polygon2dBuilder(mutableListOf(topLeft, bottomLeft, bottomRight, topRight))
}