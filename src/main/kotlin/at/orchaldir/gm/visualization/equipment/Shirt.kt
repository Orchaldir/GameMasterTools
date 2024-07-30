package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.Shirt
import at.orchaldir.gm.core.model.item.style.NecklineStyle
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.utils.renderer.RenderOptions
import at.orchaldir.gm.utils.renderer.toRender
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.character.LOWER_EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.createTorso
import at.orchaldir.gm.visualization.equipment.part.addNeckline
import at.orchaldir.gm.visualization.equipment.part.visualizeSleeves
import at.orchaldir.gm.visualization.renderBuilder

fun visualizeShirt(
    state: RenderState,
    body: Body,
    shirt: Shirt,
) {
    val options = FillAndBorder(shirt.fill.toRender(), state.config.line)

    visualizeSleeves(state, options, body, shirt.sleeveStyle)
    visualizeTorso(state, options, body, shirt.necklineStyle)
}

private fun visualizeTorso(
    state: RenderState,
    options: RenderOptions,
    body: Body,
    style: NecklineStyle,
) {
    val builder = createTorso(state, body, style.addTop())
    addNeckline(state, body, builder, style)

    renderBuilder(state, builder, options, LOWER_EQUIPMENT_LAYER)
}