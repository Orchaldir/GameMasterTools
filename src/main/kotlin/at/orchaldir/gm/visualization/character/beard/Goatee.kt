package at.orchaldir.gm.visualization.character.beard

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.Mouth
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.math.Polygon2dBuilder
import at.orchaldir.gm.visualization.RenderConfig

fun getGoatPatch(config: RenderConfig, aabb: AABB, head: Head): Polygon2d {
    val width = config.head.mouthConfig.getWidth(head.mouth) * 0.8f
    return getSharpMouthAndBottom(config, aabb, head.mouth, width)
}

fun getGoatee(config: RenderConfig, aabb: AABB, head: Head): Polygon2d {
    val width = config.head.mouthConfig.getWidth(head.mouth) * 0.8f
    val bottomY = Factor(1.1f)
    val builder = fromTopAndBottom(aabb, Factor(0.95f), bottomY, width, width)

    builder.createSharpCorner(0)
    builder.createSharpCorner(4)

    return builder.build()
}

fun getSoulPatch(config: RenderConfig, aabb: AABB, head: Head): Polygon2d {
    val topY = config.head.getMouthBottomY(head.mouth)
    val height = Factor(0.1f)
    return fromTopAndBottom(aabb, topY, topY + height, height, height).build()
}

fun getVanDyke(config: RenderConfig, aabb: AABB, head: Head): Polygon2d {
    return getSharpMouthAndBottom(config, aabb, head.mouth, Factor(0.1f))
}

private fun getSharpMouthAndBottom(
    config: RenderConfig,
    aabb: AABB,
    mouth: Mouth,
    width: Factor,
): Polygon2d {
    val builder = fromMouthAndBottom(config, aabb, mouth, Factor(1.05f), width, width)

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