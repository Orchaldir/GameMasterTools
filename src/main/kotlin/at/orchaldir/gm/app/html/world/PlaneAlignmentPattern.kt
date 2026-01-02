package at.orchaldir.gm.app.html.world

import at.orchaldir.gm.app.ALIGNMENT
import at.orchaldir.gm.app.PATTERN
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.core.model.world.plane.*
import at.orchaldir.gm.core.model.world.plane.PlanarAlignment.*
import at.orchaldir.gm.core.model.world.plane.PlaneAlignmentPatternType.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.titlecaseFirstChar
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showPlaneAlignmentPattern(
    pattern: PlaneAlignmentPattern,
) {
    field("Alignment Pattern") {
        displayPlaneAlignmentPattern(pattern)
    }
}

fun HtmlBlockTag.displayPlaneAlignmentPattern(pattern: PlaneAlignmentPattern) {
    when (pattern) {
        is FixedAlignment -> +"Always ${pattern.alignment}"
        is PlanarCycle -> +"${pattern.waxing}/${pattern.coterminous}/${pattern.waning}/${pattern.remote}"
        RandomAlignment -> +"Random"
    }
}

// edit

private const val MAX_VALUE = 10000

fun HtmlBlockTag.editPlaneAlignmentPattern(
    pattern: PlaneAlignmentPattern,
) {
    showDetails("Alignment Pattern", true) {
        selectValue("Type", PATTERN, PlaneAlignmentPatternType.entries, pattern.getType())
        when (pattern) {
            is FixedAlignment -> selectValue(
                "Alignment",
                combine(PATTERN, ALIGNMENT),
                PlanarAlignment.entries,
                pattern.alignment
            )

            is PlanarCycle -> PlanarAlignment.entries.forEach { alignment ->
                selectInt(
                    alignment.name.titlecaseFirstChar(),
                    pattern.getValue(alignment),
                    1, MAX_VALUE,
                    1,
                    combine(PATTERN, alignment.name)
                )
            }

            RandomAlignment -> doNothing()
        }
    }
}

// parse

fun parsePlaneAlignmentPattern(parameters: Parameters) = when (parse(parameters, PATTERN, Random)) {
    Cycle -> PlanarCycle(
        parseCycleValue(parameters, Waxing),
        parseCycleValue(parameters, Coterminous),
        parseCycleValue(parameters, Waning),
        parseCycleValue(parameters, Remote),
    )

    Fixed -> FixedAlignment(parse(parameters, combine(PATTERN, ALIGNMENT), Coterminous))
    Random -> RandomAlignment
}

private fun parseCycleValue(parameters: Parameters, alignment: PlanarAlignment) =
    parseInt(parameters, combine(PATTERN, alignment.name), 1)