package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.Footwear
import at.orchaldir.gm.core.model.item.FootwearStyle
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.utils.renderer.RenderOptions
import at.orchaldir.gm.utils.renderer.Renderer
import at.orchaldir.gm.visualization.RenderConfig
import at.orchaldir.gm.visualization.character.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.visualizeFeet

data class FootwearConfig(
    val heightAnkle: Factor,
    val heightKnee: Factor,
    val heightSole: Factor,
)

fun visualizeFootwear(
    renderer: Renderer,
    config: RenderConfig,
    aabb: AABB,
    body: Body,
    footwear: Footwear,
) {
    val options = FillAndBorder(footwear.color.toRender(), config.line)

    visualizeBootShaft(renderer, config, aabb, body, footwear, options)
    visualizeFeet(renderer, config, aabb, body, options, EQUIPMENT_LAYER)
    visualizeSoles(renderer, config, aabb, body, footwear)
}

private fun visualizeBootShaft(
    renderer: Renderer,
    config: RenderConfig,
    aabb: AABB,
    body: Body,
    footwear: Footwear,
    options: RenderOptions,
) {
    val height = when (footwear.style) {
        FootwearStyle.Boots -> config.equipment.footwear.heightAnkle
        FootwearStyle.KneeHighBoots -> config.equipment.footwear.heightKnee
        else -> return
    }

    visualizeBootShaft(renderer, config, aabb, body, options, height)
}

private fun visualizeBootShaft(
    renderer: Renderer,
    config: RenderConfig,
    aabb: AABB,
    body: Body,
    options: RenderOptions,
    height: Factor,
) {
    val size = config.body.getLegSize(aabb, body, height)
    val (left, right) = config.body.getMirroredLegPoint(aabb, body, FULL - height * 0.5f)
    val leftAabb = AABB.fromCenter(left, size)
    val rightAabb = AABB.fromCenter(right, size)

    renderer.renderRectangle(leftAabb, options, EQUIPMENT_LAYER)
    renderer.renderRectangle(rightAabb, options, EQUIPMENT_LAYER)
}

fun visualizeSoles(
    renderer: Renderer,
    config: RenderConfig,
    aabb: AABB,
    body:
    Body,
    footwear: Footwear,
) {
    val options = FillAndBorder(footwear.sole.toRender(), config.line)
    val (left, right) = config.body.getMirroredLegPoint(aabb, body, END)
    val width = aabb.convertHeight(config.body.getFootRadius(body) * 2.0f)
    val height = aabb.convertHeight(config.equipment.footwear.heightSole)
    val size = Size2d(width, height)
    val offset = Point2d(0.0f, size.height / 2.0f)
    val leftAABB = AABB.fromCenter(left + offset, size)
    val rightAABB = AABB.fromCenter(right + offset, size)

    renderer.renderRectangle(leftAABB, options, EQUIPMENT_LAYER)
    renderer.renderRectangle(rightAABB, options, EQUIPMENT_LAYER)
}