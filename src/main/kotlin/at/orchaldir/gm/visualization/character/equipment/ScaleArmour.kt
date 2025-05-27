package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.ScaleArmour
import at.orchaldir.gm.core.model.item.equipment.style.NecklineStyle
import at.orchaldir.gm.core.model.item.equipment.style.OuterwearLength
import at.orchaldir.gm.utils.isEven
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.JACKET_LAYER
import at.orchaldir.gm.visualization.character.appearance.addHip
import at.orchaldir.gm.visualization.character.appearance.addTorso
import at.orchaldir.gm.visualization.character.equipment.part.addNeckline
import at.orchaldir.gm.visualization.character.equipment.part.visualizeSleeves
import at.orchaldir.gm.visualization.renderBuilder
import at.orchaldir.gm.visualization.utils.visualizeRowOfShapes
import kotlin.math.ceil

fun visualizeScaleArmour(
    state: CharacterRenderState,
    body: Body,
    armour: ScaleArmour,
) {
    val clipping = createClippingPolygon(state, body)
    val clippingName = state.renderer.createClipping(clipping)
    val color = armour.scale.getColor(state.state, state.colors)
    val options = FillAndBorder(color.toRender(), state.config.line, clippingName)

    visualizeScaleArmourBody(state, options, body, armour)
}

private fun visualizeScaleArmourBody(
    state: CharacterRenderState,
    options: RenderOptions,
    body: Body,
    armour: ScaleArmour,
) {
    val renderer = state.renderer.getLayer(JACKET_LAYER)
    val torso = state.config.body.getTorsoAabb(state.aabb, body)
    val maxWidthFactor = state.config.body.getHipWidth(body.bodyShape)
    val maxWidth = torso.convertWidth(maxWidthFactor)
    val hipWidthFactor = state.config.body.getHipWidth(body.bodyShape)
    val hipWidth = torso.convertWidth(hipWidthFactor)
    val scaleWidth = hipWidth / armour.columns
    val scaleSize = armour.shape.calculateSizeFromWidth(scaleWidth)
    val start = torso.getPoint(CENTER, START)
    val bottomFactor = getOuterwearBottomY(state, body, armour.length)
    val bottom = state.aabb.getPoint(CENTER, bottomFactor)
    val step = scaleSize.height * (FULL - armour.overlap)
    val height = bottom.y - start.y
    val rows = (height.toMeters() / step.toMeters()).toInt()
    val maxColumns = ceil(maxWidth.toMeters() / scaleWidth.toMeters()).toInt()
    val columns = maxColumns + 2
    var rowCenter = start.addHeight(step * rows)

    repeat(rows + 1) { index ->
        val rowOffset = if (index.isEven()) {
            0
        } else {
            1
        }

        visualizeRowOfShapes(
            renderer,
            options,
            rowCenter,
            armour.shape,
            scaleSize,
            columns + rowOffset,
        )

        rowCenter = rowCenter.minusHeight(step)
    }
}

private fun createClippingPolygon(
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
