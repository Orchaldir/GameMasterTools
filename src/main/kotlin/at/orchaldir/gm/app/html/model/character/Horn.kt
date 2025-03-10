package at.orchaldir.gm.app.html.model.character

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.model.race.selectCrownLength
import at.orchaldir.gm.app.html.model.race.selectHornLength
import at.orchaldir.gm.app.html.selectColor
import at.orchaldir.gm.app.html.selectOneOf
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseFactor
import at.orchaldir.gm.core.generator.AppearanceGeneratorConfig
import at.orchaldir.gm.core.generator.generateHair
import at.orchaldir.gm.core.generator.generateHorn
import at.orchaldir.gm.core.generator.generateHorns
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.character.appearance.horn.*
import at.orchaldir.gm.core.model.race.appearance.DEFAULT_SIMPLE_LENGTH
import at.orchaldir.gm.core.model.race.appearance.HornOptions
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import kotlinx.html.FORM
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

            is ComplexHorn -> doNothing()
        }
    }
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

        else -> generateHorn(config, options)
    }
}

