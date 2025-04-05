package at.orchaldir.gm.app.html.model.character

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.parseFactor
import at.orchaldir.gm.app.html.model.selectPercentage
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.parseOrientation
import at.orchaldir.gm.core.generator.AppearanceGeneratorConfig
import at.orchaldir.gm.core.generator.generateHorn
import at.orchaldir.gm.core.generator.generateHorns
import at.orchaldir.gm.core.model.character.appearance.horn.*
import at.orchaldir.gm.core.model.race.appearance.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Orientation
import io.ktor.http.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// edit

fun FORM.editHorns(
    raceAppearance: RaceAppearance,
    horns: Horns,
) {
    val options = raceAppearance.horn

    h2 { +"Horns" }

    selectOneOf("Type", combine(HORN, LAYOUT), options.layouts, horns.getType(), true)
    when (horns) {
        NoHorns -> doNothing()
        is TwoHorns -> editHorn(options, horns.horn, HORN, "Horn")
        is DifferentHorns -> {
            editHorn(options, horns.left, combine(HORN, LEFT), "Left Horn")
            editHorn(options, horns.right, combine(HORN, RIGHT), "Right Horn")
        }

        is CrownOfHorns -> {
            selectOneOf(
                "Horns in Crown (Front)",
                combine(CROWN, FRONT),
                options.crownFront,
                horns.front,
                true
            ) { number ->
                label = number.toString()
                value = number.toString()
            }
            selectOneOf("Horns in Crown (Back)", combine(CROWN, BACK), options.crownBack, horns.back, true) { number ->
                label = number.toString()
                value = number.toString()
            }
            selectCrownLength(horns.length)
            selectHornWidth(CROWN, horns.width)
            selectFeatureColor(options.colors, horns.color, combine(CROWN, COLOR))
        }
    }
}

private fun FORM.editHorn(
    options: HornOptions,
    horn: Horn,
    param: String,
    noun: String,
) {
    showDetails(noun, true) {
        selectValue("Type", combine(param, TYPE), HornType.entries, horn.getType(), true)

        when (horn) {
            is SimpleHorn -> {
                selectHornLength(param, horn.length)
                selectOneOf("Simple Type", combine(param, SHAPE), options.simpleTypes, horn.simpleType, true)
                selectFeatureColor(options.colors, horn.color, combine(param, COLOR))
            }

            is ComplexHorn -> {
                selectHornLength(param, horn.length)
                selectHornWidth(param, horn.relativeWidth)
                selectValue("Position", combine(param, POSITION), HornPosition.entries, horn.position, true)
                selectOrientation(param, horn.orientationOffset, 90.0f)
                editHornShape(horn.shape, param)
                selectFeatureColor(options.colors, horn.color, combine(param, COLOR))
            }
        }
    }
}

private fun HtmlBlockTag.editHornShape(
    shape: HornShape,
    parentParam: String,
) {
    val param = combine(parentParam, SHAPE)

    showDetails("Shape", true) {
        selectValue("Type", combine(param, TYPE), HornShapeType.entries, shape.getType(), true)

        when (shape) {
            StraightHorn -> doNothing()
            is CurvedHorn -> selectOrientation(param, shape.change, 360.0f)
            is SpiralHorn -> {
                selectInt(
                    "Cycles",
                    shape.cycles,
                    MIN_SPIRAL_CYCLES,
                    MAX_SPIRAL_CYCLES,
                    1,
                    combine(param, NUMBER),
                    true,
                )
                selectPercentage(
                    "Amplitude",
                    combine(param, LENGTH),
                    shape.amplitude,
                    1,
                    100,
                    1,
                    true,
                )
            }
        }
    }
}

private fun HtmlBlockTag.selectOrientation(param: String, offset: Orientation, maxValue: Float) {
    selectFloat(
        "Orientation",
        offset.toDegree(),
        -maxValue,
        maxValue,
        1.0f,
        combine(param, ORIENTATION),
        true,
    )
}

fun HtmlBlockTag.selectHornLength(param: String, length: Factor) {
    selectPercentage(
        "Horn Length",
        combine(param, LENGTH),
        length,
        10,
        200,
        5,
        true,
    )
}

fun HtmlBlockTag.selectHornWidth(param: String, width: Factor) {
    selectPercentage(
        "Horn Width",
        combine(param, WIDTH),
        width,
        1,
        50,
        1,
        true,
    )
}

fun HtmlBlockTag.selectCrownLength(length: Factor) {
    selectPercentage(
        "Horn Length",
        combine(CROWN, LENGTH),
        length,
        1,
        50,
        1,
        true,
    )
}

// parse

fun parseHorns(parameters: Parameters, config: AppearanceGeneratorConfig): Horns {
    val options = config.appearanceOptions.horn

    return when (parameters[combine(HORN, LAYOUT)]) {
        HornsLayout.None.toString() -> NoHorns
        HornsLayout.Two.toString() -> TwoHorns(parseHorn(parameters, HORN, config))
        HornsLayout.Different.toString() -> DifferentHorns(
            parseHorn(parameters, combine(HORN, LEFT), config),
            parseHorn(parameters, combine(HORN, RIGHT), config),
        )

        HornsLayout.Crown.toString() -> {
            CrownOfHorns(
                parseInt(parameters, combine(CROWN, FRONT), DEFAULT_CROWN_HORNS),
                parseInt(parameters, combine(CROWN, BACK), DEFAULT_CROWN_HORNS),
                true,
                parseFactor(parameters, combine(CROWN, LENGTH), DEFAULT_CROWN_LENGTH),
                parseFactor(parameters, combine(CROWN, WIDTH), DEFAULT_CROWN_WIDTH),
                parseFeatureColor(parameters, config, options.colors, combine(CROWN, COLOR)),
            )
        }

        else -> generateHorns(config)
    }
}

private fun parseHorn(parameters: Parameters, param: String, config: AppearanceGeneratorConfig): Horn {
    val options = config.appearanceOptions.horn

    return when (parameters[combine(param, TYPE)]) {
        HornType.Simple.toString() -> SimpleHorn(
            parseFactor(parameters, combine(param, LENGTH), DEFAULT_SIMPLE_LENGTH),
            parseAppearanceOption(parameters, combine(param, SHAPE), config, options.simpleTypes),
            parseFeatureColor(parameters, config, options.colors, combine(param, COLOR)),
        )

        HornType.Complex.toString() -> ComplexHorn(
            parseFactor(parameters, combine(param, LENGTH), DEFAULT_SIMPLE_LENGTH),
            parseFactor(parameters, combine(param, WIDTH), DEFAULT_SIMPLE_WIDTH),
            parse(parameters, combine(param, POSITION), HornPosition.Top),
            parseOrientation(parameters, combine(param, ORIENTATION)),
            parseHornShape(parameters, param),
            parseFeatureColor(parameters, config, options.colors, combine(param, COLOR)),
        )

        else -> generateHorn(config, options)
    }
}

private fun parseHornShape(parameters: Parameters, parentParam: String): HornShape {
    val param = combine(parentParam, SHAPE)

    return when (parameters[combine(param, TYPE)]) {
        HornShapeType.Curved.toString() -> CurvedHorn(parseOrientation(parameters, combine(param, ORIENTATION)))
        HornShapeType.Spiral.toString() -> SpiralHorn(
            parseInt(parameters, combine(param, NUMBER), DEFAULT_SPIRAL_CYCLES),
            parseFactor(parameters, combine(param, LENGTH), DEFAULT_SPIRAL_AMPLITUDE),
        )

        else -> StraightHorn
    }
}

