package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.Dress
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.character.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.addTorso
import at.orchaldir.gm.visualization.renderBuilder

fun visualizeDress(
    state: RenderState,
    body: Body,
    dress: Dress,
) {
    val options = FillAndBorder(dress.color.toRender(), state.config.line)

    visualizeSleeves(state, options, body, dress.sleeveStyle)
    visualizeDressBody(state, options, body, dress)
}

private fun visualizeDressBody(
    state: RenderState,
    options: FillAndBorder,
    body: Body,
    dress: Dress,
) {
    val builder = createSkirt(state, body, dress.skirtStyle)
    addTorso(state, body, builder)
    addNeckline(state, body, dress.necklineStyle, builder)

    renderBuilder(state, builder, options, EQUIPMENT_LAYER)
}

