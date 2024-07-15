package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.item.Hat
import at.orchaldir.gm.core.model.item.HatStyle
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Polygon2dBuilder
import at.orchaldir.gm.utils.math.START
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.character.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.renderBuilder

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
        FULL
    }

    renderBuilder(state, buildCrown(state, Factor(0.0f), Factor(0.0f)), options, EQUIPMENT_LAYER)
    renderBuilder(state, buildBrim(state, Factor(1.05f), Factor(0.1f), y), options, EQUIPMENT_LAYER)
}

fun visualizeTopHat(
    state: RenderState,
    hat: Hat,
) {
    val options = FillAndBorder(hat.color.toRender(), state.config.line)

    renderBuilder(state, buildCrown(state, Factor(0.6f), Factor(0.1f)), options, EQUIPMENT_LAYER)
    renderBuilder(
        state,
        buildBrim(state, Factor(1.5f), Factor(0.1f), state.config.head.hairlineY),
        options,
        EQUIPMENT_LAYER
    )
}

private fun buildCrown(state: RenderState, height: Factor, extraTopWidth: Factor): Polygon2dBuilder {
    val builder = Polygon2dBuilder()

    builder.addMirroredPoints(state.aabb, FULL, state.config.head.hairlineY)
    builder.addMirroredPoints(state.aabb, FULL + extraTopWidth, START - height)

    return builder
}

private fun buildBrim(state: RenderState, width: Factor, height: Factor, y: Factor): Polygon2dBuilder {
    val builder = Polygon2dBuilder()
    val half = height * 0.5f

    builder.addMirroredPoints(state.aabb, width, y + half)
    builder.addMirroredPoints(state.aabb, width, y - half)

    return builder
}

