package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.BodyArmour
import at.orchaldir.gm.core.model.item.equipment.style.ScaleArmour
import at.orchaldir.gm.core.model.item.equipment.style.SleeveStyle
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.JACKET_LAYER
import at.orchaldir.gm.visualization.character.equipment.getOuterwearBottomY
import at.orchaldir.gm.visualization.utils.visualizeComplexShape
import at.orchaldir.gm.visualization.utils.visualizeRows

fun visualizeScaleArmour(
    state: CharacterRenderState,
    body: Body,
    armour: BodyArmour,
    style: ScaleArmour,
) {
    val renderer = state.renderer.getLayer(JACKET_LAYER)

    visualizeScaleArmourBody(state, renderer, body, armour, style)
    visualizeScaleArmourSleeves(state, renderer, body, armour, style)
}

private fun visualizeScaleArmourBody(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    body: Body,
    armour: BodyArmour,
    style: ScaleArmour,
) {
    val clipping = createClippingPolygonForArmourBody(state, body)
    val clippingName = state.renderer.createClipping(clipping)
    val color = style.scale.getColor(state.state, state.colors)
    val options = FillAndBorder(color.toRender(), state.config.line, clippingName)
    val torso = state.config.body.getTorsoAabb(state.aabb, body)
    val maxWidthFactor = state.config.body.getMaxWidth(body.bodyShape)
    val maxWidth = torso.convertWidth(maxWidthFactor)
    val scaleWidth = calculateArmourScaleWidth(state, body, torso, style.columns)
    val scaleSize = style.shape.calculateSizeFromWidth(scaleWidth)
    val top = torso.getPoint(CENTER, START)
    val bottomFactor = getOuterwearBottomY(state, body, armour.length, THREE_QUARTER)
    val bottom = state.aabb.getPoint(CENTER, bottomFactor)

    visualizeRows(
        scaleSize,
        top,
        bottom,
        maxWidth,
        style.overlap,
        ZERO,
        true,
        { scaleAabb ->
            visualizeComplexShape(renderer, scaleAabb, style.shape, options)
        },
    )
}

private fun visualizeScaleArmourSleeves(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    body: Body,
    armour: BodyArmour,
    style: ScaleArmour,
) {
    if (armour.sleeveStyle == SleeveStyle.None) {
        return
    }

    val (leftAabb, rightAabb) = createSleeveAabbs(state, body, armour.sleeveStyle)
    val (leftClip, rightClip) = createSleeveAabbs(state, body, SleeveStyle.Long)
    val torso = state.config.body.getTorsoAabb(state.aabb, body)
    val scaleWidth = calculateArmourScaleWidth(state, body, torso, style.columns)
    val scaleSize = style.shape.calculateSizeFromWidth(scaleWidth)

    visualizeScaleArmourSleeve(state, renderer, leftAabb, leftClip, style, scaleSize)
    visualizeScaleArmourSleeve(state, renderer, rightAabb, rightClip, style, scaleSize)
}

private fun visualizeScaleArmourSleeve(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    aabb: AABB,
    clip: AABB,
    style: ScaleArmour,
    scaleSize: Size2d,
) {
    val clipping = Polygon2d(clip)
    val clippingName = state.renderer.createClipping(clipping)
    val color = style.scale.getColor(state.state, state.colors)
    val options = FillAndBorder(color.toRender(), state.config.line, clippingName)
    val top = aabb.getPoint(CENTER, START)
    val bottom = aabb.getPoint(CENTER, FULL)

    visualizeRows(
        scaleSize,
        top,
        bottom,
        aabb.size.width,
        style.overlap,
        ZERO,
        true,
        { scaleAabb ->
            visualizeComplexShape(renderer, scaleAabb, style.shape, options)
        }
    )
}
