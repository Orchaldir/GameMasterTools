package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.item.equipment.style.OuterwearLength
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.JACKET_LAYER
import at.orchaldir.gm.visualization.character.appearance.OUTERWEAR_LAYER
import at.orchaldir.gm.visualization.character.equipment.part.LamellarArmourConfig
import at.orchaldir.gm.visualization.character.equipment.part.NecklineConfig
import at.orchaldir.gm.visualization.character.equipment.part.OpeningConfig

data class EquipmentConfig(
    val belt: BeltConfig,
    val coat: CoatConfig,
    val earring: EarringConfig,
    val eyePatch: EyePatchConfig,
    val footwear: FootwearConfig,
    val glasses: GlassesConfig,
    val hat: HatConfig,
    val lamellarArmour: LamellarArmourConfig,
    val necklace: NecklaceConfig,
    val neckline: NecklineConfig,
    val opening: OpeningConfig,
    val pants: PantsConfig,
    val polearm: PolearmConfig,
    val shield: ShieldConfig,
    val skirt: SkirtConfig,
    val tie: TieConfig,
)

fun visualizeBodyEquipment(
    state: CharacterRenderState,
    body: Body,
) {
    state.equipped.getEquipmentWithSlotSets().forEach { (pair, sets) ->
        sets.forEach { set ->
            val newState = state.copy(colors = pair.second)

            when (val data = pair.first) {
                is Belt -> visualizeBelt(newState, body, data)
                is BodyArmour -> visualizeBodyArmour(newState, body, data)
                is Coat -> visualizeCoat(newState, body, data, OUTERWEAR_LAYER)
                is Dress -> visualizeDress(newState, body, data)
                is Footwear -> visualizeFootwear(newState, body, data)
                is Gloves -> visualizeGloves(newState, body, data)
                is Necklace -> visualizeNecklace(newState, body, data)
                is Pants -> visualizePants(newState, body, data)
                is Polearm -> visualizePolearm(newState, body, data, set)
                is Shield -> visualizeShield(newState, body, data, set)
                is Shirt -> visualizeShirt(newState, body, data)
                is Skirt -> visualizeSkirt(newState, body, data)
                is Socks -> visualizeSocks(newState, body, data)
                is SuitJacket -> visualizeCoat(newState, body, data.convert(), JACKET_LAYER)
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