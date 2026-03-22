package at.orchaldir.gm.app.html.rpg.dice

import at.orchaldir.gm.app.DIE
import at.orchaldir.gm.app.MODIFIER
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.rpg.selectFromRange
import at.orchaldir.gm.core.model.rpg.dice.*
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show


// edit

fun HtmlBlockTag.editRandomNumber(
    range: ModifiedDiceRange,
    number: RandomNumber,
    param: String,
    label: String,
) {
    showDetails(label, true) {
        editRandomNumber(range, number, param)
    }
}

fun HtmlBlockTag.editRandomNumber(
    range: ModifiedDiceRange,
    number: RandomNumber,
    param: String,
) {
    selectValue(
        "Type",
        combine(param, TYPE),
        RandomNumberType.entries,
        number.getType(),
    )

    when (number) {
        is NotRandomNumber -> selectDiceModifier(range, param, number.number)
        is StandardDice -> {
            selectDiceNumber(range, param, number.dice)
            selectDiceModifier(range, param, number.modifier)
        }

        is Dice -> {
            selectDiceNumber(range, param, number.dice)
            selectDieType(param, number.type)
            selectDiceModifier(range, param, number.modifier)
        }

        is MixedDice -> {
            editMap("Dice", param, number.dice, 1, DieType.entries.size) { _, diceParam, type, dice ->
                selectDiceNumber(range, diceParam, dice)
                selectDieType(diceParam, type)
            }
            selectDiceModifier(range, param, number.modifier)
        }
    }
}

private fun HtmlBlockTag.selectDieType(
    param: String,
    type: DieType,
) {
    selectValue(
        "Die Type",
        combine(param, DIE, TYPE),
        DieType.entries,
        type,
    )
}

fun HtmlBlockTag.selectDiceNumber(
    range: ModifiedDiceRange,
    param: String,
    dice: Int,
) {
    selectFromRange(
        "Dice",
        range.dice,
        dice,
        combine(param, DIE),
    )
}

fun HtmlBlockTag.selectDiceModifier(
    range: ModifiedDiceRange,
    param: String,
    modifier: Int,
) {
    selectFromRange(
        "Modifier",
        range.modifier,
        modifier,
        combine(param, MODIFIER),
    )
}

// parse

fun parseRandomNumber(
    parameters: Parameters,
    param: String,
) = when (parse(parameters, combine(param, TYPE), RandomNumberType.NotRandom)) {
    RandomNumberType.NotRandom -> NotRandomNumber(
        parseDiceModifier(parameters, param),
    )

    RandomNumberType.StandardDice -> parseStandardDice(parameters, param)
    RandomNumberType.Dice -> Dice(
        parseDice(parameters, param),
        parseDieType(parameters, param),
        parseDiceModifier(parameters, param),
    )

    RandomNumberType.MixedDice -> MixedDice(
        parseMap(
            parameters,
            param,
            DieType.entries,
            { _, keyParam -> parseOptionalDieType(parameters, keyParam) },
            { _, _, valueParam -> parseDice(parameters, valueParam) },
        ),
        parseDiceModifier(parameters, param),
    )
}

fun parseStandardDice(
    parameters: Parameters,
    param: String,
) = StandardDice(
    parseDice(parameters, param),
    parseDiceModifier(parameters, param),
)

private fun parseDieType(
    parameters: Parameters,
    param: String,
) = parse(parameters, combine(param, DIE, TYPE), DieType.D6)

private fun parseOptionalDieType(
    parameters: Parameters,
    param: String,
) = parse<DieType>(parameters, combine(param, DIE, TYPE))

private fun parseDice(parameters: Parameters, param: String) =
    parseInt(parameters, combine(param, DIE), 1)

private fun parseDiceModifier(parameters: Parameters, param: String) =
    parseInt(parameters, combine(param, MODIFIER), 0)
