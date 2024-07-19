package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.Skirt
import at.orchaldir.gm.core.model.item.style.SkirtStyle
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Polygon2dBuilder
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.utils.renderer.RenderOptions
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.character.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.addHip
import at.orchaldir.gm.visualization.renderBuilder

fun visualizeSkirt(
    state: RenderState,
    body: Body,
    skirt: Skirt,
) {
    val options = FillAndBorder(skirt.color.toRender(), state.config.line)

    when (skirt.style) {
        SkirtStyle.Mini -> visualizeSkirt(state, body, options, Factor(0.4f))
        SkirtStyle.Sheath -> visualizeSkirt(state, body, options, Factor(0.9f))
    }
}

private fun visualizeSkirt(
    state: RenderState,
    body: Body,
    options: RenderOptions,
    height: Factor,
) {
    val builder = Polygon2dBuilder()
    val padding = Factor(1.05f)
    val width = state.config.body.getLegsWidth(body) * padding
    val bottomY = state.config.body.getLegY(body, height)

    builder.addMirroredPoints(state.aabb, width, bottomY)
    addHip(state.config, builder, state.aabb, body, padding)

    renderBuilder(state, builder, options, EQUIPMENT_LAYER)
}

