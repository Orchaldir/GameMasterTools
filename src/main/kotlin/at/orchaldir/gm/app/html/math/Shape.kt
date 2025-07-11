package at.orchaldir.gm.app.html.math

import at.orchaldir.gm.app.SHAPE
import at.orchaldir.gm.app.SIZE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.html.util.fieldFactor
import at.orchaldir.gm.app.html.util.parseFactor
import at.orchaldir.gm.app.html.util.selectFactor
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.ONE_PERCENT
import at.orchaldir.gm.utils.math.shape.*
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showComplexShape(
    shape: ComplexShape,
    label: String = "Shape",
) {
    showDetails(label, true) {
        field("Type", shape.getType())

        when (shape) {
            is UsingCircularShape -> showCircularShape(shape.shape)
            is UsingRectangularShape -> showUsingRectangularShape(shape)
        }
    }
}

fun HtmlBlockTag.showUsingRectangularShape(shape: UsingRectangularShape) {
    showRectangularShape(shape.shape)
    fieldFactor("Height to Width", shape.widthFactor)
}

fun HtmlBlockTag.showCircularShape(
    shape: CircularShape,
    label: String = "Shape",
) {
    field(label, shape)
}

fun HtmlBlockTag.showRectangularShape(
    shape: RectangularShape,
    label: String = "Shape",
) {
    field(label, shape)
}

// edit

fun HtmlBlockTag.selectComplexShape(
    shape: ComplexShape,
    param: String,
    availableShapes: Collection<RectangularShape> = RectangularShape.entries,
) {
    showDetails("Shape", true) {
        selectValue("Type", param, ComplexShapeType.entries, shape.getType())

        when (shape) {
            is UsingCircularShape -> selectCircularShape(shape.shape, combine(param, SHAPE, 0))
            is UsingRectangularShape -> selectUsingRectangularShape(shape, param, availableShapes)
        }
    }
}

fun HtmlBlockTag.selectUsingRectangularShape(
    shape: UsingRectangularShape,
    param: String,
    availableShapes: Collection<RectangularShape> = RectangularShape.entries,
) {
    selectRectangularShape(shape.shape, combine(param, SHAPE, 1), availableShapes = availableShapes)
    selectFactor(
        "Height to Width",
        combine(param, SIZE),
        shape.widthFactor,
        MIN_RECTANGULAR_FACTOR,
        MAX_RECTANGULAR_FACTOR,
        ONE_PERCENT,
    )
}

fun HtmlBlockTag.selectCircularShape(
    shape: CircularShape,
    param: String,
    label: String = "Shape",
) {
    selectValue(label, param, CircularShape.entries, shape)
}

fun HtmlBlockTag.selectRectangularShape(
    shape: RectangularShape,
    param: String,
    label: String = "Shape",
    availableShapes: Collection<RectangularShape> = RectangularShape.entries,
) {
    selectValue(label, param, availableShapes, shape)
}

// parse

fun parseComplexShape(parameters: Parameters, param: String) =
    when (parse(parameters, param, ComplexShapeType.Circular)) {
        ComplexShapeType.Circular -> UsingCircularShape(
            parseCircularShape(parameters, combine(param, SHAPE, 0)),
        )

        ComplexShapeType.Rectangular -> parseUsingRectangularShape(parameters, param)
    }

fun parseUsingRectangularShape(
    parameters: Parameters,
    param: String,
) = UsingRectangularShape(
    parseRectangularShape(parameters, combine(param, SHAPE, 1)),
    parseFactor(parameters, combine(param, SIZE), HALF),
)

fun parseCircularShape(
    parameters: Parameters,
    param: String,
    default: CircularShape = CircularShape.Circle,
) = parse(parameters, param, default)

fun parseRectangularShape(
    parameters: Parameters,
    param: String,
    default: RectangularShape = RectangularShape.Rectangle,
) = parse(parameters, param, default)
