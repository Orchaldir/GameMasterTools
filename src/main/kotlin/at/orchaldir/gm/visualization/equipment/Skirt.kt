package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.Skirt
import at.orchaldir.gm.core.model.item.style.SkirtStyle
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.utils.renderer.RenderOptions
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.character.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.createHip
import at.orchaldir.gm.visualization.renderBuilder

fun visualizeSkirt(
    state: RenderState,
    body: Body,
    skirt: Skirt,
) {
    val options = FillAndBorder(skirt.color.toRender(), state.config.line)

    when (skirt.style) {
        SkirtStyle.Mini -> visualizeMini(state, body, skirt, options)
        SkirtStyle.Sheath -> doNothing()
    }
}

private fun visualizeMini(
    state: RenderState,
    body: Body,
    skirt: Skirt,
    options: RenderOptions,
) {
    val builder = createHip(state.config, state.aabb, body)

    renderBuilder(state, builder, options, EQUIPMENT_LAYER)
}

