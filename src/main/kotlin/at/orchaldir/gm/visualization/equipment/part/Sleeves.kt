package at.orchaldir.gm.visualization.equipment.part

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.style.SleeveStyle
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.renderer.RenderOptions
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.character.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.getArmLayer

fun visualizeSleeves(
    state: RenderState,
    options: RenderOptions,
    body: Body,
    style: SleeveStyle,
    layer: Int = EQUIPMENT_LAYER,
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
    val l = getArmLayer(layer, state.renderFront)

    state.renderer.renderRectangle(leftAabb, options, l)
    state.renderer.renderRectangle(rightAabb, options, l)
}
