package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.BodyArmour
import at.orchaldir.gm.core.model.item.equipment.style.SegmentedArmour
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.CENTER
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.START
import at.orchaldir.gm.utils.math.THREE_QUARTER
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.JACKET_LAYER
import at.orchaldir.gm.visualization.character.equipment.getOuterwearBottomY

fun visualizeSegmentedArmour(
    state: CharacterRenderState,
    body: Body,
    armour: BodyArmour,
    style: SegmentedArmour,
) {
    val renderer = state.renderer.getLayer(JACKET_LAYER)

    visualizeSegmentedArmourBody(state, renderer, body, armour, style)
}

private fun visualizeSegmentedArmourBody(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    body: Body,
    armour: BodyArmour,
    style: SegmentedArmour,
) {
    val clipping = createClippingPolygonForArmourBody(state, body)
    val clippingName = state.renderer.createClipping(clipping)
    val color = style.segment.getColor(state.state, state.colors)
    val options = FillAndBorder(color.toRender(), state.config.line, clippingName)
    val torso = state.config.body.getTorsoAabb(state.aabb, body)
    val maxWidthFactor = state.config.body.getMaxWidth(body.bodyShape)
    val segmentWidth = torso.convertWidth(maxWidthFactor)
    val start = torso.getPoint(CENTER, START)
    val bottomFactor = getOuterwearBottomY(state, body, armour.length, THREE_QUARTER)
    val bottom = state.aabb.getPoint(CENTER, bottomFactor)
    val rowHeight = (bottom - start).y / style.rows.toFloat()

    var position = renderBreastPlate(style, renderer, options, torso, rowHeight, segmentWidth)

    repeat(style.rows - style.breastPlateRows) { row ->
        val aabb = AABB.fromWidthAndHeight(position, segmentWidth, rowHeight)

        renderer.renderRectangle(aabb, options)

        position = position.addHeight(rowHeight)
    }
}

private fun renderBreastPlate(
    style: SegmentedArmour,
    renderer: LayerRenderer,
    options: FillAndBorder,
    torso: AABB,
    rowHeight: Distance,
    segmentWidth: Distance,
): Point2d {
    val position = torso.getPoint(CENTER, START)
        .addHeight(rowHeight * style.breastPlateRows / 2)
    val breastplateHeight = rowHeight * style.breastPlateRows
    val breastplateAabb = AABB.fromWidthAndHeight(position, segmentWidth, breastplateHeight)

    renderer.renderRectangle(breastplateAabb, options)

    return position.addHeight(breastplateHeight / 2 + rowHeight / 2)
}
