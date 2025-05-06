package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.item.equipment.style.OuterwearLength
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.JACKET_LAYER
import at.orchaldir.gm.visualization.character.appearance.OUTERWEAR_LAYER
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
    val necklace: NecklaceConfig,
    val neckline: NecklineConfig,
    val opening: OpeningConfig,
    val pants: PantsConfig,
    val skirt: SkirtConfig,
    val tie: TieConfig,
)

fun visualizeBodyEquipment(
    state: CharacterRenderState,
    body: Body,
) {
    state.equipped.getAllEquipment().forEach {
        when (it) {
            is Belt -> visualizeBelt(state, body, it)
            is Coat -> visualizeCoat(state, body, it, OUTERWEAR_LAYER)
            is Dress -> visualizeDress(state, body, it)
            is Footwear -> visualizeFootwear(state, body, it)
            is Gloves -> visualizeGloves(state, body, it)
            is Necklace -> visualizeNecklace(state, body, it)
            is Pants -> visualizePants(state, body, it)
            is Shirt -> visualizeShirt(state, body, it)
            is Skirt -> visualizeSkirt(state, body, it)
            is Socks -> visualizeSocks(state, body, it)
            is SuitJacket -> visualizeCoat(state, body, it.convert(), JACKET_LAYER)
            is Tie -> visualizeTie(state, body, it)
            else -> doNothing()
        }
    }
}

fun visualizeHeadEquipment(
    state: CharacterRenderState,
    head: Head,
) {
    state.equipped.getEquipmentWithSlotSets().forEach { (data, sets) ->
        sets.forEach { set ->
            when (data) {
                is Earring -> visualizeEarring(state, head, data, set)
                is EyePatch -> visualizeEyePatch(state, head, data, set)
                is Glasses -> visualizeGlasses(state, head, data)
                is Hat -> visualizeHat(state, data)
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