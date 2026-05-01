package at.orchaldir.gm.app.html.ecology.plant

import at.orchaldir.gm.app.ORIENTATION
import at.orchaldir.gm.app.PROBABILITY
import at.orchaldir.gm.app.SPLIT
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.math.*
import at.orchaldir.gm.core.model.ecology.plant.appearance.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Variance
import at.orchaldir.gm.utils.math.unit.Orientation
import io.ktor.http.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showStemSplitting(
    splitting: StemSplitting,
) {
    showDetails("Stem Splitting", true) {
        field("Type", splitting.getType())

        when (splitting) {
            NoStemSplitting -> doNothing()
            is BaseSplitting -> showProbabilityAndAngle(splitting.probability, splitting.angle)
            is SegmentSplitting -> showProbabilityAndAngle(splitting.probability, splitting.angle)
        }
    }
}

private fun DETAILS.showProbabilityAndAngle(
    probability: Factor,
    angle: Variance<Orientation>,
) {
    fieldFactor("Probability", probability)
    fieldVariance("Angle Offset", angle)
}


// edit

fun HtmlBlockTag.editStemSplitting(
    splitting: StemSplitting,
    param: String,
) {
    val splitParam = combine(param, SPLIT)

    showDetails("Stem Splitting", true) {
        selectValue(
            "Type",
            splitParam,
            StemSplittingType.entries,
            splitting.getType(),
        )

        when (splitting) {
            NoStemSplitting -> doNothing()
            is BaseSplitting -> editProbabilityAndAngle(splitParam, splitting.probability, splitting.angle)
            is SegmentSplitting -> editProbabilityAndAngle(splitParam, splitting.probability, splitting.angle)
        }
    }
}

private fun DETAILS.editProbabilityAndAngle(
    param: String,
    probability: Factor,
    angle: Variance<Orientation>,
) {
    selectFactor(
        "Probability",
        combine(param, PROBABILITY),
        probability,
        MIN_SPLITTING_PROBABILITY,
        MAX_SPLITTING_PROBABILITY,
    )
    selectOrientationVariance(
        "Angle Offset",
        combine(param, ORIENTATION),
        angle,
        MIN_SPLITTING_ANGLE,
        MAX_SPLITTING_ANGLE,
        MIN_SPLITTING_OFFSET,
        MAX_SPLITTING_OFFSET,
    )
}

// parse

fun parseStemSplitting(
    parameters: Parameters,
    param: String,
): StemSplitting {
    val splitParam = combine(param, SPLIT)

    return when (parse(parameters, splitParam, StemSplittingType.None)) {
        StemSplittingType.None -> NoStemSplitting
        StemSplittingType.Segment -> SegmentSplitting(
            parseProbability(parameters, splitParam),
            parseAngle(parameters, splitParam),
        )

        StemSplittingType.Base -> BaseSplitting(
            parseProbability(parameters, splitParam),
            parseAngle(parameters, splitParam),
        )
    }
}

private fun parseProbability(parameters: Parameters, param: String) =
    parseFactor(parameters, combine(param, PROBABILITY))

private fun parseAngle(
    parameters: Parameters,
    param: String,
) = parseOrientationVariance(
    parameters,
    combine(param, ORIENTATION),
    MIN_SPLITTING_ANGLE,
)
