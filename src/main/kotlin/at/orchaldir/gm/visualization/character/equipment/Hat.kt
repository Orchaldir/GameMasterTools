package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.item.equipment.Hat
import at.orchaldir.gm.core.model.item.equipment.style.HatStyle
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.renderBuilder
import at.orchaldir.gm.visualization.renderRoundedBuilder

data class HatConfig(
    val heightBrim: Factor,
    val heightLow: Factor,
    val heightHigh: Factor,
    val heightVeryHigh: Factor,
    val thickness: Factor,
    val topOffset: Factor,
    val widthBrimNarrow: Factor,
    val widthBrimWide: Factor,
    val widthCoolie: Factor,
) {

    fun getCommonWidth() = FULL + thickness
}

fun visualizeHat(
    state: CharacterRenderState,
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
    state: CharacterRenderState,
    hat: Hat,
) {
    val fill = hat.main.getFill(state.state, state.colors)
    val options = state.config.getLineOptions(fill)
    val y = if (state.renderFront) {
        state.config.head.hatY
    } else {
        state.config.head.mouth.y
    }

    renderBuilder(state.renderer, buildCrown(state, y, ZERO, y), options, EQUIPMENT_LAYER)
    renderBrim(state, options, state.config.equipment.hat.getCommonWidth(), y)
}

private fun visualizeBoater(
    state: CharacterRenderState,
    hat: Hat,
) {
    val fill = hat.main.getFill(state.state, state.colors)
    val options = state.config.getLineOptions(fill)

    renderBuilder(state.renderer, buildCrown(state), options, EQUIPMENT_LAYER)
    renderBrim(state, options, state.config.equipment.hat.widthBrimWide)
}

private fun visualizeBowler(
    state: CharacterRenderState,
    hat: Hat,
) {
    val fill = hat.main.getFill(state.state, state.colors)
    val options = state.config.getLineOptions(fill)

    val crown = buildCrown(state)
    crown.createSharpCorners(0)

    renderRoundedBuilder(state.renderer, crown, options, EQUIPMENT_LAYER)
    renderBrim(state, options, state.config.equipment.hat.widthBrimNarrow)
}

private fun visualizeCoolie(
    state: CharacterRenderState,
    hat: Hat,
) {
    val fill = hat.main.getFill(state.state, state.colors)
    val options = state.config.getLineOptions(fill)
    val y = state.config.head.hatY

    val builder = Polygon2dBuilder()

    builder.addMirroredPoints(state.aabb, state.config.equipment.hat.widthCoolie, y)
    builder.addLeftPoint(state.aabb, CENTER, y - state.config.equipment.hat.heightHigh)

    renderBuilder(state.renderer, builder, options, EQUIPMENT_LAYER)
}

private fun visualizeCowboy(
    state: CharacterRenderState,
    hat: Hat,
) {
    val fill = hat.main.getFill(state.state, state.colors)
    val options = state.config.getLineOptions(fill)

    val crown = buildCrown(state)
    crown.addLeftPoint(state.aabb, CENTER, fromPercentage(-10))
    crown.createSharpCorners(0)

    renderRoundedBuilder(state.renderer, crown, options, EQUIPMENT_LAYER)
    renderBrim(state, options, state.config.equipment.hat.widthBrimWide)
}

private fun visualizeFez(
    state: CharacterRenderState,
    hat: Hat,
) {
    val fill = hat.main.getFill(state.state, state.colors)
    val options = state.config.getLineOptions(fill)

    renderBuilder(
        state.renderer,
        buildCrown(state, state.config.equipment.hat.heightHigh, -state.config.equipment.hat.topOffset),
        options,
        EQUIPMENT_LAYER
    )
}

private fun visualizePillbox(
    state: CharacterRenderState,
    hat: Hat,
) {
    val fill = hat.main.getFill(state.state, state.colors)
    val options = state.config.getLineOptions(fill)

    renderBuilder(state.renderer, buildCrown(state), options, EQUIPMENT_LAYER)
}

private fun visualizeTopHat(
    state: CharacterRenderState,
    hat: Hat,
) {
    val fill = hat.main.getFill(state.state, state.colors)
    val options = state.config.getLineOptions(fill)

    renderBuilder(
        state.renderer,
        buildCrown(state, state.config.equipment.hat.heightHigh, state.config.equipment.hat.topOffset),
        options,
        EQUIPMENT_LAYER
    )
    renderBrim(state, options, state.config.equipment.hat.widthBrimWide)
}

private fun buildCrown(
    state: CharacterRenderState,
    height: Factor = state.config.equipment.hat.heightLow,
    extraTopWidth: Factor = ZERO,
    y: Factor = state.config.head.hatY,
): Polygon2dBuilder {
    val builder = Polygon2dBuilder()
    val width = state.config.equipment.hat.getCommonWidth()

    builder.addMirroredPoints(state.aabb, width, y)
    builder.addMirroredPoints(state.aabb, width + extraTopWidth, y - height)

    return builder
}

private fun buildBrim(state: CharacterRenderState, width: Factor, height: Factor, y: Factor): Polygon2dBuilder {
    val builder = Polygon2dBuilder()
    val half = height * 0.5f

    builder.addMirroredPoints(state.aabb, width, y + half)
    builder.addMirroredPoints(state.aabb, width, y - half)

    return builder
}

private fun renderBrim(
    state: CharacterRenderState,
    options: FillAndBorder,
    width: Factor,
    y: Factor = state.config.head.hatY,
) {
    renderBuilder(
        state.renderer,
        buildBrim(state, width, state.config.equipment.hat.heightBrim, y),
        options,
        EQUIPMENT_LAYER
    )
}

