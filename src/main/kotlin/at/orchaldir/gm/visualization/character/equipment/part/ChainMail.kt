package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.BodyArmour
import at.orchaldir.gm.core.model.item.equipment.style.ChainMail
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.QUARTER
import at.orchaldir.gm.utils.math.TEN_PERCENTS
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.RenderCircles
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.utils.renderer.model.RenderTiles
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.JACKET_LAYER
import at.orchaldir.gm.visualization.character.equipment.createOuterwearBuilder
import at.orchaldir.gm.visualization.renderBuilder

fun visualizeChainMail(
    state: CharacterRenderState<Body>,
    armour: BodyArmour,
    style: ChainMail,
) {
    val color = style.chain.getColor(state.state, state.colors)
    val fill = RenderCircles(
        Color.Black.toRender(),
        color.toRender(),
        Distance.fromMeters(0.02f),
        Factor.fromPercentage(35),
    )
    val options = FillAndBorder(fill, state.config.line)

    visualizeChainMailBody(state, options, armour)
    visualizeArmourSleeves(state, options, armour)
}

private fun visualizeChainMailBody(
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
