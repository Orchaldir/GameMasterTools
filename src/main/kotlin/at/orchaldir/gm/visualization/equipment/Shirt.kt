package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.Shirt
import at.orchaldir.gm.core.model.item.style.NecklineStyle
import at.orchaldir.gm.core.model.item.style.SleeveStyle
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.utils.renderer.RenderOptions
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.character.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.HIGHER_EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.LOWER_EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.createTorso
import at.orchaldir.gm.visualization.equipment.part.addNeckline
import at.orchaldir.gm.visualization.renderBuilder

fun visualizeShirt(
    state: RenderState,
    body: Body,
    shirt: Shirt,
) {
    val options = FillAndBorder(shirt.color.toRender(), state.config.line)
    visualizeSleeves(state, options, body, shirt.sleeveStyle)
    visualizeTorso(state, options, body, shirt.necklineStyle)
}

fun visualizeSleeves(
    state: RenderState,
    options: RenderOptions,
    body: Body,
    style: SleeveStyle,
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
    val layer = if (state.renderFront) {
        HIGHER_EQUIPMENT_LAYER
    } else {
        LOWER_EQUIPMENT_LAYER
    }

    state.renderer.renderRectangle(leftAabb, options, layer)
    state.renderer.renderRectangle(rightAabb, options, layer)
}

private fun visualizeTorso(
    state: RenderState,
    options: RenderOptions,
    body: Body,
    style: NecklineStyle,
) {
    val builder = createTorso(state, body, style.addTop())
    addNeckline(state, body, builder, style)

    renderBuilder(state, builder, options, EQUIPMENT_LAYER)
}