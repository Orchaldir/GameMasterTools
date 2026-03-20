package at.orchaldir.gm.app.html.character.appearance

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.EXOTIC
import at.orchaldir.gm.app.STRIPE
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.generator.AppearanceGeneratorConfig
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.race.appearance.HairColorOptions
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.style

// show

fun HtmlBlockTag.fieldHairColor(
    hairColor: HairColor,
    text: String = "Hair Color",
) = field(text) {
    displayHairColor(hairColor)
}

fun HtmlBlockTag.displayHairColor(hairColor: HairColor) = when (hairColor) {
    is NoHairColor -> +"None"
    is NormalHairColor -> showHairColor(CHARACTER_CONFIG, hairColor.color)
    is ExoticHairColor -> showColor(hairColor.color)
    is StrippedHairColor -> {
        showColor(hairColor.color0)
        +" & "
        showColor(hairColor.color1)
    }
}

fun HtmlBlockTag.showHairColor(
    hairColor: HairColor,
    text: String = "Hair Color",
) {
    showDetails(text, true) {
        field("Type", hairColor.getType())

        when (hairColor) {
            is NoHairColor -> doNothing()
            is NormalHairColor -> fieldNormalHairColor(CHARACTER_CONFIG, hairColor.color)
            is ExoticHairColor -> fieldColor(hairColor.color)
            is StrippedHairColor -> {
                fieldColor(hairColor.color0, "1.Color")
                fieldColor(hairColor.color1, "1.Color")
            }
        }
    }
}

// edit

fun HtmlBlockTag.selectHairColor(
    options: HairColorOptions,
    hairColor: HairColor,
    param: String,
    text: String = "Hair Color",
) {
    val colorParam = combine(param, COLOR)

    showDetails(text, true) {
        selectFromOneOf(
            "Type",
            combine(colorParam, TYPE),
            options.types,
            hairColor.getType(),
        )

        when (hairColor) {
            is NoHairColor -> doNothing()
            is NormalHairColor -> selectFromOneOf(
                "Color",
                colorParam,
                options.normal,
                hairColor.color,
            ) { skinColor ->
                label = skinColor.name
                value = skinColor.toString()
                val bgColor = CHARACTER_CONFIG.colors.getHairColor(skinColor).toCode()
                style = "background-color:${bgColor}"
            }

            is ExoticHairColor -> selectColor(
                "Color",
                combine(colorParam, EXOTIC),
                options.exotic,
                hairColor.color,
            )

            is StrippedHairColor -> {
                selectColor(
                    "1.Color",
                    combine(colorParam, STRIPE, 0),
                    options.exotic - hairColor.color1,
                    hairColor.color0,
                )
                selectColor(
                    "2.Color",
                    combine(colorParam, STRIPE, 1),
                    options.exotic - hairColor.color0,
                    hairColor.color1,
                )
            }
        }
    }
}

// parse

fun parseHairColor(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
    options: HairColorOptions,
    param: String,
): HairColor {
    val colorParam = combine(param, COLOR)

    return when (parse(parameters, combine(colorParam, TYPE), HairColorType.Normal)) {
        HairColorType.None -> NoHairColor
        HairColorType.Normal -> NormalHairColor(
            parseAppearanceOption(
                parameters,
                colorParam,
                config,
                options.normal,
            ),
        )

        HairColorType.Exotic -> ExoticHairColor(
            parseAppearanceColor(
                parameters,
                combine(colorParam, EXOTIC),
                config,
                options.exotic,
            ),
        )

        HairColorType.Stripped -> {
            val color0 = parseAppearanceColor(
                parameters,
                combine(colorParam, STRIPE, 0),
                config,
                options.exotic,
            )
            StrippedHairColor(
                color0,
                parseAppearanceColor(
                    parameters,
                    combine(colorParam, STRIPE, 1),
                    config,
                    options.exotic - color0,
                )
            )
        }
    }
}