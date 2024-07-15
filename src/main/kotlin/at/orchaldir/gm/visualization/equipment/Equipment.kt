package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.item.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.equipment.part.NecklineConfig

data class EquipmentConfig(
    val footwear: FootwearConfig,
    val neckline: NecklineConfig,
    val pants: PantsConfig,
)

fun visualizeBodyEquipment(
    state: RenderState,
    body: Body,
    equipment: List<Equipment>,
) {
    equipment.forEach {
        when (it) {
            is Footwear -> visualizeFootwear(state, body, it)
            is Pants -> visualizePants(state, body, it)
            is Shirt -> visualizeShirt(state, body, it)
            else -> doNothing()
        }
    }
}

fun visualizeHeadEquipment(
    state: RenderState,
    head: Head,
    equipment: List<Equipment>,
) {
    equipment.forEach {
        when (it) {
            is Hat -> visualizeHat(state, head, it)
            else -> doNothing()
        }
    }
}