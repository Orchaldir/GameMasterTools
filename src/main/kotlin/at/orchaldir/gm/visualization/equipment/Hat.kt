package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.item.Hat
import at.orchaldir.gm.core.model.item.HatStyle
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.character.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.renderBuilder
import at.orchaldir.gm.visualization.renderRoundedBuilder

data class HatConfig(
    val heightAnkle: Factor,
    val heightKnee: Factor,
    val heightSole: Factor,
    val paddingShaft: Factor,
)

fun visualizeHat(
    state: RenderState,
    head: Head,
    hat: Hat,
) {
    when (hat.style) {
        HatStyle.Beanie -> visualizeBeanie(state, hat)
        HatStyle.Bowler -> visualizeBowler(state, hat)
        HatStyle.Cowboy -> visualizeCowboy(state, hat)
        HatStyle.TopHat -> visualizeTopHat(state, hat)
    }
}

fun visualizeBeanie(
    state: RenderState,
    hat: Hat,
) {
    val options = FillAndBorder(hat.color.toRender(), state.config.line)
    val y = if (state.renderFront) {
        state.config.head.hairlineY
    } else {
        state.config.head.mouthY
    }

    renderBuilder(state, buildCrown(state, y, Factor(0.0f), y), options, EQUIPMENT_LAYER)
    renderBuilder(state, buildBrim(state, Factor(1.05f), Factor(0.1f), y), options, EQUIPMENT_LAYER)
}

fun visualizeBowler(
    state: RenderState,
    hat: Hat,
) {
    val options = FillAndBorder(hat.color.toRender(), state.config.line)
    val y = state.config.head.hairlineY

    val crown = buildCrown(state, Factor(0.4f), ZERO, y)
    crown.createSharpCorners(0)

    renderRoundedBuilder(state, crown, options, EQUIPMENT_LAYER)
    renderBuilder(state, buildBrim(state, Factor(1.3f), Factor(0.1f), y), options, EQUIPMENT_LAYER)
}

fun visualizeCowboy(
    state: RenderState,
    hat: Hat,
) {
    val options = FillAndBorder(hat.color.toRender(), state.config.line)
    val y = state.config.head.hairlineY

    val crown = buildCrown(state, Factor(0.5f), ZERO, y)
    crown.addPoint(state.aabb, HALF, Factor(-0.1f))
    crown.createSharpCorners(0)

    renderRoundedBuilder(state, crown, options, EQUIPMENT_LAYER)
    renderBuilder(state, buildBrim(state, Factor(1.7f), Factor(0.1f), y), options, EQUIPMENT_LAYER)
}

fun visualizeTopHat(
    state: RenderState,
    hat: Hat,
) {
    val options = FillAndBorder(hat.color.toRender(), state.config.line)
    val y = state.config.head.hairlineY

    renderBuilder(state, buildCrown(state, Factor(0.6f), Factor(0.1f), y), options, EQUIPMENT_LAYER)
    renderBuilder(state, buildBrim(state, Factor(1.5f), Factor(0.1f), y), options, EQUIPMENT_LAYER)
}

private fun buildCrown(state: RenderState, height: Factor, extraTopWidth: Factor, y: Factor): Polygon2dBuilder {
    val builder = Polygon2dBuilder()

    builder.addMirroredPoints(state.aabb, FULL, y)
    builder.addMirroredPoints(state.aabb, FULL + extraTopWidth, y - height)

    return builder
}

private fun buildBrim(state: RenderState, width: Factor, height: Factor, y: Factor): Polygon2dBuilder {
    val builder = Polygon2dBuilder()
    val half = height * 0.5f

    builder.addMirroredPoints(state.aabb, width, y + half)
    builder.addMirroredPoints(state.aabb, width, y - half)

    return builder
}

