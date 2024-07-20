package at.orchaldir.gm.visualization.equipment.part

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.style.NecklineStyle
import at.orchaldir.gm.core.model.item.style.NecklineStyle.*
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.visualization.RenderConfig
import at.orchaldir.gm.visualization.RenderState

data class NecklineConfig(
    val heightCrew: Factor,
    val heightV: Factor,
    val heightDeepV: Factor,
    val heightVeryDeepV: Factor,
    val widthCrew: Factor,
    val widthV: Factor,
)

fun addNeckline(
    state: RenderState,
    body: Body,
    builder: Polygon2dBuilder,
    style: NecklineStyle,
) {
    if (!state.renderFront) {
        return
    }

    val torsoAabb = state.config.body.getTorsoAabb(state.aabb, body)
    val neckline = state.config.equipment.neckline

    when (style) {
        Asymmetrical -> addAsymmetrical(state, builder, body, torsoAabb)
        Crew -> addRound(builder, torsoAabb, neckline.widthCrew, neckline.heightCrew)
        None, Strapless -> return
        V -> addV(builder, torsoAabb, neckline.widthV, neckline.heightV)
        DeepV -> addV(builder, torsoAabb, neckline.widthV, neckline.heightDeepV)
        VeryDeepV -> addV(builder, torsoAabb, neckline.widthV, neckline.heightVeryDeepV)
    }
}

private fun addAsymmetrical(
    state: RenderState,
    builder: Polygon2dBuilder,
    body: Body,
    aabb: AABB,
) {
    val shoulderWidth = state.config.body.getShoulderWidth(body.bodyShape)
    builder.addPoint(aabb, CENTER + shoulderWidth * 0.5f, START)
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
