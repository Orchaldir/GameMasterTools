package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Tie
import at.orchaldir.gm.core.model.item.equipment.style.TieStyle
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.TIE_LAYER
import at.orchaldir.gm.visualization.renderBuilder
import at.orchaldir.gm.visualization.renderRoundedBuilder

data class TieConfig(
    val bowTieKnotSize: Factor,
    val bowTieWidth: SizeConfig<Factor>,
    val bowTieHeight: Factor,
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
    if (!state.renderFront) {
        return
    }

    val mainFill = tie.main.getFill(state.state, state.colors)
    val knotFill = tie.knot.getFill(state.state, state.colors)
    val tieOptions = state.config.getLineOptions(mainFill)
    val knotOptions = state.config.getLineOptions(knotFill)
    val torso = state.torsoAABB()
    val tieBuilder = createTie(state, torso, tie)
    val knotBuilder = createKnot(state, torso, tie)

    if (tieBuilder.isValid()) {
        if (isRounded(tie)) {
            renderRoundedBuilder(state.renderer, tieBuilder, tieOptions, TIE_LAYER)
        } else {
            renderBuilder(state.renderer, tieBuilder, tieOptions, TIE_LAYER)
        }
    }

    renderBuilder(state.renderer, knotBuilder, knotOptions, TIE_LAYER)
}

private fun isRounded(tie: Tie) = tie.style == TieStyle.RoundedBowTie

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

private fun createTie(state: CharacterRenderState, torso: AABB, tie: Tie): Polygon2dBuilder {
    val config = state.config.equipment.tie

    return when (tie.style) {
        TieStyle.ButterflyBowTie -> createBaseBowTie(config, torso, tie, config.bowTieHeight)
        TieStyle.DiamondBowTie -> createDiamondBowTie(config, torso, tie)
        TieStyle.KnitTie -> createKnitTie(state, torso, tie)
        TieStyle.RoundedBowTie -> createBaseBowTie(config, torso, tie, config.bowTieHeight)
        TieStyle.SlimBowTie -> createBaseBowTie(config, torso, tie, config.bowTieHeight * 0.7f)
        TieStyle.Tie -> createNormalTie(state, torso, tie)
    }
}

private fun createKnitTie(state: CharacterRenderState, torso: AABB, tie: Tie): Polygon2dBuilder {
    val config = state.config.equipment.tie

    return createBaseTie(config, torso, tie, config.tieEndY)
}

private fun createNormalTie(state: CharacterRenderState, torso: AABB, tie: Tie): Polygon2dBuilder {
    val config = state.config.equipment.tie
    val width = config.tieWidth.convert(tie.size)
    val transitionHeight = width / 2.0f

    return createBaseTie(config, torso, tie, config.tieEndY - transitionHeight)
        .addLeftPoint(torso, CENTER, config.tieEndY)
}

private fun createBaseTie(config: TieConfig, torso: AABB, tie: Tie, endY: Factor): Polygon2dBuilder {
    val width = config.tieWidth.convert(tie.size)
    val transitionHeight = width / 2.0f

    return Polygon2dBuilder()
        .addMirroredPoints(torso, config.tieKnotBottom, config.tieKnotTop)
        .addMirroredPoints(torso, width, config.tieKnotTop + transitionHeight)
        .addMirroredPoints(torso, width, endY)
        .addLeftPoint(torso, CENTER, config.tieEndY)
}

private fun createDiamondBowTie(config: TieConfig, torso: AABB, tie: Tie): Polygon2dBuilder {
    val width = config.bowTieWidth.convert(tie.size)
    val rest = width - config.bowTieKnotSize
    val middleWidth = config.bowTieKnotSize + rest / 2.0f
    val height = config.bowTieHeight * width
    val half = height / 2.0f
    val centerY = config.bowTieKnotSize / 2.0f

    return Polygon2dBuilder()
        .addMirroredPoints(torso, config.bowTieKnotSize, START)
        .addMirroredPoints(torso, middleWidth, centerY - half)
        .addMirroredPoints(torso, width, centerY)
        .addMirroredPoints(torso, middleWidth, centerY + half)
        .addMirroredPoints(torso, config.bowTieKnotSize, config.bowTieKnotSize)
}

private fun createBaseBowTie(config: TieConfig, torso: AABB, tie: Tie, relativeHeight: Factor): Polygon2dBuilder {
    val width = config.bowTieWidth.convert(tie.size)
    val height = relativeHeight * width
    val half = height / 2.0f
    val centerY = config.bowTieKnotSize / 2.0f

    return Polygon2dBuilder()
        .addMirroredPoints(torso, config.bowTieKnotSize, START)
        .addMirroredPoints(torso, width, centerY - half)
        .addMirroredPoints(torso, width, centerY + half)
        .addMirroredPoints(torso, config.bowTieKnotSize, config.bowTieKnotSize)
}