package at.orchaldir.gm.app.html.model.character

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.*
import at.orchaldir.gm.core.generator.AppearanceGeneratorConfig
import at.orchaldir.gm.core.generator.generateHorn
import at.orchaldir.gm.core.generator.generateHorns
import at.orchaldir.gm.core.model.character.appearance.horn.*
import at.orchaldir.gm.core.model.race.appearance.*
import at.orchaldir.gm.core.model.util.Color
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
    val options = raceAppearance.hornOptions

    h2 { +"Horns" }

    selectOneOf("Type", combine(HORN, LAYOUT), options.layouts, horns.getType(), true) { layout ->
        label = layout.name
        value = layout.toString()
    }
    when (horns) {
        NoHorns -> doNothing()
        is TwoHorns -> editHorn(options, horns.horn, HORN, "Horn")
        is DifferentHorns -> {
            editHorn(options, horns.left, combine(HORN, LEFT), "Left Horn")
            editHorn(options, horns.right, combine(HORN, RIGHT), "Right Horn")
        }

        is CrownOfHorns -> {
            selectCrownLength(options.crownLength)
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
                selectOneOf("Simple Type", combine(param, SHAPE), options.simpleTypes, horn.type, true) { shape ->
                    label = shape.name
                    value = shape.toString()
                }
                selectColor("Color", combine(param, COLOR), options.colors, horn.color)
            }

            is ComplexHorn -> {
                selectHornLength(param, horn.length)
                selectHornWidth(param, horn.relativeWidth)
                selectValue("Position", combine(param, POSITION), HornPosition.entries, horn.position, true)
                selectOrientation(param, horn.orientationOffset, 90.0f)
                editHornShape(horn.shape, param)
                selectColor("Color", combine(param, COLOR), options.colors, horn.color)
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
                selectInt("Cycles", shape.cycles, 2, 10, 1, combine(param, NUMBER), true)
                selectFloat(
                    "Amplitude",
                    shape.amplitude.value,
                    0.01f,
                    1.0f,
                    0.01f,
                    combine(param, LENGTH),
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
    selectFloat(
        "Horn Length",
        length.value,
        0.1f,
        2.0f,
        0.05f,
        combine(param, LENGTH),
        true,
    )
}

fun HtmlBlockTag.selectHornWidth(param: String, width: Factor) {
    selectFloat(
        "Horn Width",
        width.value,
        0.01f,
        0.5f,
        0.01f,
        combine(param, WIDTH),
        true,
    )
}

fun HtmlBlockTag.selectCrownLength(length: Factor) {
    selectFloat(
        "Horn Length",
        length.value,
        0.01f,
        0.5f,
        0.01f,
        combine(CROWN, LENGTH),
        true,
    )
}

// parse

fun parseHorns(parameters: Parameters, config: AppearanceGeneratorConfig): Horns {

    return when (parameters[combine(HORN, LAYOUT)]) {
        HornsLayout.None.toString() -> NoHorns
        HornsLayout.Two.toString() -> TwoHorns(parseHorn(parameters, HORN, config))
        HornsLayout.Different.toString() -> DifferentHorns(
            parseHorn(parameters, combine(HORN, LEFT), config),
            parseHorn(parameters, combine(HORN, RIGHT), config),
        )

        else -> generateHorns(config)
    }
}

private fun parseHorn(parameters: Parameters, param: String, config: AppearanceGeneratorConfig): Horn {
    val options = config.appearanceOptions.hornOptions
    return when (parameters[combine(param, TYPE)]) {
        HornType.Simple.toString() -> SimpleHorn(
            parseFactor(parameters, combine(param, LENGTH), DEFAULT_SIMPLE_LENGTH),
            parse(parameters, combine(param, SHAPE), SimpleHornType.Mouflon),
            parse(parameters, combine(param, COLOR), Color.Red),
        )
        HornType.Complex.toString() -> ComplexHorn(
            parseFactor(parameters, combine(param, LENGTH), DEFAULT_SIMPLE_LENGTH),
            parseFactor(parameters, combine(param, WIDTH), DEFAULT_SIMPLE_WIDTH),
            parse(parameters, combine(param, POSITION), HornPosition.Top),
            parseOrientation(parameters, combine(param, WIDTH)),
            parseHornShape(parameters, param),
            parse(parameters, combine(param, COLOR), Color.Red),
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

