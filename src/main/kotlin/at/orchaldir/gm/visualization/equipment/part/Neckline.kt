package at.orchaldir.gm.visualization.equipment.part

import at.orchaldir.gm.core.model.item.NecklineStyle
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.visualization.RenderConfig

data class NecklineConfig(
    val heightCrew: Factor,
    val heightV: Factor,
    val heightDeepV: Factor,
    val heightVeryDeepV: Factor,
    val widthCrew: Factor,
    val widthV: Factor,
)

fun addNeckline(
    builder: Polygon2dBuilder,
    config: RenderConfig,
    aabb: AABB,
    style: NecklineStyle,
) {
    val neckline = config.equipment.neckline

    when (style) {
        NecklineStyle.Crew -> addRound(builder, aabb, neckline.widthCrew, neckline.heightCrew)
        NecklineStyle.None -> return
        NecklineStyle.V -> addV(builder, aabb, neckline.widthV, neckline.heightV)
        NecklineStyle.DeepV -> addV(builder, aabb, neckline.widthV, neckline.heightDeepV)
        NecklineStyle.VeryDeepV -> addV(builder, aabb, neckline.widthV, neckline.heightVeryDeepV)
    }
}

private fun addRound(
    builder: Polygon2dBuilder,
    aabb: AABB,
    width: Factor,
    depth: Factor,
) {
    builder.addMirroredPoints(aabb, width, START)
    builder.addMirroredPoints(aabb, width * 0.7f, depth)
}

private fun addV(
    builder: Polygon2dBuilder,
    aabb: AABB,
    width: Factor,
    depth: Factor,
) {
    builder.addMirroredPoints(aabb, width, START)
    builder.addPoint(aabb, CENTER, depth)
}
