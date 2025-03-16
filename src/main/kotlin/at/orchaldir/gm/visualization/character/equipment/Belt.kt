package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Belt
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.BodyConfig
import at.orchaldir.gm.visualization.character.appearance.EQUIPMENT_LAYER

data class BeltConfig(
    val bandHeight: Factor,
    val y: Factor,
)

fun visualizeBelt(
    state: CharacterRenderState,
    body: Body,
    belt: Belt,
) {
    val torsoAABB = state.config.body.getTorsoAabb(state.aabb, body)

    visualizeBeltBand(state, body, torsoAABB, belt)
}

private fun visualizeBeltBand(
    state: CharacterRenderState,
    body: Body,
    torsoAABB: AABB,
    belt: Belt,
) {
    val hipWidth = state.config.equipment.pants.getHipWidth(state.config.body, body)
    val polygon = Polygon2dBuilder()
}
