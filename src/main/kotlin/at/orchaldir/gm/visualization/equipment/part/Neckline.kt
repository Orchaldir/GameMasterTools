package at.orchaldir.gm.visualization.equipment.part

import at.orchaldir.gm.core.model.item.style.NecklineStyle
import at.orchaldir.gm.core.model.item.style.NecklineStyle.*
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
    torsoAabb: AABB,
    style: NecklineStyle,
) {
    val neckline = config.equipment.neckline

    when (style) {
        Asymmetrical -> addAsymmetrical(builder, torsoAabb)
        Crew -> addRound(builder, torsoAabb, neckline.widthCrew, neckline.heightCrew)
        None, Strapless -> return
        V -> addV(builder, torsoAabb, neckline.widthV, neckline.heightV)
        DeepV -> addV(builder, torsoAabb, neckline.widthV, neckline.heightDeepV)
        VeryDeepV -> addV(builder, torsoAabb, neckline.widthV, neckline.heightVeryDeepV)
    }
}

private fun addAsymmetrical(
    builder: Polygon2dBuilder,
    aabb: AABB,
) {
    builder.addPoint(aabb, FULL, START)
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
