package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Shirt
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.LOWER_EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.equipment.part.visualizeSleeves
import at.orchaldir.gm.visualization.character.equipment.part.visualizeTorso

fun visualizeShirt(
    state: CharacterRenderState,
    body: Body,
    shirt: Shirt,
) {
    val fill = shirt.main.getFill(state.state, state.colors)
    val options = FillAndBorder(fill.toRender(), state.config.line)

    visualizeSleeves(state, options, body, shirt.sleeveStyle)
    visualizeTorso(state, options, body, shirt.necklineStyle, LOWER_EQUIPMENT_LAYER)
}