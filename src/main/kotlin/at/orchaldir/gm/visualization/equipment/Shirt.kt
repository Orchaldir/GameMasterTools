package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.NecklineStyle
import at.orchaldir.gm.core.model.item.Shirt
import at.orchaldir.gm.core.model.item.SleeveStyle
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.utils.renderer.Renderer
import at.orchaldir.gm.visualization.RenderConfig
import at.orchaldir.gm.visualization.character.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.createTorso
import at.orchaldir.gm.visualization.equipment.part.addNeckline

fun visualizeShirt(
    renderer: Renderer,
    config: RenderConfig,
    aabb: AABB,
    body: Body,
    shirt: Shirt,
) {
    visualizeSleeves(renderer, config, aabb, body, shirt.sleeveStyle, shirt.color)
    visualizeTorso(renderer, config, aabb, body, shirt.necklineStyle, shirt.color)
}

private fun visualizeSleeves(
    renderer: Renderer,
    config: RenderConfig,
    aabb: AABB,
    body: Body,
    style: SleeveStyle,
    color: Color,
) {
    val options = FillAndBorder(color.toRender(), config.line)
    val (left, right) = config.body.getArmStarts(aabb, body)
    val armSize = config.body.getArmSize(aabb, body)

    val sleeveSize = when (style) {
        SleeveStyle.Long -> armSize
        SleeveStyle.None -> return
        SleeveStyle.Short -> armSize.copy(height = armSize.height * 0.5f)
    }

    val leftAabb = AABB(left, sleeveSize)
    val rightAabb = AABB(right, sleeveSize)

    renderer.renderRectangle(leftAabb, options, EQUIPMENT_LAYER)
    renderer.renderRectangle(rightAabb, options, EQUIPMENT_LAYER)
}

private fun visualizeTorso(
    renderer: Renderer,
    config: RenderConfig,
    aabb: AABB,
    body: Body,
    style: NecklineStyle,
    color: Color,
) {
    val options = FillAndBorder(color.toRender(), config.line)
    val builder = createTorso(config, aabb, body)
    val torsoAabb = config.body.getTorsoAabb(aabb, body)

    addNeckline(builder, config, torsoAabb, style)

    val polygon = builder.build()

    renderer.renderPolygon(polygon, options, EQUIPMENT_LAYER)
}
