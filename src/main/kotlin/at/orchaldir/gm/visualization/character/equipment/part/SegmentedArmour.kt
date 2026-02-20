package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.BodyArmour
import at.orchaldir.gm.core.model.item.equipment.style.ChainMail
import at.orchaldir.gm.core.model.item.equipment.style.OuterwearLength
import at.orchaldir.gm.core.model.item.equipment.style.SegmentedArmour
import at.orchaldir.gm.core.model.item.equipment.style.SegmentedPlateShape
import at.orchaldir.gm.core.model.item.equipment.style.SleeveStyle
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.JACKET_LAYER
import at.orchaldir.gm.visualization.character.equipment.createOuterwearBottom
import at.orchaldir.gm.visualization.character.equipment.getOuterwearBottomY
import at.orchaldir.gm.visualization.renderBuilder

fun visualizeSegmentedArmour(
    state: CharacterRenderState<Body>,
    armour: BodyArmour,
    style: SegmentedArmour,
) {
    val renderer = state.renderer.getLayer(JACKET_LAYER)

    visualizeSegmentedArmourBody(state, renderer, armour, style)
}

fun visualizeSegmentedArmourLowerBody(
    state: CharacterRenderState<Body>,
    style: SegmentedArmour,
    length: OuterwearLength,
) {
    val renderer = state.renderer.getLayer(JACKET_LAYER)
    val options = getClippingRenderOptionsForArmourBody(state, style.segment)
    val torso = state.torsoAABB()
    val maxWidthFactor = state.config.body.getMaxWidth(state)
    val segmentWidth = torso.convertWidth(maxWidthFactor)
    val start = torso.getPoint(CENTER, END)
    val bottomFactor = getOuterwearBottomY(state, length)
    val bottom = state.fullAABB.getPoint(CENTER, bottomFactor)
    val rowHeight = (bottom.y - start.y) / style.rows.toFloat()
    val center = bottom
        .minusHeight(rowHeight * 0.5f)

    renderSegments(style, renderer, options, center, rowHeight, segmentWidth, style.rows)
}

private fun visualizeSegmentedArmourBody(
    state: CharacterRenderState<Body>,
    renderer: LayerRenderer,
    armour: BodyArmour,
    style: SegmentedArmour,
) {
    val options = getClippingRenderOptionsForArmourBody(state, style.segment)
    val torso = state.torsoAABB()
    val maxWidthFactor = state.config.body.getMaxWidth(state)
    val segmentWidth = torso.convertWidth(maxWidthFactor)
    val start = torso.getPoint(CENTER, START)
    val bottomFactor = getOuterwearBottomY(state, armour.legStyle.upperBodyLength())
    val bottom = state.fullAABB.getPoint(CENTER, bottomFactor)
    val rowHeight = (bottom - start).y / style.rows.toFloat()
    val center = torso.getPoint(CENTER, START)
        .addHeight(rowHeight * (0.5f + style.rows - 1))

    renderSegments(style, renderer, options, center, rowHeight, segmentWidth, style.rows - style.breastplateRows)
    renderBreastPlate(style, renderer, options, torso, rowHeight, segmentWidth)
    visualizeArmourSleeves(state, renderer, armour, style, rowHeight)
}

private fun renderSegments(
    style: SegmentedArmour,
    renderer: LayerRenderer,
    options: RenderOptions,
    start: Point2d,
    rowHeight: Distance,
    segmentWidth: Distance,
    rows: Int,
) {
    var center = start

    repeat(rows) {
        val polygon = createSegmentPolygon(center, segmentWidth, rowHeight, style.shape)

        renderer.renderRoundedPolygon(polygon, options)

        center = center.minusHeight(rowHeight)
    }
}

private fun renderBreastPlate(
    style: SegmentedArmour,
    renderer: LayerRenderer,
    options: RenderOptions,
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
    state: CharacterRenderState<Body>,
    renderer: LayerRenderer,
    armour: BodyArmour,
    style: SegmentedArmour,
    rowHeight: Distance,
) {
    if (armour.sleeveStyle == SleeveStyle.None) {
        return
    }

    val (leftAabb, rightAabb) = createSleeveAabbs(state, armour.sleeveStyle)

    visualizeArmourSleeve(state, renderer, leftAabb, style, rowHeight)
    visualizeArmourSleeve(state, renderer, rightAabb, style, rowHeight)
}

private fun visualizeArmourSleeve(
    state: CharacterRenderState<Body>,
    renderer: LayerRenderer,
    aabb: AABB,
    style: SegmentedArmour,
    rowHeight: Distance,
) {
    val color = style.segment.getColor(state.state, state.colors)
    val options = state.config.getLineOptions(color)
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
