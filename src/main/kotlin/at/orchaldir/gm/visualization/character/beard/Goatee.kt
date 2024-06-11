package at.orchaldir.gm.visualization.character.beard

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.visualization.RenderConfig

fun getGoatPatch(config: RenderConfig, aabb: AABB, head: Head): Polygon2d {
    val width = config.head.mouth.getWidth(head.mouth) * 0.8f
    val polygon = fromMouthAndBottom(config, aabb, Factor(1.05f), width, width)

    //polygon.create_sharp_corner(0)
    //polygon.create_sharp_corner(4)

    return Polygon2d(polygon)
}

fun fromMouthAndBottom(
    config: RenderConfig,
    aabb: AABB,
    bottomY: Factor,
    topWidth: Factor,
    bottomWidth: Factor,
) = fromTopAndBottom(aabb, config.head.mouthY, bottomY, topWidth, bottomWidth)

fun fromTopAndBottom(
    aabb: AABB,
    topY: Factor,
    bottomY: Factor,
    topWidth: Factor,
    bottomWidth: Factor,
): MutableList<Point2d> {
    val (topLeft, topRight) = aabb.getMirroredPoints(topWidth, topY)
    val (bottomLeft, bottomRight) = aabb.getMirroredPoints(bottomWidth, bottomY)

    return mutableListOf(topLeft, bottomLeft, bottomRight, topRight)
}