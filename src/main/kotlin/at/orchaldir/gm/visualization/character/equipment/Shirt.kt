package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Shirt
import at.orchaldir.gm.core.model.item.equipment.style.OuterwearLength
import at.orchaldir.gm.core.model.item.equipment.style.SleeveStyle
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.ICharacterConfig
import at.orchaldir.gm.visualization.character.appearance.LOWER_EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.equipment.part.visualizeSleeves
import at.orchaldir.gm.visualization.character.equipment.part.visualizeTorso

data class ShirtConfig(
    val thickness: Factor,
) {
    fun getVolume(
        config: ICharacterConfig<Body>,
        sleeveStyle: SleeveStyle,
    ) = config.equipment().getSleevesVolume(config, sleeveStyle, thickness) +
            config.equipment().getOuterwearBodyVolume(config, OuterwearLength.Hip, thickness)
}

fun visualizeShirt(
    state: CharacterRenderState<Body>,
    shirt: Shirt,
) {
    val options = state.getFillAndBorder(shirt.main)

    visualizeSleeves(state, options, shirt.sleeveStyle)
    visualizeTorso(state, options, shirt.neckline, LOWER_EQUIPMENT_LAYER)
}