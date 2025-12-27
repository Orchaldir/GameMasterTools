package at.orchaldir.gm.app.html.race

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.appearance.fieldHairColor
import at.orchaldir.gm.app.html.character.appearance.selectHairColor
import at.orchaldir.gm.app.html.character.appearance.showHairColor
import at.orchaldir.gm.app.html.rpg.statblock.editStatblock
import at.orchaldir.gm.app.html.rpg.statblock.parseStatblock
import at.orchaldir.gm.app.html.rpg.statblock.showStatblock
import at.orchaldir.gm.app.html.util.math.fieldFactor
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.html.util.math.selectPercentage
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.beard.BeardType
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.race.aging.*
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.Factor
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun HtmlBlockTag.showLifeStages(
    call: ApplicationCall,
    state: State,
    lifeStages: LifeStages,
) {
    h2 { +"Life Stages" }

    when (lifeStages) {
        is ImmutableLifeStage -> {
            showAppearance(call, state, lifeStages.appearance)
            showStatblock(call, state, lifeStages.statblock)
        }

        is DefaultAging -> {
            showAppearance(call, state, lifeStages.appearance)
            fieldList("Max Ages", lifeStages.maxAges.withIndex().toList()) { indexed ->
                val maxAge = indexed.value
                val stage = DefaultLifeStages.entries[indexed.index].name
                field(stage, "$maxAge years")
            }
            fieldHairColor(lifeStages.oldAgeHairColor, "Old Age Hair Color")
            fieldHairColor(lifeStages.venerableAgeHairColor, "Venerable Hair Color")
            showStatblock(call, state, lifeStages.statblock)
        }

        is SimpleAging -> {
            showAppearance(call, state, lifeStages.appearance)
            details {
                showList(lifeStages.lifeStages, HtmlBlockTag::showLifeStage)
            }
            showStatblock(call, state, lifeStages.statblock)
        }
    }
}

private fun HtmlBlockTag.showLifeStage(stage: LifeStage) {
    +stage.name.text
    ul {
        li {
            showMaxAge(stage.maxAge)
        }
        li {
            fieldFactor("Relative Size", stage.relativeSize)
        }
        if (stage.hasBeard) {
            li {
                p {
                    b { +"Has Beard" }
                }
            }
        }
        if (stage.hairColor != NoHairColor) {
            li {
                showHairColor(stage.hairColor)
            }
        }
    }
}

private fun HtmlBlockTag.showAppearance(
    call: ApplicationCall,
    state: State,
    id: RaceAppearanceId,
) {
    fieldLink("Appearance", call, state, id)
}

private fun HtmlBlockTag.showMaxAge(maxAge: Int) {
    field("Max Age", maxAge)
}


// edit

fun HtmlBlockTag.editLifeStages(
    call: ApplicationCall,
    state: State,
    lifeStages: LifeStages,
) {
    val raceAppearance = state.getRaceAppearanceStorage().getOrThrow(lifeStages.getRaceAppearance())
    val canHaveBeard = raceAppearance.hair.beardTypes.isAvailable(BeardType.Normal)

    h2 { +"Life Stages" }

    selectValue("Type", combine(LIFE_STAGE, TYPE), LifeStagesType.entries, lifeStages.getType())

    when (lifeStages) {
        is ImmutableLifeStage -> {
            selectAppearance(state, lifeStages.appearance, 0)
            editStatblock(call, state, lifeStages.statblock)
        }

        is DefaultAging -> {
            selectAppearance(state, lifeStages.appearance, 0)
            var minMaxAge = 1
            showListWithIndex(lifeStages.getAllLifeStages()) { index, stage ->
                val nextMaxAge = lifeStages.maxAges.getOrNull(index + 1) ?: 10001
                selectMaxAge(stage.name.text, index, stage.maxAge, minMaxAge, nextMaxAge - 1)
                minMaxAge = stage.maxAge + 1
            }
            selectHairColor("Old Age Hair Color", 6, lifeStages.oldAgeHairColor)
            selectHairColor("Venerable Hair Color", 7, lifeStages.venerableAgeHairColor)
            editStatblock(call, state, lifeStages.statblock)
        }

        is SimpleAging -> {
            selectAppearance(state, lifeStages.appearance, 0)
            selectNumberOfLifeStages(lifeStages.lifeStages.size)
            var minMaxAge = 1
            showListWithIndex(lifeStages.lifeStages) { index, stage ->
                selectName(stage.name, combine(LIFE_STAGE, NAME, index))
                ul {
                    li {
                        val nextMaxAge = lifeStages.lifeStages.getOrNull(index + 1)?.maxAge ?: 10001
                        selectMaxAge("Max Age", index, stage.maxAge, minMaxAge, nextMaxAge - 1)
                    }
                    li {
                        selectRelativeSize(stage.relativeSize, index)
                    }
                    li {
                        selectBool(
                            "Has Beard",
                            stage.hasBeard && canHaveBeard,
                            combine(LIFE_STAGE, BEARD, index),
                            !canHaveBeard
                        )
                    }
                    li {
                        selectHairColor("Hair Color", index, stage.hairColor)
                    }
                }
                minMaxAge = stage.maxAge + 1
            }
            editStatblock(call, state, lifeStages.statblock)
        }
    }
}

private fun HtmlBlockTag.selectHairColor(label: String, index: Int, color: HairColor) {
    selectHairColor(
        OneOf(setOf(HairColorType.None, HairColorType.Exotic)),
        OneOf(NormalHairColorEnum.entries),
        OneOf(Color.entries),
        color,
        combine(LIFE_STAGE, HAIR, index),
        label,
    )
}

private fun HtmlBlockTag.selectNumberOfLifeStages(number: Int) {
    selectInt("Life Stages", number, 2, 100, 1, LIFE_STAGE)
}

private fun HtmlBlockTag.selectMaxAge(
    label: String,
    index: Int,
    age: Int,
    minAge: Int,
    maxAge: Int,
) {
    selectInt(label, age, minAge, maxAge, 1, combine(LIFE_STAGE, AGE, index))
}

private fun LI.selectRelativeSize(
    size: Factor,
    index: Int,
) {
    selectPercentage("Relative Size", combine(LIFE_STAGE, SIZE, index), size, 1, 100, 1)
}

private fun HtmlBlockTag.selectAppearance(
    state: State,
    raceAppearanceId: RaceAppearanceId,
    index: Int,
) {
    selectElement(
        state,
        "Appearance",
        combine(RACE, APPEARANCE, index),
        state.getRaceAppearanceStorage().getAll(),
        raceAppearanceId,
    )
}

// parse

fun parseLifeStages(
    state: State,
    parameters: Parameters,
) = when (parse(parameters, combine(LIFE_STAGE, TYPE), LifeStagesType.DefaultAging)) {
    LifeStagesType.ImmutableLifeStage -> ImmutableLifeStage(
        parseAppearanceId(parameters, 0),
        parseStatblock(state, parameters),
    )

    LifeStagesType.DefaultAging -> DefaultAging(
        parseAppearanceId(parameters, 0),
        parseMaxAges(parameters),
        parseHairColor(parameters, 6),
        parseHairColor(parameters, 7),
        parseStatblock(state, parameters),
    )

    LifeStagesType.SimpleAging -> SimpleAging(
        parseAppearanceId(parameters, 0),
        parseSimpleLifeStages(parameters),
        parseStatblock(state, parameters),
    )
}

private fun parseMaxAges(parameters: Parameters): List<Int> = (0..<DefaultLifeStages.entries.size)
    .map { parseMaxAge(parameters, it, DEFAULT_MAX_AGES[it]) }

private fun parseSimpleLifeStages(parameters: Parameters): List<LifeStage> {
    val count = parseInt(parameters, LIFE_STAGE, 2)

    return (0..<count)
        .map { parseSimpleLifeStage(parameters, it) }
}

private fun parseSimpleLifeStage(parameters: Parameters, index: Int) = LifeStage(
    parseName(parameters, combine(LIFE_STAGE, NAME, index), "${index + 1}.Life Stage"),
    parseMaxAge(parameters, index),
    parseFactor(parameters, combine(LIFE_STAGE, SIZE, index)),
    parseBool(parameters, combine(LIFE_STAGE, BEARD, index)),
    parseHairColor(parameters, index),
)

private fun parseMaxAge(parameters: Parameters, index: Int, default: Int = 2) =
    parseInt(parameters, combine(LIFE_STAGE, AGE, index), default)

private fun parseHairColor(parameters: Parameters, index: Int, default: Color? = null): HairColor {
    val colorParam = combine(combine(LIFE_STAGE, HAIR, index), COLOR)

    return when (parse(parameters, combine(colorParam, TYPE), HairColorType.Normal)) {
        HairColorType.None -> NoHairColor
        HairColorType.Normal -> NormalHairColor(
            parse(parameters, colorParam, NormalHairColorEnum.MediumBrown),
        )

        HairColorType.Exotic -> ExoticHairColor(
            parse(parameters, combine(colorParam, EXOTIC), Color.Blue),
        )
    }
}

private fun parseAppearanceId(parameters: Parameters, index: Int) =
    parseRaceAppearanceId(parameters, combine(RACE, APPEARANCE, index))
