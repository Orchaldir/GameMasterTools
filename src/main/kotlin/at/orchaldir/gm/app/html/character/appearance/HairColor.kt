package at.orchaldir.gm.app.html.character.appearance

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.EXOTIC
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.core.generator.AppearanceGeneratorConfig
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.race.appearance.HairColorOptions
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.render.Color
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
}

fun HtmlBlockTag.showHairColor(
    hairColor: HairColor,
    text: String = "Hair Color",
) {
    showDetails(text, true) {
        field("Type", hairColor.getType())

        when (hairColor) {
            is NoHairColor -> doNothing()
            is NormalHairColor -> field("Color", hairColor.color)
            is ExoticHairColor -> field("Color", hairColor.color)
        }
    }
}

// edit

fun HtmlBlockTag.selectHairColor(
    options: HairColorOptions,
    hairColor: HairColor,
    param: String,
    text: String = "Hair Color",
) = selectHairColor(
    options.types,
    options.normal,
    options.exotic,
    hairColor,
    param,
    text,
)

fun HtmlBlockTag.selectHairColor(
    allowedTypes: OneOf<HairColorType>,
    allowedNormalColors: OneOf<NormalHairColorEnum>,
    allowedExoticColors: OneOf<Color>,
    hairColor: HairColor,
    param: String,
    text: String = "Hair Color",
) {
    val colorParam = combine(param, COLOR)

    showDetails(text, true) {
        selectFromOneOf(
            "Type",
            combine(colorParam, TYPE),
            allowedTypes,
            hairColor.getType(),
        )

        when (hairColor) {
            is NoHairColor -> doNothing()
            is NormalHairColor -> selectFromOneOf(
                "Color",
                colorParam,
                allowedNormalColors,
                hairColor.color,
            ) { skinColor ->
                label = skinColor.name
                value = skinColor.toString()
                val bgColor = CHARACTER_CONFIG.getHairColor(skinColor).toCode()
                style = "background-color:${bgColor}"
            }

            is ExoticHairColor -> selectColor(
                "Color",
                combine(colorParam, EXOTIC),
                allowedExoticColors,
                hairColor.color,
            )
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
    }
}