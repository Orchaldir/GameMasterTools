package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.BodyArmour
import at.orchaldir.gm.core.model.item.equipment.style.ChainMail
import at.orchaldir.gm.core.model.item.equipment.style.OuterwearLength
import at.orchaldir.gm.core.model.item.equipment.style.ScaleArmour
import at.orchaldir.gm.core.model.item.equipment.style.SleeveStyle
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.JACKET_LAYER
import at.orchaldir.gm.visualization.character.equipment.createOuterwearBottom
import at.orchaldir.gm.visualization.character.equipment.getOuterwearBottomY
import at.orchaldir.gm.visualization.renderBuilder
import at.orchaldir.gm.visualization.utils.visualizeComplexShape
import at.orchaldir.gm.visualization.utils.visualizeRows

fun visualizeScaleArmour(
    state: CharacterRenderState<Body>,
    armour: BodyArmour,
    style: ScaleArmour,
) {
    val renderer = state.renderer.getLayer(JACKET_LAYER)

    visualizeScaleArmourBody(state, renderer, style, armour.legStyle.upperBodyLength(), START)
    visualizeScaleArmourSleeves(state, renderer, armour, style)
}

private fun visualizeScaleArmourBody(
    state: CharacterRenderState<Body>,
    renderer: LayerRenderer,
    style: ScaleArmour,
    length: OuterwearLength,
    startY: Factor,
) {
    val options = getClippingRenderOptionsForArmourBody(state, style.scale)
    val torso = state.torsoAABB()
    val maxWidthFactor = state.config.body.getMaxWidth(state)
    val maxWidth = torso.convertWidth(maxWidthFactor)
    val scaleWidth = calculateArmourScaleWidth(state, style.columns)
    val scaleSize = style.shape.calculateSizeFromWidth(scaleWidth)
    val top = torso.getPoint(CENTER, startY)
    val bottomFactor = getOuterwearBottomY(state, length)
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
    val options = getClippingRenderOptionsForArmourBody(state, style.scale)
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

fun visualizeScaleArmourLowerBody(
    state: CharacterRenderState<Body>,
    style: ScaleArmour,
    length: OuterwearLength,
) {
    val renderer = state.renderer.getLayer(JACKET_LAYER)

    visualizeScaleArmourBody(state, renderer, style, length, state.config.body.hipY)
}