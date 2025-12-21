package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.item.equipment.style.OuterwearLength
import at.orchaldir.gm.core.model.item.equipment.style.SleeveStyle
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Volume
import at.orchaldir.gm.utils.math.unit.ZERO_VOLUME
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
    val hat: HatConfig,
    val helmet: HelmetConfig,
    val iounStone: IounStoneConfig,
    val necklace: NecklaceConfig,
    val neckline: NecklineConfig,
    val opening: OpeningConfig,
    val pants: PantsConfig,
    val polearm: PolearmConfig,
    val shield: ShieldConfig,
    val skirt: SkirtConfig,
    val sword: SwordConfig,
    val tie: TieConfig,
) {
    // sleaves

    fun getSleeveSize(config: ICharacterConfig<Body>, style: SleeveStyle): Size2d? {
        val armSize = config.body().getArmSize(config)

        return when (style) {
            SleeveStyle.Long -> armSize
            SleeveStyle.None -> null
            SleeveStyle.Short -> armSize.replaceHeight(HALF)
        }
    }

    fun getSleevesVolume(
        config: ICharacterConfig<Body>,
        style: SleeveStyle,
        thickness: Distance,
    ): Volume {
        val size = getSleeveSize(config, style) ?: return ZERO_VOLUME
        val numberOfArms = 2.0f
        val numberOfSides = 4.0f

        return size.calculateVolumeOfPrism(thickness) * numberOfArms * numberOfSides
    }

    // outerwear

    fun getOuterwearSize(config: ICharacterConfig<Body>, length: OuterwearLength): Size2d {
        val topY = config.body().shoulderY
        val bottomY = getOuterwearBottomY(config, length)
        val height = bottomY - topY

        return config.torsoAABB().size.scale(FULL, height)
    }

    fun getOuterwearBodyVolume(
        config: ICharacterConfig<Body>,
        length: OuterwearLength,
        thickness: Distance,
    ) = getOuterwearSize(config, length).calculateVolumeOfPrism(thickness) * config.body().getTorsoCircumferenceFactor()
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