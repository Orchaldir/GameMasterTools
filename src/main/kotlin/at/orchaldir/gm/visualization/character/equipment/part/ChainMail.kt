package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.BodyArmour
import at.orchaldir.gm.core.model.item.equipment.style.ChainMail
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.JACKET_LAYER

fun visualizeChainMail(
    state: CharacterRenderState,
    body: Body,
    armour: BodyArmour,
    style: ChainMail,
) {
    val renderer = state.renderer.getLayer(JACKET_LAYER)

    visualizeChainMailBody(state, renderer, body, armour, style)
    visualizeArmourSleeves(state, body, armour, style)
}

private fun visualizeChainMailBody(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    body: Body,
    armour: BodyArmour,
    style: ChainMail,
) {

}

private fun visualizeArmourSleeves(
    state: CharacterRenderState,
    body: Body,
    armour: BodyArmour,
    style: ChainMail,
) {
    val fill = style.chain.getColor(state.state, state.colors)
    val options = FillAndBorder(fill.toRender(), state.config.line)

    visualizeSleeves(state, options, body, armour.sleeveStyle, JACKET_LAYER)
}
