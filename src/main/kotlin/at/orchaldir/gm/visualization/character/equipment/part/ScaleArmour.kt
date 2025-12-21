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
    state: CharacterRenderState<Body>,
    armour: BodyArmour,
    style: ScaleArmour,
) {
    val renderer = state.renderer.getLayer(JACKET_LAYER)

    visualizeScaleArmourBody(state, renderer, armour, style)
    visualizeScaleArmourSleeves(state, renderer, armour, style)
}

private fun visualizeScaleArmourBody(
    state: CharacterRenderState<Body>,
    renderer: LayerRenderer,
    armour: BodyArmour,
    style: ScaleArmour,
) {
    val clipping = createClippingPolygonForArmourBody(state)
    val clippingName = state.renderer.createClipping(clipping)
    val color = style.scale.getColor(state.state, state.colors)
    val options = FillAndBorder(color.toRender(), state.config.line, clippingName)
    val torso = state.torsoAABB()
    val maxWidthFactor = state.config.body.getMaxWidth(state)
    val maxWidth = torso.convertWidth(maxWidthFactor)
    val scaleWidth = calculateArmourScaleWidth(state, style.columns)
    val scaleSize = style.shape.calculateSizeFromWidth(scaleWidth)
    val top = torso.getPoint(CENTER, START)
    val bottomFactor = getOuterwearBottomY(state, armour.length, THREE_QUARTER)
    val bottom = state.fullAABB.getPoint(CENTER, bottomFactor)

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
    state: CharacterRenderState<Body>,
    renderer: LayerRenderer,
    armour: BodyArmour,
    style: ScaleArmour,
) {
    if (armour.sleeveStyle == SleeveStyle.None) {
        return
    }

    val (leftAabb, rightAabb) = createSleeveAabbs(state, armour.sleeveStyle)
    val (leftClip, rightClip) = createSleeveAabbs(state, SleeveStyle.Long)
    val scaleWidth = calculateArmourScaleWidth(state, style.columns)
    val scaleSize = style.shape.calculateSizeFromWidth(scaleWidth)

    visualizeScaleArmourSleeve(state, renderer, leftAabb, leftClip, style, scaleSize)
    visualizeScaleArmourSleeve(state, renderer, rightAabb, rightClip, style, scaleSize)
}

private fun visualizeScaleArmourSleeve(
    state: CharacterRenderState<Body>,
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
