package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.item.equipment.style.OuterwearLength
import at.orchaldir.gm.core.model.item.equipment.style.SleeveStyle
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Volume
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.ICharacterConfig
import at.orchaldir.gm.visualization.character.appearance.JACKET_LAYER
import at.orchaldir.gm.visualization.character.appearance.OUTERWEAR_LAYER
import at.orchaldir.gm.visualization.character.equipment.part.NecklineConfig
import at.orchaldir.gm.visualization.character.equipment.part.OpeningConfig

data class EquipmentConfig(
    val armor: BodyArmourConfig,
    val axe: AxeConfig,
    val belt: BeltConfig,
    val club: ClubConfig,
    val coat: CoatConfig,
    val dress: DressConfig,
    val earring: EarringConfig,
    val eyePatch: EyePatchConfig,
    val footwear: FootwearConfig,
    val glasses: GlassesConfig,
    val gloves: GlovesConfig,
    val hat: HatConfig,
    val helmet: HelmetConfig,
    val iounStone: IounStoneConfig,
    val necklace: NecklaceConfig,
    val neckline: NecklineConfig,
    val opening: OpeningConfig,
    val pants: PantsConfig,
    val polearm: PolearmConfig,
    val shield: ShieldConfig,
    val shirt: ShirtConfig,
    val skirt: SkirtConfig,
    val sock: SockConfig,
    val sword: SwordConfig,
    val tie: TieConfig,
) {
    // pantlegs

    fun getPantlegCrossSection(config: ICharacterConfig<Body>): Size2d {
        val factor = config.body().getLegWidth(config)
        return Size2d.square(config.fullAABB().size.width * factor)
    }

    fun getPantlegVolume(
        config: ICharacterConfig<Body>,
        height: Distance,
        thickness: Factor,
    ) = Volume.fromHollowCube(getPantlegCrossSection(config), thickness, height)

    fun getPantlegsVolume(
        config: ICharacterConfig<Body>,
        height: Distance,
        thickness: Factor,
    ) = getPantlegVolume(config, height, thickness) * 2.0f

    // sleeves

    fun getSleeveCrossSection(config: ICharacterConfig<Body>) =
        Size2d.square(config.body().getArmWidth(config))

    fun getSleeveHeightFactor(style: SleeveStyle): Factor? = when (style) {
        SleeveStyle.None -> null
        SleeveStyle.Short -> HALF
        SleeveStyle.Long -> FULL
    }

    fun getSleeveHeight(config: ICharacterConfig<Body>, style: SleeveStyle) =
        config.body().getArmHeight(config) * (getSleeveHeightFactor(style) ?: ZERO)

    fun getSleeveFrontSize(config: ICharacterConfig<Body>, style: SleeveStyle): Size2d? {
        val armSize = config.body().getArmSize(config)
        val heightFactor = getSleeveHeightFactor(style) ?: return null

        return armSize.replaceHeight(heightFactor)
    }

    fun getSleevesVolume(
        config: ICharacterConfig<Body>,
        style: SleeveStyle,
        thickness: Factor,
    ) = getSleevesVolume(config, thickness, getSleeveHeightFactor(style))

    fun getSleevesVolume(
        config: ICharacterConfig<Body>,
        thickness: Factor,
        heightFactor: Factor?,
    ): Volume {
        val height = config.body().getArmHeight(config) * (heightFactor ?: ZERO)
        return Volume.fromHollowCube(getSleeveCrossSection(config), thickness, height) * 2.0f
    }

    // outerwear

    fun getOuterwearCrossSection(config: ICharacterConfig<Body>): Size2d {
        val width = config.torsoAABB().size.width
        val thickness = width * config.body().torsoThicknessRelativeToWidth

        return Size2d(width, thickness)
    }

    fun getOuterwearHeightFactor(
        length: OuterwearLength,
        ankleFactor: Factor = FULL,
    ): Factor = when (length) {
        OuterwearLength.Hip -> ZERO
        OuterwearLength.Knee -> HALF
        OuterwearLength.Ankle -> ankleFactor
    }

    fun getOuterwearHeight(
        config: ICharacterConfig<Body>,
        legHeight: Factor,
    ): Distance {
        val hipToBottom = config.body().getLegHeight(config, legHeight)
        val shouldersToHip = config.torsoAABB().size.height

        return hipToBottom + shouldersToHip
    }

    fun getOuterwearHeight(
        config: ICharacterConfig<Body>,
        length: OuterwearLength,
        ankleFactor: Factor = FULL,
    ) = getOuterwearHeight(config, getOuterwearHeightFactor(length, ankleFactor))

    fun getOuterwearBodyVolume(
        config: ICharacterConfig<Body>,
        height: Distance,
        thickness: Factor,
    ): Volume {
        val size = getOuterwearCrossSection(config)

        return Volume.fromHollowCube(size, thickness, height)
    }

    fun getOuterwearBodyVolume(
        config: ICharacterConfig<Body>,
        length: OuterwearLength,
        thickness: Factor,
    ) = getOuterwearBodyVolume(config, getOuterwearHeight(config, length), thickness)
}

fun visualizeBodyEquipment(state: CharacterRenderState<Body>) {
    state.equipped.getEquipmentWithSlotSets().forEach { (pair, sets) ->
        sets.forEach { set ->
            val newState = state.copy(colors = pair.second)

            when (val data = pair.first) {
                is OneHandedAxe -> visualizeAxe(newState, data.head, data.shaft, data.fixation, true, set)
                is TwoHandedAxe -> visualizeAxe(newState, data.head, data.shaft, data.fixation, false, set)
                is Belt -> visualizeBelt(newState, data)
                is BodyArmour -> visualizeBodyArmour(newState, data)
                is OneHandedClub -> visualizeClub(
                    newState,
                    data.head,
                    data.size,
                    data.shaft,
                    data.fixation,
                    true,
                    set
                )

                is TwoHandedClub -> visualizeClub(
                    newState,
                    data.head,
                    data.size,
                    data.shaft,
                    data.fixation,
                    false,
                    set
                )

                is Coat -> visualizeCoat(newState, data, OUTERWEAR_LAYER)
                is Dress -> visualizeDress(newState, data)
                is Footwear -> visualizeFootwear(newState, data)
                is Gloves -> visualizeGloves(newState, data)
                is Helmet -> visualizeHelmetForBody(newState, data)
                is Necklace -> visualizeNecklace(newState, data)
                is Pants -> visualizePants(newState, data)
                is Polearm -> visualizePolearm(newState, data, set)
                is Shield -> visualizeShield(newState, data, set)
                is Shirt -> visualizeShirt(newState, data)
                is Skirt -> visualizeSkirt(newState, data)
                is Socks -> visualizeSocks(newState, data)
                is SuitJacket -> visualizeCoat(newState, data.convert(), JACKET_LAYER)
                is OneHandedSword -> visualizeSword(newState, data.blade, data.hilt, true, set)
                is TwoHandedSword -> visualizeSword(newState, data.blade, data.hilt, false, set)
                is Tie -> visualizeTie(newState, data)
                else -> doNothing()
            }
        }
    }
}

fun visualizeHeadEquipment(
    state: CharacterRenderState<Head>,
) {
    state.equipped.getEquipmentWithSlotSets().forEach { (pair, sets) ->
        sets.forEach { set ->
            val newState = state.copy(colors = pair.second)

            when (val data = pair.first) {
                is Earring -> visualizeEarring(newState, data, set)
                is EyePatch -> visualizeEyePatch(newState, data, set)
                is Glasses -> visualizeGlasses(newState, data)
                is Hat -> visualizeHat(newState, data)
                is Helmet -> visualizeHelmetForHead(newState, data)
                is IounStone -> visualizeIounStone(newState, data, set)
                else -> doNothing()
            }
        }
    }
}

private fun SuitJacket.convert() = Coat(
    OuterwearLength.Hip,
    necklineStyle,
    sleeveStyle,
    openingStyle,
    pocketStyle,
    main
)