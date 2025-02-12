package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.style.SleeveStyle
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.appearance.getArmLayer

fun visualizeSleeves(
    state: CharacterRenderState,
    options: RenderOptions,
    body: Body,
    style: SleeveStyle,
    layerIndex: Int = EQUIPMENT_LAYER,
) {
    val (left, right) = state.config.body.getArmStarts(state.aabb, body)
    val armSize = state.config.body.getArmSize(state.aabb, body)
    val sleeveSize = when (style) {
        SleeveStyle.Long -> armSize
        SleeveStyle.None -> return
        SleeveStyle.Short -> armSize.copy(height = armSize.height * 0.5f)
    }
    val leftAabb = AABB(left, sleeveSize)
    val rightAabb = AABB(right, sleeveSize)
    val layer = state.renderer.getLayer(getArmLayer(layerIndex, state.renderFront))

    layer.renderRectangle(leftAabb, options)
    layer.renderRectangle(rightAabb, options)
}
