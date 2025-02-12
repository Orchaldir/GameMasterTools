package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.style.NecklineStyle
import at.orchaldir.gm.core.model.item.equipment.style.NecklineStyle.*
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.visualization.character.CharacterRenderState

data class NecklineConfig(
    val heightCrew: Factor,
    val heightV: Factor,
    val heightDeepV: Factor,
    val heightVeryDeepV: Factor,
    val widthCrew: Factor,
    val widthV: Factor,
) {
    fun getHeight(style: NecklineStyle) = when (style) {
        Crew -> heightCrew
        Halter -> TODO()
        Strapless -> TODO()
        V -> heightV
        DeepV -> heightDeepV
        VeryDeepV -> heightVeryDeepV
        else -> ZERO
    }
}

fun addNeckline(
    state: CharacterRenderState,
    body: Body,
    builder: Polygon2dBuilder,
    style: NecklineStyle,
) {
    if (!state.renderFront && !style.renderBack()) {
        return
    }

    val torsoAabb = state.config.body.getTorsoAabb(state.aabb, body)
    val neckline = state.config.equipment.neckline

    when (style) {
        Asymmetrical -> addAsymmetrical(state, builder, body, torsoAabb)
        Crew -> addRound(builder, torsoAabb, neckline.widthCrew, neckline.heightCrew)
        Halter -> addHalter(state, builder, body, torsoAabb)
        None, Strapless -> return
        V -> addV(builder, torsoAabb, neckline.widthV, neckline.heightV)
        DeepV -> addV(builder, torsoAabb, neckline.widthV, neckline.heightDeepV)
        VeryDeepV -> addV(builder, torsoAabb, neckline.widthV, neckline.heightVeryDeepV)
    }
}

private fun addAsymmetrical(
    state: CharacterRenderState,
    builder: Polygon2dBuilder,
    body: Body,
    aabb: AABB,
) {
    val shoulderWidth = state.config.body.getShoulderWidth(body.bodyShape)
    val offset = state.getSideOffset(shoulderWidth * 0.5f)
    builder.addPoint(aabb, CENTER + offset, START)
}

private fun addHalter(
    state: CharacterRenderState,
    builder: Polygon2dBuilder,
    body: Body,
    aabb: AABB,
) {
    val shoulderWidth = state.config.body.getShoulderWidth(body.bodyShape)
    builder.addMirroredPoints(aabb, shoulderWidth * 0.5f, START)
    builder.addPoint(aabb, CENTER, state.config.body.shoulderY)
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
