package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldAccuracy(
    accuracy: Accuracy,
) {
    field("Accuracy") {
        displayAccuracy(accuracy, true)
    }
}

fun HtmlBlockTag.displayAccuracy(
    accuracy: Accuracy,
    showUndefined: Boolean = false,
) {
    when (accuracy) {
        is SimpleAccuracy -> +accuracy.modifier.toString()
        is AccuracyWithScope -> +"${accuracy.base}+${accuracy.scope}"
        UndefinedAccuracy -> if(showUndefined) {
            +"Undefined"
        }
    }
}

// edit

fun HtmlBlockTag.editAccuracy(
    accuracy: Accuracy,
    param: String,
) {
    val accuracyParam = combine(param, ACCURACY)
    val minAccuracy = 0
    val maxAccuracy = 10

    showDetails("Accuracy", true) {
        selectValue(
            "Type",
            combine(accuracyParam, TYPE),
            AccuracyType.entries,
            accuracy.getType(),
        )

        when (accuracy) {
            is SimpleAccuracy -> selectInt(
                "Modifier",
                accuracy.modifier,
                minAccuracy,
                maxAccuracy,
                1,
                combine(accuracyParam, NUMBER),
            )

            is AccuracyWithScope -> {
                selectInt(
                    "Base",
                    accuracy.base,
                    minAccuracy,
                    maxAccuracy,
                    1,
                    combine(accuracyParam, NUMBER),
                )
                selectInt(
                    "Scope",
                    accuracy.scope,
                    minAccuracy,
                    maxAccuracy,
                    1,
                    combine(accuracyParam, MODIFIER),
                )
            }

            UndefinedAccuracy -> doNothing()
        }
    }
}

// parse

fun parseAccuracy(
    parameters: Parameters,
    param: String,
): Accuracy {
    val accuracyParam = combine(param, ACCURACY)

    return when (parse(parameters, combine(accuracyParam, TYPE), AccuracyType.Undefined)) {
        AccuracyType.Simple -> SimpleAccuracy(
            parseInt(parameters, combine(accuracyParam, NUMBER), 1),
        )

        AccuracyType.Scope -> AccuracyWithScope(
            parseInt(parameters, combine(accuracyParam, NUMBER), 1),
            parseInt(parameters, combine(accuracyParam, MODIFIER), 1),
        )

        AccuracyType.Undefined -> UndefinedAccuracy
    }
}
