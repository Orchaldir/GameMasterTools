package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.Equipment
import at.orchaldir.gm.core.model.item.NoEquipment
import at.orchaldir.gm.core.model.item.Pants
import at.orchaldir.gm.core.model.item.Shirt
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.renderer.Renderer
import at.orchaldir.gm.visualization.RenderConfig
import at.orchaldir.gm.visualization.equipment.part.NecklineConfig

data class EquipmentConfig(
    val neckline: NecklineConfig,
    val pants: PantsConfig,
)

fun visualizeBodyEquipment(
    renderer: Renderer,
    config: RenderConfig,
    aabb: AABB,
    body: Body,
    equipment: List<Equipment>,
) {
    equipment.forEach {
        when (it) {
            NoEquipment -> doNothing()
            is Pants -> visualizePants(renderer, config, aabb, body, it)
            is Shirt -> visualizeShirt(renderer, config, aabb, body, it)
        }
    }
}