package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.item.Hat
import at.orchaldir.gm.core.model.item.HatStyle
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.character.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.renderBuilder
import at.orchaldir.gm.visualization.renderRoundedBuilder

data class HatConfig(
    val heightBrim: Factor,
    val heightCommon: Factor,
    val thickness: Factor,
) {

    fun getCommonWidth() = FULL + thickness
}

fun visualizeHat(
    state: RenderState,
    hat: Hat,
) {
    when (hat.style) {
        HatStyle.Beanie -> visualizeBeanie(state, hat)
        HatStyle.Boater -> visualizeBoater(state, hat)
        HatStyle.Bowler -> visualizeBowler(state, hat)
        HatStyle.Coolie -> visualizeCoolie(state, hat)
        HatStyle.Cowboy -> visualizeCowboy(state, hat)
        HatStyle.Fez -> visualizeFez(state, hat)
        HatStyle.Pillbox -> visualizePillbox(state, hat)
        HatStyle.TopHat -> visualizeTopHat(state, hat)
    }
}

private fun visualizeBeanie(
    state: RenderState,
    hat: Hat,
) {
    val options = FillAndBorder(hat.color.toRender(), state.config.line)
    val y = if (state.renderFront) {
        state.config.head.hairlineY
    } else {
        state.config.head.mouthY
    }

    renderBuilder(state, buildCrown(state, y, ZERO, y), options, EQUIPMENT_LAYER)
    renderBrim(state, options, state.config.equipment.hat.getCommonWidth(), y)
}

private fun visualizeBoater(
    state: RenderState,
    hat: Hat,
) {
    val options = FillAndBorder(hat.color.toRender(), state.config.line)

    renderBuilder(state, buildCrown(state), options, EQUIPMENT_LAYER)
    renderBrim(state, options, Factor(1.5f))
}

private fun visualizeBowler(
    state: RenderState,
    hat: Hat,
) {
    val options = FillAndBorder(hat.color.toRender(), state.config.line)

    val crown = buildCrown(state)
    crown.createSharpCorners(0)

    renderRoundedBuilder(state, crown, options, EQUIPMENT_LAYER)
    renderBrim(state, options, Factor(1.3f))
}

private fun visualizeCoolie(
    state: RenderState,
    hat: Hat,
) {
    val options = FillAndBorder(hat.color.toRender(), state.config.line)
    val y = state.config.head.hairlineY

    val builder = Polygon2dBuilder()

    builder.addMirroredPoints(state.aabb, Factor(1.7f), y)
    builder.addPoint(state.aabb, CENTER, y - Factor(0.6f))

    renderBuilder(state, builder, options, EQUIPMENT_LAYER)
}

private fun visualizeCowboy(
    state: RenderState,
    hat: Hat,
) {
    val options = FillAndBorder(hat.color.toRender(), state.config.line)

    val crown = buildCrown(state)
    crown.addPoint(state.aabb, CENTER, Factor(-0.1f))
    crown.createSharpCorners(0)

    renderRoundedBuilder(state, crown, options, EQUIPMENT_LAYER)
    renderBrim(state, options, Factor(1.7f))
}

private fun visualizeFez(
    state: RenderState,
    hat: Hat,
) {
    val options = FillAndBorder(hat.color.toRender(), state.config.line)

    renderBuilder(state, buildCrown(state, Factor(0.6f), Factor(-0.08f)), options, EQUIPMENT_LAYER)
}

private fun visualizePillbox(
    state: RenderState,
    hat: Hat,
) {
    val options = FillAndBorder(hat.color.toRender(), state.config.line)

    renderBuilder(state, buildCrown(state), options, EQUIPMENT_LAYER)
}

private fun visualizeTopHat(
    state: RenderState,
    hat: Hat,
) {
    val options = FillAndBorder(hat.color.toRender(), state.config.line)
    val y = state.config.head.hairlineY

    renderBuilder(state, buildCrown(state, Factor(0.6f), Factor(0.1f)), options, EQUIPMENT_LAYER)
    renderBuilder(state, buildBrim(state, Factor(1.5f), Factor(0.1f), y), options, EQUIPMENT_LAYER)
}

private fun buildCrown(
    state: RenderState,
    height: Factor = state.config.equipment.hat.heightCommon,
    extraTopWidth: Factor = ZERO,
    y: Factor = state.config.head.hairlineY,
): Polygon2dBuilder {
    val builder = Polygon2dBuilder()
    val width = state.config.equipment.hat.getCommonWidth()

    builder.addMirroredPoints(state.aabb, width, y)
    builder.addMirroredPoints(state.aabb, width + extraTopWidth, y - height)

    return builder
}

private fun buildBrim(state: RenderState, width: Factor, height: Factor, y: Factor): Polygon2dBuilder {
    val builder = Polygon2dBuilder()
    val half = height * 0.5f

    builder.addMirroredPoints(state.aabb, width, y + half)
    builder.addMirroredPoints(state.aabb, width, y - half)

    return builder
}

private fun renderBrim(
    state: RenderState,
    options: FillAndBorder,
    width: Factor,
    y: Factor = state.config.head.hairlineY,
) {
    renderBuilder(
        state,
        buildBrim(state, width, state.config.equipment.hat.heightBrim, y),
        options,
        EQUIPMENT_LAYER
    )
}

