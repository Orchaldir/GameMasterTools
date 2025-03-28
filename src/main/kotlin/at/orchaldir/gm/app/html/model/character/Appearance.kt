package at.orchaldir.gm.app.html.model.character

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.model.parseDistance
import at.orchaldir.gm.app.html.model.selectDistance
import at.orchaldir.gm.app.html.selectColor
import at.orchaldir.gm.app.html.selectOneOf
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.routes.character.showCurrentHeight
import at.orchaldir.gm.core.generator.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.wing.*
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.race.appearance.FootOptions
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.model.race.appearance.WingOptions
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import io.ktor.http.*
import kotlinx.html.FORM
import kotlinx.html.h2
import kotlinx.html.style

// edit

fun FORM.editAppearance(
    state: State,
    raceAppearance: RaceAppearance,
    appearance: Appearance,
    character: Character,
    culture: Culture,
) {
    selectOneOf(
        "Appearance Type",
        APPEARANCE,
        raceAppearance.appearanceTypes,
        appearance.getType(),
        true
    )

    when (appearance) {
        is HeadOnly -> {
            editHeight(state, character, appearance.height)
            editHead(raceAppearance, culture, appearance.head)
            editSkin(raceAppearance, appearance.head.skin)
        }

        is HumanoidBody -> {
            editHeight(state, character, appearance.height)
            editBody(raceAppearance, character, appearance.body)
            editHead(raceAppearance, culture, appearance.head)
            editSkin(raceAppearance, appearance.head.skin)
            editWings(raceAppearance, appearance.wings)
        }

        UndefinedAppearance -> doNothing()
    }
}

private fun FORM.editHeight(
    state: State,
    character: Character,
    maxHeight: Distance,
) {
    val race = state.getRaceStorage().getOrThrow(character.race)
    selectDistance("Max Height", HEIGHT, maxHeight, race.height.getMin(), race.height.getMax(), fromMillimeters(10))
    showCurrentHeight(state, character, maxHeight)
}

private fun FORM.editBody(
    raceAppearance: RaceAppearance,
    character: Character,
    body: Body,
) {
    h2 { +"Body" }
    selectValue("Shape", BODY_SHAPE, getAvailableBodyShapes(character.gender), body.bodyShape, true)
    selectValue("Width", BODY_WIDTH, Size.entries, body.width, true)
    editFoot(raceAppearance.footOptions, body.foot)
}

private fun FORM.editFoot(footOptions: FootOptions, foot: Foot) {
    h2 { +"Feet" }

    selectOneOf("Type", FOOT, footOptions.footTypes, foot.getType(), true)

    when (foot) {
        is ClawedFoot -> {
            selectOneOf("Claw Size", combine(FOOT, CLAWS, SIZE), footOptions.clawSizes, foot.size, true)
            selectOneOf("Claw Color", combine(FOOT, CLAWS, COLOR), footOptions.clawColors, foot.color, true)
        }

        else -> doNothing()
    }
}

private fun FORM.editSkin(
    raceAppearance: RaceAppearance,
    skin: Skin,
) {
    h2 { +"Skin" }

    selectOneOf("Type", combine(SKIN, TYPE), raceAppearance.skinTypes, skin.getType(), true)

    when (skin) {
        is Fur -> selectColor("Color", combine(SKIN, EXOTIC, COLOR), raceAppearance.furColors, skin.color)
        is Scales -> selectColor("Color", combine(SKIN, EXOTIC, COLOR), raceAppearance.scalesColors, skin.color)
        is ExoticSkin -> selectColor("Color", combine(SKIN, EXOTIC, COLOR), raceAppearance.exoticSkinColors, skin.color)
        is NormalSkin -> {
            selectOneOf("Color", combine(SKIN, COLOR), raceAppearance.normalSkinColors, skin.color, true) { skinColor ->
                label = skinColor.name
                value = skinColor.toString()
                val bgColor = CHARACTER_CONFIG.getSkinColor(skinColor).toCode()
                style = "background-color:${bgColor}"
            }
        }
    }
}

private fun FORM.editWings(
    raceAppearance: RaceAppearance,
    wings: Wings,
) {
    val wingOptions = raceAppearance.wingOptions

    h2 { +"Wings" }

    selectOneOf("Layout", combine(WING, LAYOUT), wingOptions.layouts, wings.getType(), true)

    when (wings) {
        NoWings -> doNothing()
        is OneWing -> {
            editWing(wingOptions, wings.wing, WING)
            selectValue("Wing Side", combine(WING, SIDE), Side.entries, wings.side, true)
        }

        is TwoWings -> editWing(wingOptions, wings.wing, WING)
        is DifferentWings -> {
            editWing(wingOptions, wings.left, combine(LEFT, WING))
            editWing(wingOptions, wings.right, combine(RIGHT, WING))
        }
    }
}

private fun FORM.editWing(
    wingOptions: WingOptions,
    wing: Wing,
    param: String,
) {
    selectWingType(wingOptions, wing.getType(), combine(param, TYPE))

    when (wing) {
        is BatWing -> selectColor("Wing Color", combine(param, COLOR), wingOptions.batColors, wing.color)
        is BirdWing -> selectColor("Wing Color", combine(param, COLOR), wingOptions.birdColors, wing.color)
        is ButterflyWing -> selectColor("Wing Color", combine(param, COLOR), wingOptions.butterflyColors, wing.color)
    }
}

private fun FORM.selectWingType(
    wingOptions: WingOptions,
    currentType: WingType,
    param: String,
) {
    selectOneOf("Type", param, wingOptions.types, currentType, true)
}

// parse

fun parseAppearance(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
    character: Character,
): Appearance {
    val height = parseHeight(parameters, config)
    val skin = parseSkin(parameters, config)

    return when (parameters[APPEARANCE]) {
        AppearanceType.HeadOnly.toString() -> HeadOnly(parseHead(parameters, config, character, skin), height)
        AppearanceType.Body.toString() -> HumanoidBody(
            parseBody(parameters, config, skin),
            parseHead(parameters, config, character, skin),
            height,
            parseWings(parameters, config),
        )

        else -> UndefinedAppearance
    }
}

private fun parseHeight(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
) = parseDistance(parameters, HEIGHT, config.heightDistribution.center.value())

private fun parseBody(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
    skin: Skin,
): Body {
    if (parameters.contains(BODY_SHAPE)) {
        return Body(
            parse(parameters, BODY_SHAPE, BodyShape.Rectangle),
            parseFoot(parameters, config),
            parse(parameters, BODY_WIDTH, Size.Medium),
            skin,
        )
    }

    return generateBody(config, skin)
}

private fun parseFoot(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
) = when (parameters[FOOT]) {
    FootType.Normal.toString() -> NormalFoot
    FootType.Clawed.toString() -> {
        val options = config.appearanceOptions.footOptions
        ClawedFoot(
            options.clawNumber,
            parseAppearanceOption(parameters, combine(FOOT, CLAWS, SIZE), config, options.clawSizes),
            parseAppearanceColor(parameters, combine(FOOT, CLAWS), config, options.clawColors),
        )
    }

    else -> generateFoot(config)
}

private fun parseSkin(parameters: Parameters, config: AppearanceGeneratorConfig): Skin {
    val options = config.appearanceOptions

    return when (parameters[combine(SKIN, TYPE)]) {
        SkinType.Fur.toString() -> {
            return Fur(parseExoticColor(parameters, config, options.furColors))
        }

        SkinType.Scales.toString() -> {
            return Scales(parseExoticColor(parameters, config, options.scalesColors))
        }

        SkinType.Exotic.toString() -> {
            return ExoticSkin(parseExoticColor(parameters, config, options.exoticSkinColors))
        }

        SkinType.Normal.toString() -> {
            val color = parseAppearanceOption(parameters, combine(SKIN, COLOR), config, options.normalSkinColors)
            return NormalSkin(color)
        }

        else -> generateSkin(config)
    }
}

private fun parseExoticColor(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
    colors: OneOf<Color>,
) = parseAppearanceColor(parameters, combine(SKIN, EXOTIC), config, colors)

private fun parseWings(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
) = when (parameters[combine(WING, LAYOUT)]) {
    WingsLayout.None.toString() -> NoWings
    WingsLayout.One.toString() -> OneWing(
        parseWing(parameters, config, WING),
        parse(parameters, combine(WING, SIDE), Side.Right),
    )

    WingsLayout.Two.toString() -> TwoWings(
        parseWing(parameters, config, WING),
    )

    WingsLayout.Different.toString() -> DifferentWings(
        parseWing(parameters, config, combine(LEFT, WING)),
        parseWing(parameters, config, combine(RIGHT, WING)),
    )

    else -> generateWings(config)
}

private fun parseWing(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
    param: String,
): Wing {
    val wingOptions = config.appearanceOptions.wingOptions

    return when (parameters[combine(param, TYPE)]) {
        WingType.Bat.toString() -> BatWing(
            parseAppearanceColor(parameters, param, config, wingOptions.batColors),
        )

        WingType.Bird.toString() -> BirdWing(
            parseAppearanceColor(parameters, param, config, wingOptions.birdColors),
        )

        WingType.Butterfly.toString() -> ButterflyWing(
            parseAppearanceColor(parameters, param, config, wingOptions.butterflyColors),
        )

        else -> generateWing(config)
    }
}

fun parseAppearanceColor(
    parameters: Parameters,
    param: String,
    config: AppearanceGeneratorConfig,
    colors: OneOf<Color>,
) = parseAppearanceOption(parameters, combine(param, COLOR), config, colors)

inline fun <reified T : Enum<T>> parseAppearanceOption(
    parameters: Parameters,
    param: String,
    config: AppearanceGeneratorConfig,
    values: OneOf<T>,
) = parse<T>(parameters, param) ?: config.generate(values)