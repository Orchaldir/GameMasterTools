package at.orchaldir.gm.core.selector.item.equipment

import at.orchaldir.gm.core.model.State
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
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.unit.VolumePerMaterial
import at.orchaldir.gm.visualization.character.CharacterRenderConfig
import at.orchaldir.gm.visualization.character.ICharacterConfig
import at.orchaldir.gm.visualization.character.appearance.BodyConfig
import at.orchaldir.gm.visualization.character.appearance.HeadConfig
import at.orchaldir.gm.visualization.character.equipment.EquipmentConfig

data class CalculateVolumeConfig<T>(
    val appearance: T,
    val fullAABB: AABB,
    val headAABB: AABB?,
    val torsoAABB: AABB?,
    val body: BodyConfig,
    val equipment: EquipmentConfig,
    val head: HeadConfig,
): ICharacterConfig<T> {

    companion object {

        fun from(config: CharacterRenderConfig, appearance: Appearance = HumanoidBody()): CalculateVolumeConfig<Appearance> {
            val fullAABB = AABB(appearance.getSize2d())
            val headAABB = when (appearance) {
                is HeadOnly -> fullAABB
                is HumanoidBody -> config.body.getHeadAabb(fullAABB)
                UndefinedAppearance -> null
            }
            val torsoAABB = if (appearance is HumanoidBody) {
                config.body.getTorsoAabb(fullAABB, appearance.body)
            } else {
                null
            }

            return CalculateVolumeConfig(
                appearance,
                fullAABB,
                headAABB,
                torsoAABB,
                config.body,
                config.equipment,
                config.head,
            )
        }

    }

    override fun get() = appearance

    override fun fullAABB() = fullAABB
    override fun headAABB() = headAABB ?: error("Head is unsupported!")
    override fun torsoAABB() = torsoAABB ?: error("Head is unsupported!")

    override fun body() = body
    override fun equipment() = equipment
    override fun head() = head
}

fun CalculateVolumeConfig<Appearance>.convert(appearance: Body) = CalculateVolumeConfig(
    appearance,
    fullAABB,
    headAABB,
    torsoAABB,
    body,
    equipment,
    head,
)

fun CalculateVolumeConfig<Appearance>.convert(appearance: Head) = CalculateVolumeConfig(
    appearance,
    fullAABB,
    headAABB,
    torsoAABB,
    body,
    equipment,
    head,
)

fun calculateWeight(
    state: State,
    config: CalculateVolumeConfig<Appearance>,
    data: EquipmentData,
    appearance: Appearance = HumanoidBody(),
) = calculateVolumePerMaterial(config, data, appearance)
    .getWeight(state)

fun calculateVolumePerMaterial(
    config: CalculateVolumeConfig<Appearance>,
    data: EquipmentData,
    appearance: Appearance = HumanoidBody(),
): VolumePerMaterial {
    val vpm = VolumePerMaterial()

    when (appearance) {
        is HeadOnly -> calculateVolumePerMaterialForHead(
            config.convert(appearance.head),
            data,
            vpm,
        )
        is HumanoidBody -> {
            calculateVolumePerMaterialForBody(
                config.convert(appearance.body),
                data,
                vpm,
            )
            calculateVolumePerMaterialForHead(
                config.convert(appearance.head),
                data,
                vpm,
            )
        }
        UndefinedAppearance -> error("Cannot calculate the equipment weight with an undefined appearance!")
    }

    return vpm
}

private fun calculateVolumePerMaterialForBody(
    config: CalculateVolumeConfig<Body>,
    data: EquipmentData,
    vpm: VolumePerMaterial,
) {
    when (data) {
        is Belt -> {
            vpm.add(data.strap.material, config.equipment.belt.getBandVolume(config))

            if (data.buckle is SimpleBuckle) {
                val buckleVolume = config.equipment.belt.getBuckleVolume(config, data.buckle.shape, data.buckle.size)
                vpm.add(data.buckle.part.material, buckleVolume)
            }
        }
        is BodyArmour -> {
            val thickness = config.equipment.armor.getThickness(config, data.style)
            val sleeves = config.equipment.getSleevesVolume(config, data.sleeveStyle, thickness)
            val torso = config.equipment.getOuterwearBodyVolume(config, data.length, thickness)

            vpm.add(data.style.mainMaterial(), torso + sleeves)
        }
        is Coat -> {
            val thickness = config.equipment.coat.getThickness(config)
            val sleeves = config.equipment.getSleevesVolume(config, data.sleeveStyle, thickness)
            val torso = config.equipment.getOuterwearBodyVolume(config, data.length, thickness)

            vpm.add(data.main.material, torso + sleeves)
        }
        is Dress -> {
            val thickness = config.equipment.dress.getThickness(config)
            val sleeves = config.equipment.getSleevesVolume(config, data.sleeveStyle, thickness)
            val torso = config.equipment.dress.getBodyVolume(config, data.skirtStyle, thickness)

            vpm.add(data.main.material, torso + sleeves)
        }
        is Footwear -> {
            val shafts = config.equipment.footwear.getShaftVolume(config, data.style)
            val soles = config.equipment.footwear.getSoleVolume(config, data.style)

            vpm.add(data.shaft.material, shafts)
            vpm.add(data.sole.material, soles)
        }
        is Gloves -> {
            val hands = config.equipment.gloves.getHandsVolume(config)
            val sleeves = config.equipment.gloves.getSleevesVolume(config, data.style)

            vpm.add(data.main.material, hands + sleeves)
        }
        is Necklace -> doNothing()
        is OneHandedAxe -> doNothing()
        is OneHandedClub -> doNothing()
        is OneHandedSword -> doNothing()
        is Pants -> doNothing()
        is Polearm -> doNothing()
        is Shield -> doNothing()
        is Shirt -> doNothing()
        is Skirt -> doNothing()
        is Socks -> doNothing()
        is SuitJacket -> doNothing()
        is Tie -> doNothing()
        is TwoHandedAxe -> doNothing()
        is TwoHandedClub -> doNothing()
        is TwoHandedSword -> doNothing()
        else -> doNothing()
    }
}

private fun calculateVolumePerMaterialForHead(
    config: CalculateVolumeConfig<Head>,
    data: EquipmentData,
    vpm: VolumePerMaterial,
) {
    when (data) {
        is Earring -> doNothing()
        is EyePatch -> doNothing()
        is Glasses -> doNothing()
        is Hat -> doNothing()
        is Helmet -> doNothing()
        is IounStone -> doNothing()
        else -> doNothing()
    }
}