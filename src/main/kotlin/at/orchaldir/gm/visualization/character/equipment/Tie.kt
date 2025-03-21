package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Tie
import at.orchaldir.gm.core.model.item.equipment.style.TieStyle
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.TIE_LAYER
import at.orchaldir.gm.visualization.renderBuilder

data class TieConfig(
    val bowTieKnotSize: Factor,
    val tieKnotTop: Factor,
    val tieKnotBottom: Factor,
    val tieWidth: SizeConfig<Factor>,
    val tieEndY: Factor,
)

fun visualizeTie(
    state: CharacterRenderState,
    body: Body,
    tie: Tie,
) {
    val tieOptions = state.config.getLineOptions(tie.fill)
    val knotOptions = state.config.getLineOptions(tie.knotFill)
    val torso = state.config.body.getTorsoAabb(state.aabb, body)
    val tieBuilder = createTie(state, torso, tie)
    val knotBuilder = createKnot(state, torso, tie)

    renderBuilder(state.renderer, knotBuilder, knotOptions, TIE_LAYER)
    if (tieBuilder.isValid()) {
        renderBuilder(state.renderer, tieBuilder, tieOptions, TIE_LAYER)
    }
}

private fun createKnot(state: CharacterRenderState, torso: AABB, tie: Tie): Polygon2dBuilder {
    val config = state.config.equipment.tie

    return if (tie.style.isBowTie()) {
        Polygon2dBuilder()
            .addMirroredPoints(torso, config.bowTieKnotSize, START)
            .addMirroredPoints(torso, config.bowTieKnotSize, config.bowTieKnotSize)
    } else {
        Polygon2dBuilder()
            .addMirroredPoints(torso, config.tieKnotTop, START)
            .addMirroredPoints(torso, config.tieKnotBottom, config.tieKnotTop)
    }
}

private fun createTie(state: CharacterRenderState, torso: AABB, tie: Tie) = when (tie.style) {
    TieStyle.ButterflyBowTie -> Polygon2dBuilder()
    TieStyle.DiamondBowTie -> Polygon2dBuilder()
    TieStyle.KnitTie -> createKnitTie(state, torso, tie)
    TieStyle.RoundedBowTie -> Polygon2dBuilder()
    TieStyle.SlimBowTie -> Polygon2dBuilder()
    TieStyle.Tie -> Polygon2dBuilder()
}

private fun createKnitTie(state: CharacterRenderState, torso: AABB, tie: Tie): Polygon2dBuilder {
    val config = state.config.equipment.tie
    val width = config.tieWidth.convert(tie.size)
    val transitionHeight = width / 2.0f

    return Polygon2dBuilder()
        .addMirroredPoints(torso, config.tieKnotBottom, config.tieKnotTop)
        .addMirroredPoints(torso, width, config.tieKnotTop + transitionHeight)
        .addMirroredPoints(torso, width, config.tieEndY - transitionHeight)
        .addLeftPoint(torso, CENTER, config.tieEndY)
}