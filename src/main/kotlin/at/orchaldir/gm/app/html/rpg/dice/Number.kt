package at.orchaldir.gm.app.html.rpg.dice

import at.orchaldir.gm.app.DIE
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.rpg.selectFromRange
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.dice.*
import at.orchaldir.gm.core.model.rpg.dice.Number
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show


// edit

fun HtmlBlockTag.editNumber(
    range: ModifiedDiceRange,
    number: Number,
    param: String,
) {
    selectValue(
        "Type",
        combine(param, TYPE),
        NumberType.entries,
        number.getType(),
    )

    when (number) {
        is FixedNumber -> selectDiceModifier(range, param, number.number)
        is StandardDice -> editDice(range, param, number.dice, number.modifier)
        is Dice -> {
            editDice(range, param, number.dice, number.modifier)
            selectDieType(param, number.type)
        }
        is MixedDice -> {
            editMap("Dice", param, number.dice, 1, range.dice.max) { _, diceParam, type, dice ->
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

fun HtmlBlockTag.editDice(
    range: ModifiedDiceRange,
    param: String,
    dice: Int,
    modifier: Int,
) {
    selectDiceNumber(range, param, dice)
    selectDiceModifier(range, param, modifier)
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
        combine(param, NUMBER),
    )
}

// parse

fun parseNumber(
    parameters: Parameters,
    param: String,
) = when (parse(parameters, combine(param, TYPE), NumberType.Fixed)) {
    NumberType.Fixed -> FixedNumber(
        parseDiceModifier(parameters, param),
    )
    NumberType.StandardDice -> parseStandardDice(parameters, param)
    NumberType.Dice -> Dice(
        parseDice(parameters, param),
        parseDieType(parameters, param),
        parseDiceModifier(parameters, param),
    )
    NumberType.MixedDice -> MixedDice(
        parseMap(
            parameters,
            param,
            { _, keyParam -> parseDieType(parameters, keyParam) },
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

private fun parseDice(parameters: Parameters, param: String) =
    parseInt(parameters, combine(param, DIE), 1)

private fun parseDiceModifier(parameters: Parameters, param: String) =
    parseInt(parameters, combine(param, NUMBER), 0)
