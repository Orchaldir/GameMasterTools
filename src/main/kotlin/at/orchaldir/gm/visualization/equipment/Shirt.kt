package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.Shirt
import at.orchaldir.gm.core.model.item.SleeveStyle
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.utils.renderer.Renderer
import at.orchaldir.gm.visualization.RenderConfig
import at.orchaldir.gm.visualization.character.EQUIPMENT_LAYER

fun visualizeShirt(
    renderer: Renderer,
    config: RenderConfig,
    aabb: AABB,
    body: Body,
    shirt: Shirt,
) {
    visualizeSleeves(renderer, config, aabb, body, shirt.sleeveStyle, shirt.color)
}

fun visualizeSleeves(
    renderer: Renderer,
    config: RenderConfig,
    aabb: AABB,
    body: Body,
    style: SleeveStyle,
    color: Color,
) {
    val options = FillAndBorder(color.toRender(), config.line)
    val start = config.body.getArmStart(aabb, body)
    val size = config.body.getArmSize(aabb, body)

    val armAabb = when (style) {
        SleeveStyle.Long -> AABB(start, size)
        SleeveStyle.None -> return
        SleeveStyle.Short -> AABB(start, size.copy(height = size.height * 0.5f))
    }

    renderer.renderRectangle(armAabb, options, EQUIPMENT_LAYER)
}
