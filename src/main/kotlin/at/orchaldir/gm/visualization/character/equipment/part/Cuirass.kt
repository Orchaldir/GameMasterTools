package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.BodyArmour
import at.orchaldir.gm.core.model.item.equipment.style.Cuirass
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.JACKET_LAYER
import at.orchaldir.gm.visualization.character.equipment.createOuterwearBuilder
import at.orchaldir.gm.visualization.renderBuilder

fun visualizeCuirass(
    state: CharacterRenderState<Body>,
    armour: BodyArmour,
    style: Cuirass,
) {
    val fill = style.main.getColor(state.state, state.colors)
    val options = FillAndBorder(fill.toRender(), state.config.line)

    visualizeCuirassBody(state, options, armour)
    visualizeArmourSleeves(state, options, armour)
}

private fun visualizeCuirassBody(
    state: CharacterRenderState<Body>,
    options: RenderOptions,
    armour: BodyArmour,
) {
    val builder = createOuterwearBuilder(state, armour.legStyle.length())

    renderBuilder(state.renderer, builder, options, JACKET_LAYER)
}

private fun visualizeArmourSleeves(
    state: CharacterRenderState<Body>,
    options: RenderOptions,
    armour: BodyArmour,
) {
    visualizeSleeves(state, options, armour.sleeveStyle, JACKET_LAYER)
}
