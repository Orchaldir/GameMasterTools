package at.orchaldir.gm.app.html.ecology.plant

import at.orchaldir.gm.app.BASE
import at.orchaldir.gm.app.BRANCH
import at.orchaldir.gm.app.LENGTH
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.ORIENTATION
import at.orchaldir.gm.app.PROBABILITY
import at.orchaldir.gm.app.SIDE
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.math.*
import at.orchaldir.gm.core.model.ecology.plant.appearance.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.QUARTER
import at.orchaldir.gm.utils.math.THIRD
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showBranching(
    branching: Branching,
) {
    showDetails("Branching", true) {
        field("Type", branching.getType())

        when (branching) {
            NoBranching -> doNothing()
            is SimpleBranching -> {
                field("Max Number", branching.maxCount)
                field("Side Pattern", branching.sidePattern)
                fieldFactor("Base", branching.base)
                fieldFactor("Max Length", branching.maxLength)
                field("Length", branching.length)
                showStem(branching.branch)
                fieldVariance("Angle Offset", branching.angle)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editBranching(
    branching: Branching,
    param: String,
) {
    val branchingParam = combine(param, BRANCH)

    showDetails("Branching", true) {
        selectValue(
            "Type",
            branchingParam,
            BranchingType.entries,
            branching.getType(),
        )

        when (branching) {
            NoBranching -> doNothing()
            is SimpleBranching -> {
                selectInt(
                    "Max Number",
                    branching.maxCount,
                    MIN_BRANCHES,
                    MAX_BRANCHES,
                    1,
                    combine(branchingParam, NUMBER),
                )
                selectValue(
                    "Side Pattern",
                    combine(branchingParam, SIDE),
                    BranchSidePattern.entries,
                    branching.sidePattern,
                )
                selectFactor(
                    "Base",
                    combine(branchingParam, BASE),
                    branching.base,
                )
                selectFactor(
                    "Max Length",
                    combine(branchingParam, LENGTH),
                    branching.maxLength,
                )
                selectValue(
                    "Side Pattern",
                    combine(branchingParam, SIDE),
                    BranchSidePattern.entries,
                    branching.sidePattern,
                )
                selectValue(
                    "Length",
                    combine(branchingParam, LENGTH, TYPE),
                    BranchLength.entries,
                    branching.length,
                )
                editStem(branching.branch, branchingParam)
                selectOrientationVariance(
                    "Angle Offset",
                    combine(branchingParam, ORIENTATION),
                    branching.angle,
                    MIN_BRANCH_CENTER,
                            MAX_BRANCH_CENTER,
                    MAX_BRANCH_OFFSET,
                )
            }
        }
    }
}

// parse

fun parseBranching(
    parameters: Parameters,
    param: String,
): Branching {
    val branchingParam = combine(param, BRANCH)

    return when (parse(parameters, branchingParam, BranchingType.None)) {
        BranchingType.None -> NoBranching

        BranchingType.Simple -> SimpleBranching(
            parseInt(parameters, combine(branchingParam, NUMBER), 10),
            parse(parameters, combine(branchingParam, SIDE), BranchSidePattern.BothSides),
            parseFactor(parameters, combine(branchingParam, BASE), DEFAULT_BRANCHING_BASE),
            parseFactor(parameters, combine(branchingParam, LENGTH), QUARTER),
            parse(parameters, combine(branchingParam, LENGTH, TYPE), BranchLength.Conical),
            parseStem(parameters, branchingParam),
            parseAngle(parameters, branchingParam),
        )
    }
}


private fun parseAngle(
    parameters: Parameters,
    param: String,
) = parseOrientationVariance(
    parameters,
    combine(param, ORIENTATION),
    DEFAULT_BRANCH_CENTER,
)
