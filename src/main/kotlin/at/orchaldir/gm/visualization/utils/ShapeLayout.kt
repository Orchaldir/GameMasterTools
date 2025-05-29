package at.orchaldir.gm.visualization.utils

import at.orchaldir.gm.utils.isEven
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.unit.Distance
import kotlin.math.ceil


fun visualizeRows(
    shapeSize: Size2d,
    top: Point2d,
    bottom: Point2d,
    width: Distance,
    rowOverlap: Factor,
    columnOverlap: Factor,
    useRowOffset: Boolean,
    renderCell: (AABB) -> Unit,
    renderRow: (AABB) -> Unit = {},
) {
    val rowStep = calculateStep(shapeSize.height, rowOverlap)
    val columnStep = calculateStep(shapeSize.width, columnOverlap)
    val height = bottom.y - top.y
    val rows = (height.toMeters() / rowStep.toMeters()).toInt()
    val maxColumns = ceil(width.toMeters() / columnStep.toMeters()).toInt()
    val columns = maxColumns + 2

    visualizeRows(
        shapeSize,
        top,
        rowOverlap,
        columnOverlap,
        rows,
        columns,
        useRowOffset,
        renderCell,
        renderRow,
    )
}

private fun visualizeRows(
    shapeSize: Size2d,
    start: Point2d,
    rowOverlap: Factor,
    columnOverlap: Factor,
    rows: Int,
    columns: Int,
    useRowOffset: Boolean = true,
    renderCell: (AABB) -> Unit,
    renderRow: (AABB) -> Unit = {},
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
        val cells = columns + rowOffset

        visualizeRow(
            rowCenter,
            shapeSize,
            columnStep,
            cells,
            renderCell,
        )

        renderRow(AABB.fromCenter(rowCenter, shapeSize.replaceWidth(Factor.fromPercentage(cells * 100))))

        rowCenter = rowCenter.minusHeight(rowStep)
    }
}

fun calculateStep(
    distance: Distance,
    rowOverlap: Factor,
): Distance = distance * (FULL - rowOverlap)

fun visualizeRow(
    rowCenter: Point2d,
    size: Size2d,
    step: Distance,
    number: Int,
    renderCell: (AABB) -> Unit = {},
) {
    val rowStart = rowCenter.minusWidth(step * (number - 1) / 2.0f)
    var center = rowStart

    repeat(number) {
        val shapeAabb = AABB.fromCenter(center, size)

        renderCell(shapeAabb)

        center = center.addWidth(step)
    }
}