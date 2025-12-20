package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.style.SleeveStyle
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.HALF
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
    if (style == SleeveStyle.None) {
        return
    }
    val (leftAabb, rightAabb) = createSleeveAabbs(state, body, style)
    val layer = state.renderer.getLayer(getArmLayer(layerIndex, state.renderFront))

    layer.renderRectangle(leftAabb, options)
    layer.renderRectangle(rightAabb, options)
}

fun createSleeveAabbs(
    state: CharacterRenderState,
    body: Body,
    style: SleeveStyle,
): Pair<AABB, AABB> {
    val (left, right) = state.config.body.getArmStarts(state.aabb, body)
    val sleeveSize = state.config.equipment.getSleeveSize(state.config, body, state.aabb, style)
        ?: error("Cannot create sleeves for style None!")
    val leftAabb = AABB(left, sleeveSize)
    val rightAabb = AABB(right, sleeveSize)

    return Pair(leftAabb, rightAabb)
}
