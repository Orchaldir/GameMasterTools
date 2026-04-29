package at.orchaldir.gm.app.html.ecology.plant

import at.orchaldir.gm.app.END
import at.orchaldir.gm.app.ROUND
import at.orchaldir.gm.app.SPLIT
import at.orchaldir.gm.app.START
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.math.fieldFactor
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.html.util.math.selectFactor
import at.orchaldir.gm.core.model.ecology.plant.appearance.ConstantStemThickness
import at.orchaldir.gm.core.model.ecology.plant.appearance.LinearStemThickness
import at.orchaldir.gm.core.model.ecology.plant.appearance.StemThickness
import at.orchaldir.gm.core.model.ecology.plant.appearance.StemThicknessType
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.ONE
import at.orchaldir.gm.utils.math.ONE_TENTH_PERCENT
import at.orchaldir.gm.utils.math.ZERO
import io.ktor.http.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showStemThickness(
    thickness: StemThickness,
) {
    showDetails("Stem Thickness", true) {
        field("Type", thickness.getType())

        when (thickness) {
            is ConstantStemThickness -> {
                fieldFactor("Thickness relative to Length", thickness.relativeToLength)
                field("Has Rounded End", thickness.hasRoundedEnd)
            }
            is LinearStemThickness -> {
                fieldFactor("Thickness at Start relative to Length", thickness.start)
                fieldFactor("Thickness at End relative to Start", thickness.end)
                field("Has Rounded End", thickness.hasRoundedEnd)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editStemThickness(
    thickness: StemThickness,
    param: String ,
) {
    val splitParam = combine(param, SPLIT)

    showDetails("Stem Thickness", true) {
        selectValue(
            "Type",
            splitParam,
            StemThicknessType.entries,
            thickness.getType(),
        )

        when (thickness) {
            is ConstantStemThickness -> {
                editThicknessRelativeToLength("Thickness relative to Length", thickness.relativeToLength, splitParam)
                selectHasRoundedEnd(thickness.hasRoundedEnd, splitParam)
            }
            is LinearStemThickness -> {
                editThicknessRelativeToLength("Thickness at Start relative to Length", thickness.start, splitParam)
                selectFactor(
                    "Thickness at End relative to Start",
                    combine(splitParam, END),
                    thickness.end,
                )
                selectHasRoundedEnd(thickness.hasRoundedEnd, splitParam)
            }
        }
    }
}

private fun DETAILS.editThicknessRelativeToLength(
    label: String,
    thickness: Factor,
    param: String,
) = selectFactor(
    label,
    combine(param, START),
    thickness,
    ZERO,
    ONE,
    ONE_TENTH_PERCENT,
)

private fun DETAILS.selectHasRoundedEnd(
    hasRoundedEnd: Boolean,
    param: String,
) = selectBool(
    "Has Rounded End",
    hasRoundedEnd,
    combine(param, ROUND),
)

// parse

fun parseStemThickness(
    parameters: Parameters,
    param: String ,
): StemThickness {
    val splitParam = combine(param, SPLIT)

    return when (parse(parameters, splitParam, StemThicknessType.Linear)) {
        StemThicknessType.Constant -> ConstantStemThickness(
            parseThicknessRelativeToLength(parameters, splitParam),
            parseHasRoundedEnd(parameters, splitParam),
        )
        StemThicknessType.Linear -> LinearStemThickness(
            parseThicknessRelativeToLength(parameters, splitParam),
            parseFactor(parameters, combine(param, END)),
            parseHasRoundedEnd(parameters, splitParam),
        )
    }
}

private fun parseThicknessRelativeToLength(parameters: Parameters, param: String) =
    parseFactor(parameters, combine(param, START))

private fun parseHasRoundedEnd(parameters: Parameters, param: String) =
    parseBool(parameters, combine(param, ROUND))
