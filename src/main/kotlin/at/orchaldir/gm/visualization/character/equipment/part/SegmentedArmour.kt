package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.BodyArmour
import at.orchaldir.gm.core.model.item.equipment.style.SegmentedArmour
import at.orchaldir.gm.core.model.item.equipment.style.SegmentedPlateShape
import at.orchaldir.gm.core.model.item.equipment.style.SleeveStyle
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.CENTER
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.math.Polygon2dBuilder
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
    var center = torso.getPoint(CENTER, START)
        .addHeight(rowHeight * (0.5f + style.rows - 1))

    repeat(style.rows - style.breastplateRows) { row ->
        val polygon = createSegmentPolygon(center, segmentWidth, rowHeight, style.shape)

        renderer.renderRoundedPolygon(polygon, options)

        center = center.minusHeight(rowHeight)
    }

    renderBreastPlate(style, renderer, options, torso, rowHeight, segmentWidth)
    visualizeArmourSleeves(state, renderer, body, armour, style, rowHeight)
}

private fun renderBreastPlate(
    style: SegmentedArmour,
    renderer: LayerRenderer,
    options: FillAndBorder,
    torso: AABB,
    rowHeight: Distance,
    segmentWidth: Distance,
) {
    val breastplateHeight = rowHeight * style.breastplateRows
    val halfHeight = breastplateHeight / 2
    val center = torso.getPoint(CENTER, START)
        .addHeight(halfHeight)
    val polygon = createSegmentPolygon(center, segmentWidth, rowHeight, style.shape, style.breastplateRows)

    renderer.renderRoundedPolygon(polygon, options)
}

private fun createSegmentPolygon(
    center: Point2d,
    segmentWidth: Distance,
    rowHeight: Distance,
    shape: SegmentedPlateShape,
    rows: Int = 1,
): Polygon2d {
    val aabb = AABB.fromWidthAndHeight(center, segmentWidth, rowHeight * rows)

    return when (shape) {
        SegmentedPlateShape.Straight -> Polygon2dBuilder()
            .addMirroredPoints(aabb, FULL, START, true)
            .addMirroredPoints(aabb, FULL, END, true)
            .build()

        SegmentedPlateShape.Curved -> {
            val bottom = aabb.getPoint(CENTER, END)
                .addHeight(rowHeight / 4)

            Polygon2dBuilder()
                .addMirroredPoints(aabb, FULL, START, true)
                .addMirroredPoints(aabb, FULL, END, true)
                .addLeftPoint(bottom)
                .build()
        }

        SegmentedPlateShape.CenterTriangle -> {
            val bottom = aabb.getPoint(CENTER, END)
                .addHeight(rowHeight / 4)

            Polygon2dBuilder()
                .addMirroredPoints(aabb, FULL, START, true)
                .addMirroredPoints(aabb, FULL, END, true)
                .addMirroredPoints(aabb, Factor.fromPercentage(30), END)
                .addLeftPoint(bottom, true)
                .build()
        }
    }
}

private fun visualizeArmourSleeves(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    body: Body,
    armour: BodyArmour,
    style: SegmentedArmour,
    rowHeight: Distance,
) {
    if (armour.sleeveStyle == SleeveStyle.None) {
        return
    }

    val (leftAabb, rightAabb) = createSleeveAabbs(state, body, armour.sleeveStyle)

    visualizeArmourSleeve(state, renderer, leftAabb, style, rowHeight)
    visualizeArmourSleeve(state, renderer, rightAabb, style, rowHeight)
}

private fun visualizeArmourSleeve(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    aabb: AABB,
    style: SegmentedArmour,
    rowHeight: Distance,
) {
    val color = style.segment.getColor(state.state, state.colors)
    val options = FillAndBorder(color.toRender(), state.config.line)
    val top = aabb.getPoint(CENTER, START)
    val bottom = aabb.getPoint(CENTER, FULL)
    var center = top
        .addHeight(rowHeight / 2)

    while (center.y < bottom.y) {
        val segmentAabb = AABB.fromWidthAndHeight(center, aabb.size.width, rowHeight)

        renderer.renderRectangle(segmentAabb, options)

        center = center.addHeight(rowHeight)
    }
}
