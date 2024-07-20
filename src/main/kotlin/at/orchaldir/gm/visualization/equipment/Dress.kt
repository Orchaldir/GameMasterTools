package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.Dress
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.visualization.RenderState

fun visualizeDress(
    state: RenderState,
    body: Body,
    dress: Dress,
) {
    val options = FillAndBorder(dress.color.toRender(), state.config.line)

    visualizeSleeves(state, options, body, dress.sleeveStyle)
}

