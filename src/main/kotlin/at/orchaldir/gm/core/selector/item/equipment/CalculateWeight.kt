package at.orchaldir.gm.core.selector.item.equipment

import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.item.equipment.Belt
import at.orchaldir.gm.core.model.item.equipment.BodyArmour
import at.orchaldir.gm.core.model.item.equipment.Coat
import at.orchaldir.gm.core.model.item.equipment.Dress
import at.orchaldir.gm.core.model.item.equipment.Earring
import at.orchaldir.gm.core.model.item.equipment.EquipmentData
import at.orchaldir.gm.core.model.item.equipment.EyePatch
import at.orchaldir.gm.core.model.item.equipment.Footwear
import at.orchaldir.gm.core.model.item.equipment.Glasses
import at.orchaldir.gm.core.model.item.equipment.Gloves
import at.orchaldir.gm.core.model.item.equipment.Hat
import at.orchaldir.gm.core.model.item.equipment.Helmet
import at.orchaldir.gm.core.model.item.equipment.IounStone
import at.orchaldir.gm.core.model.item.equipment.Necklace
import at.orchaldir.gm.core.model.item.equipment.OneHandedAxe
import at.orchaldir.gm.core.model.item.equipment.OneHandedClub
import at.orchaldir.gm.core.model.item.equipment.OneHandedSword
import at.orchaldir.gm.core.model.item.equipment.Pants
import at.orchaldir.gm.core.model.item.equipment.Polearm
import at.orchaldir.gm.core.model.item.equipment.Shield
import at.orchaldir.gm.core.model.item.equipment.Shirt
import at.orchaldir.gm.core.model.item.equipment.Skirt
import at.orchaldir.gm.core.model.item.equipment.Socks
import at.orchaldir.gm.core.model.item.equipment.SuitJacket
import at.orchaldir.gm.core.model.item.equipment.Tie
import at.orchaldir.gm.core.model.item.equipment.TwoHandedAxe
import at.orchaldir.gm.core.model.item.equipment.TwoHandedClub
import at.orchaldir.gm.core.model.item.equipment.TwoHandedSword
import at.orchaldir.gm.utils.math.unit.WeightPerMaterial
import at.orchaldir.gm.visualization.character.ICharacterConfig
import at.orchaldir.gm.visualization.character.appearance.BodyConfig
import at.orchaldir.gm.visualization.character.appearance.HeadConfig
import at.orchaldir.gm.visualization.character.equipment.EquipmentConfig

data class CalculateWeightConfig(
    val appearance: Appearance,
    val body: BodyConfig,
    val equipment: EquipmentConfig,
    val head: HeadConfig,
): ICharacterConfig {

    override fun body() = body
    override fun equipment() = equipment
    override fun head() = head
}

fun calculateWeightPerMaterial(config: CalculateWeightConfig, data: EquipmentData): WeightPerMaterial {
    val wpm = WeightPerMaterial()

    when (data) {
        is Belt -> TODO()
        is BodyArmour -> TODO()
        is Coat -> TODO()
        is Dress -> TODO()
        is Earring -> TODO()
        is EyePatch -> TODO()
        is Footwear -> TODO()
        is Glasses -> TODO()
        is Gloves -> TODO()
        is Hat -> TODO()
        is Helmet -> TODO()
        is IounStone -> TODO()
        is Necklace -> TODO()
        is OneHandedAxe -> TODO()
        is OneHandedClub -> TODO()
        is OneHandedSword -> TODO()
        is Pants -> TODO()
        is Polearm -> TODO()
        is Shield -> TODO()
        is Shirt -> TODO()
        is Skirt -> TODO()
        is Socks -> TODO()
        is SuitJacket -> TODO()
        is Tie -> TODO()
        is TwoHandedAxe -> TODO()
        is TwoHandedClub -> TODO()
        is TwoHandedSword -> TODO()
    }

    return wpm
}