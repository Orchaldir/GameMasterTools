package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Dress
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.appearance.addTorso
import at.orchaldir.gm.visualization.character.equipment.part.addNeckline
import at.orchaldir.gm.visualization.character.equipment.part.visualizeSleeves
import at.orchaldir.gm.visualization.renderBuilder

fun visualizeDress(
    state: CharacterRenderState,
    body: Body,
    dress: Dress,
) {
    val fill = dress.main.getFill(state.state, state.colors)
    val options = FillAndBorder(fill.toRender(), state.config.line)

    visualizeSleeves(state, options, body, dress.sleeveStyle)
    visualizeDressBody(state, options, body, dress)
}

private fun visualizeDressBody(
    state: CharacterRenderState,
    options: FillAndBorder,
    body: Body,
    dress: Dress,
) {
    val builder = createSkirt(state, body, dress.skirtStyle)
    addTorso(state, body, builder, dress.necklineStyle.addTop())
    addNeckline(state, body, builder, dress.necklineStyle)

    renderBuilder(state.renderer, builder, options, EQUIPMENT_LAYER)
}

