package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.Footwear
import at.orchaldir.gm.core.model.item.FootwearStyle
import at.orchaldir.gm.core.model.item.Pants
import at.orchaldir.gm.core.model.item.PantsStyle
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.utils.renderer.RenderOptions
import at.orchaldir.gm.utils.renderer.Renderer
import at.orchaldir.gm.visualization.RenderConfig
import at.orchaldir.gm.visualization.character.BodyConfig
import at.orchaldir.gm.visualization.character.EQUIPMENT_LAYER

data class FootwearConfig(
    val heightAnkle: Factor,
    val heightKnee: Factor,
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
    val legSize = config.body.getLegSize(aabb, body)
    val size = legSize.copy(height = legSize.height * height.value)
    val (left, right) = config.body.getMirroredLegPoint(aabb, body, FULL - height * 0.5f)
    val leftAabb = AABB.fromCenter(left, size)
    val rightAabb = AABB.fromCenter(right, size)

    renderer.renderRectangle(leftAabb, options, EQUIPMENT_LAYER)
    renderer.renderRectangle(rightAabb, options, EQUIPMENT_LAYER)
}