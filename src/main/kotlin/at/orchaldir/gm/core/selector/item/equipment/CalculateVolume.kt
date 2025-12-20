package at.orchaldir.gm.core.selector.item.equipment

import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.character.appearance.UndefinedAppearance
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
import at.orchaldir.gm.core.model.item.equipment.style.SimpleBuckle
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.unit.VolumePerMaterial
import at.orchaldir.gm.visualization.character.ICharacterConfig
import at.orchaldir.gm.visualization.character.appearance.BodyConfig
import at.orchaldir.gm.visualization.character.appearance.HeadConfig
import at.orchaldir.gm.visualization.character.equipment.EquipmentConfig

data class CalculateWeightConfig(
    val body: BodyConfig,
    val equipment: EquipmentConfig,
    val head: HeadConfig,
): ICharacterConfig {

    override fun body() = body
    override fun equipment() = equipment
    override fun head() = head
}

fun calculateVolumePerMaterial(config: CalculateWeightConfig, appearance: Appearance, data: EquipmentData): VolumePerMaterial {
    val wpm = VolumePerMaterial()
    val aabb = AABB(appearance.getSize2d())

    when (appearance) {
        is HeadOnly -> calculateVolumePerMaterialForHead(
            config,
            appearance.head,
            aabb,
            data,
            wpm,
        )
        is HumanoidBody -> {
            val headAabb = config.body.getHeadAabb(aabb)

            calculateVolumePerMaterialForBody(
                config,
                appearance.body,
                aabb,
                data,
                wpm,
            )
            calculateVolumePerMaterialForHead(
                config,
                appearance.head,
                headAabb,
                data,
                wpm,
            )
        }
        UndefinedAppearance -> error("Cannot calculate the equipment weight with an undefined appearance!")
    }

    return wpm
}

private fun calculateVolumePerMaterialForBody(
    config: CalculateWeightConfig,
    body: Body,
    aabb: AABB,
    data: EquipmentData,
    wpm: VolumePerMaterial,
) {
    val torsoAABB = config.body.getTorsoAabb(aabb, body)

    when (data) {
        is Belt -> {
            wpm.add(data.strap.material, config.equipment.belt.getBandVolume(config, torsoAABB, body))

            if (data.buckle is SimpleBuckle) {
                val buckleVolume = config.equipment.belt.getBuckleVolume(torsoAABB, data.buckle.shape, data.buckle.size)
                wpm.add(data.buckle.part.material, buckleVolume)
            }
        }
        is BodyArmour -> TODO()
        is Coat -> TODO()
        is Dress -> TODO()
        is Footwear -> TODO()
        is Gloves -> TODO()
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
        else -> error("Equipment type ${data.getType()} is unsupported!")
    }
}

private fun calculateVolumePerMaterialForHead(
    config: CalculateWeightConfig,
    head: Head,
    aabb: AABB,
    data: EquipmentData,
    wpm: VolumePerMaterial,
) {
    when (data) {
        is Earring -> TODO()
        is EyePatch -> TODO()
        is Glasses -> TODO()
        is Hat -> TODO()
        is Helmet -> TODO()
        is IounStone -> TODO()
        else -> error("Equipment type ${data.getType()} is unsupported!")
    }
}