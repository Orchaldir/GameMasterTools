package at.orchaldir.gm.visualization.character.beard

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.Mouth
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.visualization.RenderConfig

fun getChinPuff(config: RenderConfig, aabb: AABB, head: Head): Polygon2d {
    val width = config.head.mouthConfig.getWidth(head.mouth) * 0.8f
    return getSharpMouthAndBottom(config, aabb, head.mouth, width, Factor(1.05f))
}

fun getGoatee(config: RenderConfig, aabb: AABB, head: Head): Polygon2d {
    val width = config.head.mouthConfig.getWidth(head.mouth) * 0.8f
    val (topLeft, topRight) = aabb.getMirroredPoints(width, Factor(0.95f))
    val bottom = aabb.getPoint(CENTER, Factor(1.1f))

    return Polygon2d(listOf(topLeft, topRight, bottom))
}

fun getLandingStrip(config: RenderConfig, aabb: AABB, head: Head): Polygon2d {
    val width = config.head.mouthConfig.getWidth(head.mouth) * 0.8f
    return getSharpMouthAndBottom(config, aabb, head.mouth, width, END)
}

fun getSoulPatch(config: RenderConfig, aabb: AABB, head: Head): Polygon2d {
    val topY = config.head.getMouthBottomY(head.mouth)
    val height = Factor(0.1f)
    return fromTopAndBottom(aabb, topY, topY + height, height, height).build()
}

fun getVanDyke(config: RenderConfig, aabb: AABB, head: Head): Polygon2d {
    return getSharpMouthAndBottom(config, aabb, head.mouth, Factor(0.1f), Factor(1.05f))
}

private fun getSharpMouthAndBottom(
    config: RenderConfig,
    aabb: AABB,
    mouth: Mouth,
    width: Factor,
    bottomY: Factor,
): Polygon2d {
    val builder = fromMouthAndBottom(config, aabb, mouth, bottomY, width, width)

    builder.createSharpCorner(0)
    builder.createSharpCorner(4)

    return builder.build()
}

private fun fromMouthAndBottom(
    config: RenderConfig,
    aabb: AABB,
    mouth: Mouth,
    bottomY: Factor,
    topWidth: Factor,
    bottomWidth: Factor,
) = fromTopAndBottom(aabb, config.head.getMouthBottomY(mouth), bottomY, topWidth, bottomWidth)

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