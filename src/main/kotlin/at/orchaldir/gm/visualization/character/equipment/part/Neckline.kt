package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.visualization.character.CharacterRenderState

data class NecklineConfig(
    val heightCrew: Factor,
    val heightV: SizeConfig<Factor>,
    val widthCrew: Factor,
    val widthV: Factor,
    val openingPadding: Factor,
) {
    fun getHeight(neckline: Neckline) = when (neckline) {
        Crew -> heightCrew
        Halter -> TODO()
        is NecklineWithOpening -> heightV.convert(neckline.height)
        Strapless -> TODO()
        is VNeck -> heightV.convert(neckline.height)
        else -> ZERO
    }
}

fun visualizeNeckline(
    state: CharacterRenderState<Body>,
    neckline: Neckline,
    layer: Int,
) = when (neckline) {
    is NecklineWithOpening -> {
        val padding = state.config.equipment.neckline.openingPadding
        val aabb = state.torsoAABB()
        val height = state.config.equipment.neckline.heightV.convert(neckline.height)
        val start = aabb.getPoint(HALF, START)
        val end = aabb.getPoint(HALF, START + height + padding * 2)

        state.renderer.getLayer(layer)
            .renderLine(start, end, state.config.colors.line)

        visualizeOpening(
            state,
            aabb,
            HALF,
            START + padding,
            START + height + padding,
            neckline.opening,
            layer,
        )
    }

    else -> doNothing()
}

fun addNeckline(
    state: CharacterRenderState<Body>,
    builder: Polygon2dBuilder,
    neckline: Neckline,
) {
    if (!state.renderFront && !neckline.renderBack()) {
        return
    }

    val torsoAabb = state.torsoAABB()
    val config = state.config.equipment.neckline

    when (neckline) {
        Asymmetrical -> addAsymmetrical(state, builder, torsoAabb)
        Crew -> addRound(builder, torsoAabb, config.widthCrew, config.heightCrew)
        Halter -> addHalter(state, builder, torsoAabb)
        NoNeckline, Strapless -> return
        is NecklineWithOpening -> return
        is VNeck -> addV(builder, torsoAabb, config.widthV, config.heightV.convert(neckline.height))
    }
}

private fun addAsymmetrical(
    state: CharacterRenderState<Body>,
    builder: Polygon2dBuilder,
    aabb: AABB,
) {
    val shoulderWidth = state.config.body.getShoulderWidth(state)
    val offset = state.getSideOffset(shoulderWidth * 0.5f)
    builder.addLeftPoint(aabb, CENTER + offset, START)
}

private fun addHalter(
    state: CharacterRenderState<Body>,
    builder: Polygon2dBuilder,
    aabb: AABB,
) {
    val shoulderWidth = state.config.body.getShoulderWidth(state)
    builder.addMirroredPoints(aabb, shoulderWidth * 0.5f, START)
    builder.addLeftPoint(aabb, CENTER, state.config.body.shoulderY)
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
    builder.addLeftPoint(aabb, CENTER, depth)
}
