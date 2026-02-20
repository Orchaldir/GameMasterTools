package at.orchaldir.gm.core.selector.item.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.economy.money.CalculatedPrice
import at.orchaldir.gm.core.model.economy.money.UserDefinedPrice
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.item.equipment.style.OuterwearLength
import at.orchaldir.gm.core.model.item.equipment.style.SimpleBuckle
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.unit.CalculatedWeight
import at.orchaldir.gm.utils.math.unit.UserDefinedWeight
import at.orchaldir.gm.utils.math.unit.VolumePerMaterial
import at.orchaldir.gm.visualization.character.CharacterRenderConfig
import at.orchaldir.gm.visualization.character.ICharacterConfig
import at.orchaldir.gm.visualization.character.appearance.BodyConfig
import at.orchaldir.gm.visualization.character.appearance.HeadConfig
import at.orchaldir.gm.visualization.character.equipment.EquipmentConfig

val VOLUME_CONFIG = CalculateVolumeConfig.from(CHARACTER_CONFIG)

data class CalculateVolumeConfig<T>(
    val appearance: T,
    val fullAABB: AABB,
    val headAABB: AABB?,
    val torsoAABB: AABB?,
    val body: BodyConfig,
    val equipment: EquipmentConfig,
    val head: HeadConfig,
) : ICharacterConfig<T> {

    companion object {

        fun from(
            config: CharacterRenderConfig,
            appearance: Appearance = HumanoidBody(),
        ): CalculateVolumeConfig<Appearance> {
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

fun calculatePrice(
    state: State,
    config: CalculateVolumeConfig<Appearance>,
    equipment: Equipment,
    appearance: Appearance = HumanoidBody(),
) = when (equipment.price) {
    CalculatedPrice -> calculatePrice(state, config, equipment.data, appearance)
    is UserDefinedPrice -> equipment.price.price
}

fun calculatePrice(
    state: State,
    config: CalculateVolumeConfig<Appearance>,
    data: EquipmentData,
    appearance: Appearance = HumanoidBody(),
) = calculateVolumePerMaterial(config, data, appearance)
    .getPrice(state)

fun calculatePrice(
    state: State,
    config: CalculateVolumeConfig<Appearance>,
    map: EquipmentIdMap,
    appearance: Appearance = HumanoidBody(),
) = map.getAllEquipment()
    .map { (id, _) -> state.getEquipmentStorage().getOrThrow(id) }
    .map { equipment -> calculatePrice(state, config, equipment, appearance) }
    .reduceOrNull { total, price -> total + price }

fun calculateWeight(
    state: State,
    config: CalculateVolumeConfig<Appearance>,
    equipment: Equipment,
    appearance: Appearance = HumanoidBody(),
) = when (equipment.weight) {
    CalculatedWeight -> calculateWeight(state, config, equipment.data, appearance)
    is UserDefinedWeight -> equipment.weight.weight
}

fun calculateWeight(
    state: State,
    config: CalculateVolumeConfig<Appearance>,
    data: EquipmentData,
    appearance: Appearance = HumanoidBody(),
) = calculateVolumePerMaterial(config, data, appearance)
    .getWeight(state)

fun calculateWeight(
    state: State,
    config: CalculateVolumeConfig<Appearance>,
    map: EquipmentIdMap,
    appearance: Appearance = HumanoidBody(),
) = map.getAllEquipment()
    .map { (id, _) -> state.getEquipmentStorage().getOrThrow(id) }
    .map { equipment -> calculateWeight(state, config, equipment, appearance) }
    .reduceOrNull { total, weight -> total + weight }

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
            // TODO
            val volume =
                config.equipment.armor.getVolume(config, data.style, data.legStyle.upperBodyLength(), data.sleeveStyle)

            vpm.add(data.style.mainMaterial(), volume)
        }

        is Coat -> {
            val volume = config.equipment.coat.getVolume(config, data.length, data.sleeveStyle)

            vpm.add(data.main.material, volume)
        }

        is Dress -> {
            val volume = config.equipment.dress.getVolume(config, data.skirtStyle, data.sleeveStyle)

            vpm.add(data.main.material, volume)
        }

        is Footwear -> {
            val shafts = config.equipment.footwear.getShaftVolume(config, data.style)
            val soles = config.equipment.footwear.getSoleVolume(config, data.style)

            vpm.add(data.shaft.material, shafts)
            vpm.add(data.sole.material, soles)
        }

        is Gloves -> {
            val volume = config.equipment.gloves.getVolume(config, data.style)

            vpm.add(data.main.material, volume)
        }

        is Necklace -> doNothing()
        is OneHandedAxe -> doNothing()
        is OneHandedClub -> doNothing()
        is OneHandedSword -> doNothing()
        is Pants -> {
            val volume = config.equipment.pants.getVolume(config, data.style)

            vpm.add(data.main.material, volume)
        }

        is Polearm -> doNothing()
        is Shield -> doNothing()
        is Shirt -> {
            val volume = config.equipment.shirt.getVolume(config, data.sleeveStyle)

            vpm.add(data.main.material, volume)
        }

        is Skirt -> {
            val volume = config.equipment.skirt.getVolume(config, data.style)

            vpm.add(data.main.material, volume)
        }

        is Socks -> {
            val volume = config.equipment.sock.getVolume(config, data.style)

            vpm.add(data.main.material, volume)
        }

        is SuitJacket -> {
            val volume = config.equipment.coat.getVolume(config, OuterwearLength.Hip, data.sleeveStyle)

            vpm.add(data.main.material, volume)
        }

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