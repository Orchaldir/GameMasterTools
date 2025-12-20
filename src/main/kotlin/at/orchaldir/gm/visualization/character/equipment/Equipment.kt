package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.app.html.race.weightPrefix
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.item.equipment.style.OuterwearLength
import at.orchaldir.gm.core.model.item.equipment.style.SleeveStyle
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Volume
import at.orchaldir.gm.utils.math.unit.WEIGHTLESS
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
    fun getSleeveSize(config: ICharacterConfig, body: Body, torsoAABB: AABB, style: SleeveStyle): Size2d? {
        val armSize = config.body().getArmSize(torsoAABB, body)

        return when (style) {
            SleeveStyle.Long -> armSize
            SleeveStyle.None -> null
            SleeveStyle.Short -> armSize.replaceHeight(HALF)
        }
    }

    fun getAllSidesForSleeves(config: ICharacterConfig, body: Body, torsoAABB: AABB, style: SleeveStyle): Size2d? {
        val size = getSleeveSize(config, body, torsoAABB, style) ?: return null
        val numberOfArms = 2.0f
        val numberOfSides = 4.0f

        return size * numberOfArms * numberOfSides
    }

    fun getSleevesVolume(
        config: ICharacterConfig,
        body: Body,
        torsoAABB: AABB,
        style: SleeveStyle,
        thickness: Distance,
    ) = getAllSidesForSleeves(config, body, torsoAABB, style)?.calculateVolumeOfPrism(thickness) ?: ZERO_VOLUME

    fun getAllSidesForOuterwearBody(config: ICharacterConfig, body: Body, torsoAABB: AABB, length: OuterwearLength): Size2d {
        val topY = config.body().shoulderY
        val bottomY = getOuterwearBottomY(config, body, length)
        val height = bottomY - topY

        return torsoAABB.size.scale(FULL, height) * config.body().getTorsoCircumferenceFactor()
    }

    fun getOuterwearBodyVolume(
        config: ICharacterConfig,
        body: Body,
        torsoAABB: AABB,
        length: OuterwearLength,
        thickness: Distance,
    ) = getAllSidesForOuterwearBody(config, body, torsoAABB, length).calculateVolumeOfPrism(thickness)
}

fun visualizeBodyEquipment(
    state: CharacterRenderState,
    body: Body,
) {
    state.equipped.getEquipmentWithSlotSets().forEach { (pair, sets) ->
        sets.forEach { set ->
            val newState = state.copy(colors = pair.second)

            when (val data = pair.first) {
                is OneHandedAxe -> visualizeAxe(newState, body, data.head, data.shaft, data.fixation, true, set)
                is TwoHandedAxe -> visualizeAxe(newState, body, data.head, data.shaft, data.fixation, false, set)
                is Belt -> visualizeBelt(newState, body, data)
                is BodyArmour -> visualizeBodyArmour(newState, body, data)
                is OneHandedClub -> visualizeClub(
                    newState,
                    body,
                    data.head,
                    data.size,
                    data.shaft,
                    data.fixation,
                    true,
                    set
                )

                is TwoHandedClub -> visualizeClub(
                    newState,
                    body,
                    data.head,
                    data.size,
                    data.shaft,
                    data.fixation,
                    false,
                    set
                )

                is Coat -> visualizeCoat(newState, body, data, OUTERWEAR_LAYER)
                is Dress -> visualizeDress(newState, body, data)
                is Footwear -> visualizeFootwear(newState, body, data)
                is Gloves -> visualizeGloves(newState, body, data)
                is Helmet -> visualizeHelmetForBody(newState, body, data)
                is Necklace -> visualizeNecklace(newState, body, data)
                is Pants -> visualizePants(newState, body, data)
                is Polearm -> visualizePolearm(newState, body, data, set)
                is Shield -> visualizeShield(newState, body, data, set)
                is Shirt -> visualizeShirt(newState, body, data)
                is Skirt -> visualizeSkirt(newState, body, data)
                is Socks -> visualizeSocks(newState, body, data)
                is SuitJacket -> visualizeCoat(newState, body, data.convert(), JACKET_LAYER)
                is OneHandedSword -> visualizeSword(newState, body, data.blade, data.hilt, true, set)
                is TwoHandedSword -> visualizeSword(newState, body, data.blade, data.hilt, false, set)
                is Tie -> visualizeTie(newState, body, data)
                else -> doNothing()
            }
        }
    }
}

fun visualizeHeadEquipment(
    state: CharacterRenderState,
    head: Head,
) {
    state.equipped.getEquipmentWithSlotSets().forEach { (pair, sets) ->
        sets.forEach { set ->
            val newState = state.copy(colors = pair.second)

            when (val data = pair.first) {
                is Earring -> visualizeEarring(newState, head, data, set)
                is EyePatch -> visualizeEyePatch(newState, head, data, set)
                is Glasses -> visualizeGlasses(newState, head, data)
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