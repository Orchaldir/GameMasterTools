package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Footwear
import at.orchaldir.gm.core.model.item.equipment.style.FootwearStyle
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.BEHIND_LAYER
import at.orchaldir.gm.visualization.character.appearance.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.appearance.visualizeFeet

data class FootwearConfig(
    val heightAnkle: Factor,
    val heightKnee: Factor,
    val heightSole: Factor,
    val paddingShaft: Factor,
)

fun visualizeFootwear(
    state: CharacterRenderState,
    body: Body,
    footwear: Footwear,
) {
    val options = FillAndBorder(footwear.color.toRender(), state.config.line)

    visualizeBootShaft(state, body, footwear, options)

    if (footwear.style.isFootVisible(state.renderFront)) {
        val layer = if (state.renderFront) {
            EQUIPMENT_LAYER
        } else {
            BEHIND_LAYER
        }
        visualizeFeet(state, body, options, layer)
    }

    if (footwear.style.hasSole()) {
        visualizeSoles(state, body, footwear)
    }
}

private fun visualizeBootShaft(
    state: CharacterRenderState,
    body: Body,
    footwear: Footwear,
    options: RenderOptions,
) {
    val shoeHeight = getShoeHeight(state, body)
    val height = when (footwear.style) {
        FootwearStyle.Boots -> state.config.equipment.footwear.heightAnkle
        FootwearStyle.KneeHighBoots -> state.config.equipment.footwear.heightKnee
        FootwearStyle.Pumps -> if (state.renderFront) {
            return
        } else {
            shoeHeight
        }

        FootwearStyle.Shoes -> shoeHeight
        else -> return
    }

    visualizeBootShaft(state, body, options, height)
}

private fun getShoeHeight(
    state: CharacterRenderState,
    body: Body,
) = state.config.body.getFootRadius(body) / state.config.body.getLegHeight()

private fun visualizeBootShaft(
    state: CharacterRenderState,
    body: Body,
    options: RenderOptions,
    scale: Factor,
) {
    val config = state.config
    val width = config.body.getLegWidth(body) + config.equipment.footwear.paddingShaft
    val height = config.body.getLegHeight() * scale
    val size = state.aabb.size.scale(width, height)
    val (left, right) = config.body.getMirroredLegPoint(state.aabb, body, FULL - scale * 0.5f)
    val leftAabb = AABB.fromCenter(left, size)
    val rightAabb = AABB.fromCenter(right, size)
    val layer = state.renderer.getLayer(EQUIPMENT_LAYER)

    layer.renderRectangle(leftAabb, options)
    layer.renderRectangle(rightAabb, options)
}

fun visualizeSoles(
    state: CharacterRenderState,
    body: Body,
    footwear: Footwear,
) {
    val config = state.config
    val options = FillAndBorder(footwear.sole.toRender(), config.line)
    val (left, right) = config.body.getMirroredLegPoint(state.aabb, body, END)
    val width = state.aabb.convertHeight(config.body.getFootRadius(body) * 2.0f)
    val height = state.aabb.convertHeight(config.equipment.footwear.heightSole)
    val size = Size2d(width, height)
    val offset = Point2d(0.0f, size.height / 2.0f)
    val leftAABB = AABB.fromCenter(left + offset, size)
    val rightAABB = AABB.fromCenter(right + offset, size)
    val layer = state.renderer.getLayer(EQUIPMENT_LAYER)

    layer.renderRectangle(leftAABB, options)
    layer.renderRectangle(rightAABB, options)
}