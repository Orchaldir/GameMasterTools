package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.Coat
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.utils.renderer.toRender
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.equipment.part.visualizeOpening
import at.orchaldir.gm.visualization.equipment.part.visualizeSleeves
import at.orchaldir.gm.visualization.equipment.part.visualizeTorso

fun visualizeCoat(
    state: RenderState,
    body: Body,
    coat: Coat,
) {
    val options = FillAndBorder(coat.fill.toRender(), state.config.line)

    visualizeSleeves(state, options, body, coat.sleeveStyle)
    visualizeTorso(state, options, body, coat.necklineStyle)

    if (state.renderFront) {
        val necklineHeight = state.config.equipment.neckline.getHeight(coat.necklineStyle)
        val torsoAabb = state.config.body.getTorsoAabb(state.aabb, body)
        visualizeOpening(state, torsoAabb, HALF, necklineHeight, FULL, coat.openingStyle)
    }
}