package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.style.OuterwearLength
import at.orchaldir.gm.core.model.item.equipment.style.SleeveStyle
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.visualization.character.ICharacterConfig

data class ShirtConfig(
    val thickness: Factor,
) {
    fun getVolume(
        config: ICharacterConfig<Body>,
        sleeveStyle: SleeveStyle,
    ) = config.equipment().getSleevesVolume(config, sleeveStyle, thickness) +
            config.equipment().getOuterwearBodyVolume(config, OuterwearLength.Hip, thickness)
}
