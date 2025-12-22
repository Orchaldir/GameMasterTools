package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Dress
import at.orchaldir.gm.core.model.item.equipment.style.SkirtStyle
import at.orchaldir.gm.core.model.item.equipment.style.SleeveStyle
import at.orchaldir.gm.utils.math.DOUBLE
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.THREE_QUARTER
import at.orchaldir.gm.utils.math.unit.Volume
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.ICharacterConfig
import at.orchaldir.gm.visualization.character.appearance.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.appearance.addTorso
import at.orchaldir.gm.visualization.character.equipment.part.addNeckline
import at.orchaldir.gm.visualization.character.equipment.part.visualizeSleeves
import at.orchaldir.gm.visualization.renderBuilder

data class DressConfig(
    val thickness: Factor,
) {
    fun getBottomHeight(skirtStyle: SkirtStyle) = when (skirtStyle) {
        SkirtStyle.ALine -> FULL
        SkirtStyle.Asymmetrical -> THREE_QUARTER
        SkirtStyle.BallGown -> DOUBLE
        SkirtStyle.Mini -> THREE_QUARTER
        SkirtStyle.Sheath -> FULL
    }

    fun getBodyVolume(
        config: ICharacterConfig<Body>,
        style: SkirtStyle,
    ): Volume {
        val bottomHeight = getBottomHeight(style)
        val height = config.equipment().getOuterwearHeight(config, bottomHeight)

        return config.equipment().getOuterwearBodyVolume(config, height, thickness)
    }

    fun getVolume(
        config: ICharacterConfig<Body>,
        skirtStyle: SkirtStyle,
        sleeveStyle: SleeveStyle,
    ) = getBodyVolume(config, skirtStyle) + config.equipment().getSleevesVolume(config, sleeveStyle, thickness)
}

fun visualizeDress(
    state: CharacterRenderState<Body>,
    dress: Dress,
) {
    val fill = dress.main.getFill(state.state, state.colors)
    val options = FillAndBorder(fill.toRender(), state.config.line)

    visualizeSleeves(state, options, dress.sleeveStyle)
    visualizeDressBody(state, options, dress)
}

private fun visualizeDressBody(
    state: CharacterRenderState<Body>,
    options: FillAndBorder,
    dress: Dress,
) {
    val builder = createSkirt(state, dress.skirtStyle)
    addTorso(state, builder, dress.necklineStyle.addTop())
    addNeckline(state, builder, dress.necklineStyle)

    renderBuilder(state.renderer, builder, options, EQUIPMENT_LAYER)
}

