package at.orchaldir.gm.visualization.utils

import at.orchaldir.gm.utils.isEven
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.shape.ComplexShape
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.RenderOptions

fun visualizeRowsOfShapes(
    renderer: LayerRenderer,
    options: RenderOptions,
    shape: ComplexShape,
    shapeSize: Size2d,
    start: Point2d,
    rowOverlap: Factor,
    columnOverlap: Factor,
    rows: Int,
    columns: Int,
    useRowOffset: Boolean = true,
) {
    val rowStep = calculateStep(shapeSize.height, rowOverlap)
    val columnStep = calculateStep(shapeSize.width, columnOverlap)
    var rowCenter = start.addHeight(rowStep * rows)

    repeat(rows + 1) { index ->
        val rowOffset = if (!useRowOffset || index.isEven()) {
            0
        } else {
            1
        }

        visualizeRowOfShapes(
            renderer,
            options,
            rowCenter,
            shape,
            shapeSize,
            columnStep,
            columns + rowOffset,
        )

        rowCenter = rowCenter.minusHeight(rowStep)
    }
}

fun calculateStep(
    distance: Distance,
    rowOverlap: Factor,
): Distance = distance * (FULL - rowOverlap)

fun visualizeRowOfShapes(
    renderer: LayerRenderer,
    options: RenderOptions,
    rowCenter: Point2d,
    shape: ComplexShape,
    size: Size2d,
    step: Distance,
    number: Int,
) {
    val rowStart = rowCenter.minusWidth(step * (number - 1) / 2.0f)
    var center = rowStart

    repeat(number) {
        val scaleAabb = AABB.fromCenter(center, size)

        visualizeComplexShape(renderer, scaleAabb, shape, options)

        center = center.addWidth(step)
    }
}