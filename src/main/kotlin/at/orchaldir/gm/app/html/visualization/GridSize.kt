package at.orchaldir.gm.app.html.visualization

import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.SIZE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.fieldMapSize
import at.orchaldir.gm.app.html.util.parseMapSize
import at.orchaldir.gm.app.html.util.selectMapSize
import at.orchaldir.gm.core.model.visualization.GridSize
import at.orchaldir.gm.core.model.visualization.GridSizeType
import at.orchaldir.gm.core.model.visualization.RowsAndColumns
import at.orchaldir.gm.core.model.visualization.SquareGrid
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showGridSize(
    size: GridSize,
) {
    showDetails("Grid Size", true) {
        field("Type", size.getType())

        when (size) {
            is RowsAndColumns -> fieldMapSize("Size", size.size)
            is SquareGrid -> field("Size", size.size)
        }
    }
}

// edit

fun HtmlBlockTag.editGridSize(
    size: GridSize,
    param: String,
    minSize: Int,
    maxSize: Int,
) {
    val sizeParam = combine(param, SIZE)

    showDetails("Grid Size", true) {
        selectValue(
            "Type",
            sizeParam,
            GridSizeType.entries,
            size.getType(),
        )

        when (size) {
            is RowsAndColumns -> selectMapSize(
                sizeParam,
                size.size,
                minSize,
                maxSize,
            )
            is SquareGrid -> selectInt(
                "Size",
                size.size,
                minSize,
                maxSize,
                1,
                combine(sizeParam, NUMBER),
            )
        }
    }
}

// parse

fun parseGridSize(
    parameters: Parameters,
    param: String,
): GridSize {
    val sizeParam = combine(param, SIZE)

    return when (parse(parameters, sizeParam, GridSizeType.Square)) {
        GridSizeType.RowsAndColumns -> RowsAndColumns(
            parseMapSize(parameters, sizeParam, 10),
        )
        GridSizeType.Square -> SquareGrid(
            parseInt(parameters, sizeParam, 10),
        )
    }
}
