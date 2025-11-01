package at.orchaldir.gm.app.html.character.appearance

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.character.showCurrentHeight
import at.orchaldir.gm.app.html.selectColor
import at.orchaldir.gm.app.html.selectFromOneOf
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.util.parseDistance
import at.orchaldir.gm.app.html.util.selectDistance
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.generator.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.tail.NoTails
import at.orchaldir.gm.core.model.character.appearance.tail.SimpleTail
import at.orchaldir.gm.core.model.character.appearance.tail.Tails
import at.orchaldir.gm.core.model.character.appearance.tail.TailsLayout
import at.orchaldir.gm.core.model.character.appearance.wing.*
import at.orchaldir.gm.core.model.culture.fashion.AppearanceFashion
import at.orchaldir.gm.core.model.race.appearance.FootOptions
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.model.race.appearance.WingOptions
import at.orchaldir.gm.core.model.util.RarityMap
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.SiPrefix
import io.ktor.http.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2


val siPrefix = SiPrefix.Centi

// edit

fun HtmlBlockTag.editAppearance(
    state: State,
    raceAppearance: RaceAppearance,
    appearance: Appearance,
    character: Character,
    fashion: AppearanceFashion?,
) {
    selectFromOneOf(
        "Appearance Type",
        APPEARANCE,
        raceAppearance.appearanceTypes,
        appearance.getType(),
    )

    when (appearance) {
        is HeadOnly -> {
            editHeight(state, character, appearance.height)
            editHead(state, raceAppearance, fashion, appearance.head)
            editSkin(state, raceAppearance.skin, appearance.skin)
        }

        is HumanoidBody -> {
            editHeight(state, character, appearance.height)
            editBody(raceAppearance, character, appearance.body)
            editHead(state, raceAppearance, fashion, appearance.head)
            editSkin(state, raceAppearance.skin, appearance.skin)
            editTails(state, raceAppearance, appearance.tails)
            editWings(state, raceAppearance, appearance.wings)
        }

        UndefinedAppearance -> doNothing()
    }
}

private fun HtmlBlockTag.editHeight(
    state: State,
    character: Character,
    maxHeight: Distance,
) {
    val race = state.getRaceStorage().getOrThrow(character.race)
    selectDistance("Max Height", HEIGHT, maxHeight, race.height.getMin(), race.height.getMax(), siPrefix)
    showCurrentHeight(state, character, maxHeight)
}

private fun HtmlBlockTag.editBody(
    raceAppearance: RaceAppearance,
    character: Character,
    body: Body,
) {
    h2 { +"Body" }
    selectValue("Shape", BODY_SHAPE, getAvailableBodyShapes(character.gender), body.bodyShape)
    selectValue("Width", BODY_WIDTH, Size.entries, body.width)
    editFoot(raceAppearance.foot, body.foot)
}

private fun HtmlBlockTag.editFoot(footOptions: FootOptions, foot: Foot) {
    h2 { +"Feet" }

    selectFromOneOf("Type", FOOT, footOptions.footTypes, foot.getType())

    when (foot) {
        is ClawedFoot -> {
            selectFromOneOf("Claw Size", combine(FOOT, CLAWS, SIZE), footOptions.clawSizes, foot.size)
            selectFromOneOf("Claw Color", combine(FOOT, CLAWS, COLOR), footOptions.clawColors, foot.color)
        }

        else -> doNothing()
    }
}

private fun HtmlBlockTag.editTails(
    state: State,
    raceAppearance: RaceAppearance,
    tails: Tails,
) {
    val options = raceAppearance.tail

    h2 { +"Tails" }

    selectFromOneOf("Layout", combine(TAIL, LAYOUT), options.layouts, tails.getType())

    when (tails) {
        NoTails -> doNothing()
        is SimpleTail -> {
            val colorOptions = options.getFeatureColorOptions(tails.shape)

            selectFromOneOf("Shape", combine(TAIL, SHAPE), options.simpleShapes, tails.shape)
            selectValue("Size", combine(TAIL, SIZE), Size.entries, tails.size)
            selectFeatureColor(state, colorOptions, tails.color, TAIL)
        }
    }
}

private fun HtmlBlockTag.editWings(
    state: State,
    raceAppearance: RaceAppearance,
    wings: Wings,
) {
    val wingOptions = raceAppearance.wing

    h2 { +"Wings" }

    selectFromOneOf("Layout", combine(WING, LAYOUT), wingOptions.layouts, wings.getType())

    when (wings) {
        NoWings -> doNothing()
        is OneWing -> {
            editWing(state, wingOptions, wings.wing, WING)
            selectValue("Wing Side", combine(WING, SIDE), Side.entries, wings.side)
        }

        is TwoWings -> editWing(state, wingOptions, wings.wing, WING)
        is DifferentWings -> {
            editWing(state, wingOptions, wings.left, combine(LEFT, WING))
            editWing(state, wingOptions, wings.right, combine(RIGHT, WING))
        }
    }
}

private fun HtmlBlockTag.editWing(
    state: State,
    options: WingOptions,
    wing: Wing,
    param: String,
) {
    selectWingType(options, wing.getType(), combine(param, TYPE))

    when (wing) {
        is BatWing -> selectFeatureColor(state, options.batColors, wing.color, combine(param, COLOR))
        is BirdWing -> selectColor("Wing Color", combine(param, COLOR), options.birdColors, wing.color)
        is ButterflyWing -> selectColor("Wing Color", combine(param, COLOR), options.butterflyColors, wing.color)
    }
}

private fun HtmlBlockTag.selectWingType(
    wingOptions: WingOptions,
    currentType: WingType,
    param: String,
) {
    selectFromOneOf("Type", param, wingOptions.types, currentType)
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
        AppearanceType.HeadOnly.toString() -> HeadOnly(parseHead(parameters, config, character), height, skin)
        AppearanceType.Body.toString() -> HumanoidBody(
            parseBody(parameters, config),
            parseHead(parameters, config, character),
            height,
            skin,
            parseTails(parameters, config),
            parseWings(parameters, config),
        )

        else -> UndefinedAppearance
    }
}

private fun parseHeight(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
) = parseDistance(parameters, HEIGHT, siPrefix, config.heightDistribution.center)

private fun parseBody(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
): Body {
    if (parameters.contains(BODY_SHAPE)) {
        return Body(
            parse(parameters, BODY_SHAPE, BodyShape.Rectangle),
            parseFoot(parameters, config),
            parse(parameters, BODY_WIDTH, Size.Medium),
        )
    }

    return generateBody(config)
}

private fun parseFoot(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
) = when (parameters[FOOT]) {
    FootType.Normal.toString() -> NormalFoot
    FootType.Clawed.toString() -> {
        val options = config.appearanceOptions.foot
        ClawedFoot(
            options.clawNumber,
            parseAppearanceOption(parameters, combine(FOOT, CLAWS, SIZE), config, options.clawSizes),
            parseAppearanceColor(parameters, combine(FOOT, CLAWS), config, options.clawColors),
        )
    }

    else -> generateFoot(config)
}

private fun parseTails(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
) = when (parameters[combine(TAIL, LAYOUT)]) {
    TailsLayout.None.toString() -> NoTails
    TailsLayout.Simple.toString() -> {
        val options = config.appearanceOptions.tail
        val shape = parseAppearanceOption(parameters, combine(TAIL, SHAPE), config, options.simpleShapes)
        val shapeOptions = options.getFeatureColorOptions(shape)
        val featureColor = parseFeatureColor(parameters, config, shapeOptions, TAIL)

        SimpleTail(
            shape,
            parse(parameters, combine(TAIL, SIZE), Size.Medium),
            featureColor,
        )
    }

    else -> generateTails(config)
}

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
    val wingOptions = config.appearanceOptions.wing

    return when (parameters[combine(param, TYPE)]) {
        WingType.Bat.toString() -> BatWing(
            parseFeatureColor(parameters, config, wingOptions.batColors, combine(param, COLOR))
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
    colors: RarityMap<Color>,
) = parseAppearanceOption(parameters, combine(param, COLOR), config, colors)

inline fun <reified T : Enum<T>> parseAppearanceOption(
    parameters: Parameters,
    param: String,
    config: AppearanceGeneratorConfig,
    values: RarityMap<T>,
) = parse<T>(parameters, param) ?: config.generate(values)