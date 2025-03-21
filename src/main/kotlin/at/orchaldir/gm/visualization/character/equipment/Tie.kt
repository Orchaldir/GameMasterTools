package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Tie
import at.orchaldir.gm.core.model.item.equipment.style.TieStyle
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Polygon2dBuilder
import at.orchaldir.gm.utils.math.START
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.TIE_LAYER
import at.orchaldir.gm.visualization.renderBuilder

fun visualizeTie(
    state: CharacterRenderState,
    body: Body,
    tie: Tie,
) {
    val tieOptions = state.config.getLineOptions(tie.fill)
    val knotOptions = state.config.getLineOptions(tie.knotFill)
    val torso = state.config.body.getTorsoAabb(state.aabb, body)
    val tieBuilder = createTie(torso, tie)
    val knotBuilder = createKnot(torso, tie)

    renderBuilder(state.renderer, knotBuilder, knotOptions, TIE_LAYER)
    if (tieBuilder.isValid()) {
        renderBuilder(state.renderer, tieBuilder, tieOptions, TIE_LAYER)
    }
}

private fun createKnot(torso: AABB, tie: Tie) =
    if (tie.style.isBowTie()) {
        Polygon2dBuilder()
            .addMirroredPoints(torso, Factor(0.1f), START)
            .addMirroredPoints(torso, Factor(0.1f), Factor(0.1f))
    } else {
        Polygon2dBuilder()
            .addMirroredPoints(torso, Factor(0.15f), START)
            .addMirroredPoints(torso, Factor(0.1f), Factor(0.15f))
    }

private fun createTie(torso: AABB, tie: Tie) = when (tie.style) {
    TieStyle.ButterflyBowTie -> Polygon2dBuilder()
    TieStyle.DiamondBowTie -> Polygon2dBuilder()
    TieStyle.KnitTie -> Polygon2dBuilder()
    TieStyle.RoundedBowTie -> Polygon2dBuilder()
    TieStyle.SlimBowTie -> Polygon2dBuilder()
    TieStyle.Tie -> Polygon2dBuilder()
}