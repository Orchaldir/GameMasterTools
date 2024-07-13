package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.NecklineStyle
import at.orchaldir.gm.core.model.item.Shirt
import at.orchaldir.gm.core.model.item.SleeveStyle
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.character.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.createTorso
import at.orchaldir.gm.visualization.equipment.part.addNeckline

fun visualizeShirt(
    state: RenderState,
    body: Body,
    shirt: Shirt,
) {
    visualizeSleeves(state, body, shirt.sleeveStyle, shirt.color)
    visualizeTorso(state, body, shirt.necklineStyle, shirt.color)
}

private fun visualizeSleeves(
    state: RenderState,
    body: Body,
    style: SleeveStyle,
    color: Color,
) {
    val options = FillAndBorder(color.toRender(), state.config.line)
    val (left, right) = state.config.body.getArmStarts(state.aabb, body)
    val armSize = state.config.body.getArmSize(state.aabb, body)

    val sleeveSize = when (style) {
        SleeveStyle.Long -> armSize
        SleeveStyle.None -> return
        SleeveStyle.Short -> armSize.copy(height = armSize.height * 0.5f)
    }

    val leftAabb = AABB(left, sleeveSize)
    val rightAabb = AABB(right, sleeveSize)

    state.renderer.renderRectangle(leftAabb, options, EQUIPMENT_LAYER)
    state.renderer.renderRectangle(rightAabb, options, EQUIPMENT_LAYER)
}

private fun visualizeTorso(
    state: RenderState,
    body: Body,
    style: NecklineStyle,
    color: Color,
) {
    val options = FillAndBorder(color.toRender(), state.config.line)
    val builder = createTorso(state.config, state.aabb, body)
    val torsoAabb = state.config.body.getTorsoAabb(state.aabb, body)

    addNeckline(builder, state.config, torsoAabb, style)

    val polygon = builder.build()

    state.renderer.renderPolygon(polygon, options, EQUIPMENT_LAYER)
}
