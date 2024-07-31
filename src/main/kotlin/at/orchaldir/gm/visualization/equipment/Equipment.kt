package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.item.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.equipment.part.NecklineConfig
import at.orchaldir.gm.visualization.equipment.part.OpeningConfig

data class EquipmentConfig(
    val footwear: FootwearConfig,
    val hat: HatConfig,
    val neckline: NecklineConfig,
    val opening: OpeningConfig,
    val pants: PantsConfig,
    val skirt: SkirtConfig,
)

fun visualizeBodyEquipment(
    state: RenderState,
    body: Body,
) {
    state.equipped.forEach {
        when (it) {
            is Coat -> visualizeCoat(state, body, it)
            is Dress -> visualizeDress(state, body, it)
            is Footwear -> visualizeFootwear(state, body, it)
            is Gloves -> visualizeGloves(state, body, it)
            is Pants -> visualizePants(state, body, it)
            is Shirt -> visualizeShirt(state, body, it)
            is Skirt -> visualizeSkirt(state, body, it)
            else -> doNothing()
        }
    }
}

fun visualizeHeadEquipment(
    state: RenderState,
    head: Head,
) {
    state.equipped.forEach {
        when (it) {
            is Hat -> visualizeHat(state, it)
            else -> doNothing()
        }
    }
}