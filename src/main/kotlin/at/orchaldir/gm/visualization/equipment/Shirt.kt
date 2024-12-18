package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.Shirt
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.character.LOWER_EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.equipment.part.visualizeSleeves
import at.orchaldir.gm.visualization.equipment.part.visualizeTorso

fun visualizeShirt(
    state: RenderState,
    body: Body,
    shirt: Shirt,
) {
    val options = FillAndBorder(shirt.fill.toRender(), state.config.line)

    visualizeSleeves(state, options, body, shirt.sleeveStyle)
    visualizeTorso(state, options, body, shirt.necklineStyle, LOWER_EQUIPMENT_LAYER)
}