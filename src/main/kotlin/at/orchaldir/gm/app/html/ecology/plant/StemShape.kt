package at.orchaldir.gm.app.html.ecology.plant

import at.orchaldir.gm.app.ORIENTATION
import at.orchaldir.gm.app.SHAPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.math.fieldVariance
import at.orchaldir.gm.app.html.util.math.parseOrientationVariance
import at.orchaldir.gm.app.html.util.math.selectOrientationVariance
import at.orchaldir.gm.core.model.ecology.plant.appearance.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.ZERO_ORIENTATION
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showStemShape(
    shape: StemShape,
) {
    showDetails("Stem Shape", true) {
        field("Type", shape.getType())

        when (shape) {
            StraightStem -> doNothing()
            is CurvedStem -> fieldVariance("Angle", shape.angle)
        }
    }
}

// edit

fun HtmlBlockTag.editStemShape(
    shape: StemShape,
    param: String,
) {
    val shapeParam = combine(param, SHAPE)

    showDetails("Stem Shape", true) {
        selectValue(
            "Type",
            shapeParam,
            StemShapeType.entries,
            shape.getType(),
        )

        when (shape) {
            StraightStem -> doNothing()
            is CurvedStem -> selectOrientationVariance(
                "Angle Offset",
                combine(shapeParam, ORIENTATION),
                shape.angle,
                MIN_CURVE_CENTER,
                MAX_CURVE_CENTER,
                MAX_CURVE_OFFSET,
            )
        }
    }
}

// parse

fun parseStemShape(
    parameters: Parameters,
    param: String,
): StemShape {
    val shapeParam = combine(param, SHAPE)

    return when (parse(parameters, shapeParam, StemShapeType.Straight)) {
        StemShapeType.Straight -> StraightStem
        StemShapeType.Curved -> CurvedStem(
            parseOrientationVariance(
                parameters,
                combine(shapeParam, ORIENTATION),
                ZERO_ORIENTATION,
            ),
        )
    }
}
