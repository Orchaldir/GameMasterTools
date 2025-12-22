package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.BodyArmour
import at.orchaldir.gm.core.model.item.equipment.style.ChainMail
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.JACKET_LAYER
import at.orchaldir.gm.visualization.character.equipment.createOuterwearBuilder
import at.orchaldir.gm.visualization.renderBuilder

fun visualizeChainMail(
    state: CharacterRenderState<Body>,
    armour: BodyArmour,
    style: ChainMail,
) {
    val fill = style.chain.getColor(state.state, state.colors)
    val options = FillAndBorder(fill.toRender(), state.config.line)

    visualizeChainMailBody(state, options, armour)
    visualizeArmourSleeves(state, options, armour)
}

private fun visualizeChainMailBody(
    state: CharacterRenderState<Body>,
    options: RenderOptions,
    armour: BodyArmour,
) {
    val builder = createOuterwearBuilder(state, armour.length)

    renderBuilder(state.renderer, builder, options, JACKET_LAYER)
}

private fun visualizeArmourSleeves(
    state: CharacterRenderState<Body>,
    options: RenderOptions,
    armour: BodyArmour,
) {
    visualizeSleeves(state, options, armour.sleeveStyle, JACKET_LAYER)
}
