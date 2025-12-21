package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.BodyArmour
import at.orchaldir.gm.core.model.item.equipment.style.ArmourStyle
import at.orchaldir.gm.core.model.item.equipment.style.ChainMail
import at.orchaldir.gm.core.model.item.equipment.style.LamellarArmour
import at.orchaldir.gm.core.model.item.equipment.style.ScaleArmour
import at.orchaldir.gm.core.model.item.equipment.style.SegmentedArmour
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.ICharacterConfig
import at.orchaldir.gm.visualization.character.equipment.part.LamellarArmourConfig
import at.orchaldir.gm.visualization.character.equipment.part.visualizeChainMail
import at.orchaldir.gm.visualization.character.equipment.part.visualizeLamellarArmour
import at.orchaldir.gm.visualization.character.equipment.part.visualizeScaleArmour
import at.orchaldir.gm.visualization.character.equipment.part.visualizeSegmentedArmour

data class BodyArmourConfig(
    val thicknessChainMail: Factor,
    val thicknessLamellar: Factor,
    val thicknessScale: Factor,
    val thicknessSegmented: Factor,
    val lamellar: LamellarArmourConfig,
) {

    fun getThickness(config: ICharacterConfig<Body>, style: ArmourStyle): Distance {
        val thicknessFactor = when (style) {
            is ChainMail -> thicknessChainMail
            is LamellarArmour -> thicknessLamellar
            is ScaleArmour -> thicknessScale
            is SegmentedArmour -> thicknessSegmented
        }

        return config.torsoAABB().convertHeight(thicknessFactor)
    }

}

fun visualizeBodyArmour(
    state: CharacterRenderState<Body>,
    armour: BodyArmour,
) = when (armour.style) {
    is ChainMail -> visualizeChainMail(state, armour, armour.style)
    is LamellarArmour -> visualizeLamellarArmour(state, armour, armour.style)
    is ScaleArmour -> visualizeScaleArmour(state, armour, armour.style)
    is SegmentedArmour -> visualizeSegmentedArmour(state, armour, armour.style)
}

