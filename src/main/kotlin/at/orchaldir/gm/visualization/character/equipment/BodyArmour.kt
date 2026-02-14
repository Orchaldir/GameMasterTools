package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.BodyArmour
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.unit.Volume
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.ICharacterConfig
import at.orchaldir.gm.visualization.character.equipment.part.*

data class BodyArmourConfig(
    val thicknessChainMail: Factor,
    val thicknessLamellar: Factor,
    val thicknessScale: Factor,
    val thicknessSegmented: Factor,
    val lamellar: LamellarArmourConfig,
) {

    fun getThickness(style: ArmourStyle) = when (style) {
        is ChainMail -> thicknessChainMail
        is Cuirass -> thicknessChainMail // TODO
        is LamellarArmour -> thicknessLamellar
        is ScaleArmour -> thicknessScale
        is SegmentedArmour -> thicknessSegmented
    }

    fun getVolume(
        config: ICharacterConfig<Body>,
        style: ArmourStyle,
        length: OuterwearLength,
        sleeveStyle: SleeveStyle,
    ): Volume {
        val thickness = getThickness(style)
        val sleeves = config.equipment().getSleevesVolume(config, sleeveStyle, thickness)
        val body = config.equipment().getOuterwearBodyVolume(config, length, thickness)

        return sleeves + body
    }

}

fun visualizeBodyArmour(
    state: CharacterRenderState<Body>,
    armour: BodyArmour,
) = when (armour.style) {
    is ChainMail -> visualizeChainMail(state, armour, armour.style)
    is Cuirass -> doNothing()
    is LamellarArmour -> visualizeLamellarArmour(state, armour, armour.style)
    is ScaleArmour -> visualizeScaleArmour(state, armour, armour.style)
    is SegmentedArmour -> visualizeSegmentedArmour(state, armour, armour.style)
}

