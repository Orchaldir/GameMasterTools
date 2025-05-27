package at.orchaldir.gm.visualization.utils

import at.orchaldir.gm.utils.isEven
import at.orchaldir.gm.utils.math.AABB
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
    step: Distance,
    rows: Int,
    columns: Int,
) {
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
            shape,
            shapeSize,
            columns + rowOffset,
        )

        rowCenter = rowCenter.minusHeight(step)
    }
}

fun visualizeRowOfShapes(
    renderer: LayerRenderer,
    options: RenderOptions,
    rowCenter: Point2d,
    shape: ComplexShape,
    size: Size2d,
    number: Int,
) {
    val rowStart = rowCenter.minusWidth(size.width * (number - 1) / 2.0f)
    var center = rowStart

    repeat(number) {
        val scaleAabb = AABB.fromCenter(center, size)

        visualizeComplexShape(renderer, scaleAabb, shape, options)

        center = center.addWidth(size.width)
    }
}