package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.equipment.part.NecklineConfig
import at.orchaldir.gm.visualization.character.equipment.part.OpeningConfig

data class EquipmentConfig(
    val belt: BeltConfig,
    val coat: CoatConfig,
    val footwear: FootwearConfig,
    val glasses: GlassesConfig,
    val hat: HatConfig,
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
    state.equipped.forEach {
        when (it) {
            is Belt -> visualizeBelt(state, body, it)
            is Coat -> visualizeCoat(state, body, it)
            is Dress -> visualizeDress(state, body, it)
            is Footwear -> visualizeFootwear(state, body, it)
            is Gloves -> visualizeGloves(state, body, it)
            is Pants -> visualizePants(state, body, it)
            is Shirt -> visualizeShirt(state, body, it)
            is Skirt -> visualizeSkirt(state, body, it)
            is Socks -> visualizeSocks(state, body, it)
            is Tie -> visualizeTie(state, body, it)
            else -> doNothing()
        }
    }
}

fun visualizeHeadEquipment(
    state: CharacterRenderState,
    head: Head,
) {
    state.equipped.forEach {
        when (it) {
            is Glasses -> visualizeGlasses(state, head, it)
            is Hat -> visualizeHat(state, it)
            else -> doNothing()
        }
    }
}