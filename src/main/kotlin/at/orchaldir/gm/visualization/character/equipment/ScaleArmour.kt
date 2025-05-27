package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.ScaleArmour
import at.orchaldir.gm.core.model.item.equipment.style.SleeveStyle
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.JACKET_LAYER
import at.orchaldir.gm.visualization.character.appearance.addHip
import at.orchaldir.gm.visualization.character.appearance.addTorso
import at.orchaldir.gm.visualization.character.equipment.part.createSleeveAabbs
import at.orchaldir.gm.visualization.utils.visualizeRowsOfShapes
import kotlin.math.ceil

fun visualizeScaleArmour(
    state: CharacterRenderState,
    body: Body,
    armour: ScaleArmour,
) {
    val renderer = state.renderer.getLayer(JACKET_LAYER)

    visualizeScaleArmourBody(state, renderer, body, armour)
    visualizeScaleArmourSleeves(state, renderer, body, armour)
}

private fun visualizeScaleArmourBody(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    body: Body,
    armour: ScaleArmour,
) {
    val clipping = createClippingPolygonForBody(state, body)
    val clippingName = state.renderer.createClipping(clipping)
    val color = armour.scale.getColor(state.state, state.colors)
    val options = FillAndBorder(color.toRender(), state.config.line, clippingName)
    val torso = state.config.body.getTorsoAabb(state.aabb, body)
    val maxWidthFactor = state.config.body.getHipWidth(body.bodyShape)
    val maxWidth = torso.convertWidth(maxWidthFactor)
    val scaleWidth = calculateScaleWidth(state, body, torso, armour)
    val scaleSize = armour.shape.calculateSizeFromWidth(scaleWidth)
    val start = torso.getPoint(CENTER, START)
    val bottomFactor = getOuterwearBottomY(state, body, armour.length)
    val bottom = state.aabb.getPoint(CENTER, bottomFactor)
    val step = scaleSize.height * (FULL - armour.overlap)
    val height = bottom.y - start.y
    val rows = (height.toMeters() / step.toMeters()).toInt()
    val maxColumns = ceil(maxWidth.toMeters() / scaleWidth.toMeters()).toInt()
    val columns = maxColumns + 2

    visualizeRowsOfShapes(renderer, options, armour.shape, scaleSize, start, step, rows, columns)
}

private fun visualizeScaleArmourSleeves(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    body: Body,
    armour: ScaleArmour,
) {
    if (armour.sleeveStyle == SleeveStyle.None) {
        return
    }

    val (leftAabb, rightAabb) = createSleeveAabbs(state, body, armour.sleeveStyle)
    val torso = state.config.body.getTorsoAabb(state.aabb, body)
    val scaleWidth = calculateScaleWidth(state, body, torso, armour)
    val scaleSize = armour.shape.calculateSizeFromWidth(scaleWidth)

    visualizeScaleArmourSleeve(state, renderer, leftAabb, armour, scaleSize)
    visualizeScaleArmourSleeve(state, renderer, rightAabb, armour, scaleSize)
}

private fun visualizeScaleArmourSleeve(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    aabb: AABB,
    armour: ScaleArmour,
    scaleSize: Size2d,
) {
    val color = armour.scale.getColor(state.state, state.colors)
    val options = FillAndBorder(color.toRender(), state.config.line)
    val start = aabb.getPoint(CENTER, START)
    val bottom = aabb.getPoint(CENTER, FULL)
    val step = scaleSize.height * (FULL - armour.overlap)
    val height = bottom.y - start.y
    val rows = (height.toMeters() / step.toMeters()).toInt()
    val maxColumns = ceil(aabb.size.width.toMeters() / scaleSize.width.toMeters()).toInt()
    val columns = maxColumns + 1

    visualizeRowsOfShapes(renderer, options, armour.shape, scaleSize, start, step, rows, columns)
}

private fun calculateScaleWidth(
    state: CharacterRenderState,
    body: Body,
    torso: AABB,
    armour: ScaleArmour,
): Distance {
    val hipWidthFactor = state.config.body.getHipWidth(body.bodyShape)
    val hipWidth = torso.convertWidth(hipWidthFactor)

    return hipWidth / armour.columns
}

private fun createClippingPolygonForBody(
    state: CharacterRenderState,
    body: Body,
): Polygon2d {
    val torso = state.config.body.getTorsoAabb(state.aabb, body)
    val hipWidthFactor = state.config.body.getHipWidth(body.bodyShape)
    val hipWidth = torso.convertWidth(hipWidthFactor)
    val half = hipWidth / 2
    val bottom = state.aabb.getPoint(CENTER, END)
    val builder = Polygon2dBuilder()
        .addPoints(bottom.minusWidth(half), bottom.addWidth(half))

    addHip(state.config, builder, state.aabb, body)
    addTorso(state, body, builder)

    return builder.build()
}
