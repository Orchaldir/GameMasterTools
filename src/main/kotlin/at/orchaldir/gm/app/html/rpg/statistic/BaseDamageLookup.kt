package at.orchaldir.gm.app.html.rpg.statistic

import at.orchaldir.gm.app.DAMAGE
import at.orchaldir.gm.app.DIE
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.rpg.dice.parseStandardDice
import at.orchaldir.gm.app.html.rpg.dice.selectDiceModifier
import at.orchaldir.gm.app.html.rpg.dice.selectDiceNumber
import at.orchaldir.gm.app.html.util.editLookupTable
import at.orchaldir.gm.app.html.util.parseLookup
import at.orchaldir.gm.app.html.util.showLookupTable
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.dice.DieType
import at.orchaldir.gm.core.model.rpg.statistic.BaseDamageDicePool
import at.orchaldir.gm.core.model.rpg.statistic.BaseDamageLookup
import at.orchaldir.gm.core.model.rpg.statistic.BaseDamageLookupType
import at.orchaldir.gm.core.model.rpg.statistic.SimpleBaseDamageLookup
import io.ktor.http.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showBaseDamageLookup(
    lookup: BaseDamageLookup,
) {
    showDetails("Base Damage Lookup", true) {
        field("Type", lookup.getType())

        when (lookup) {
            is BaseDamageDicePool -> fieldDiceType(lookup.dieType)
            is SimpleBaseDamageLookup -> {
                fieldDiceType(lookup.dieType)

                showLookupTable(
                    lookup.lookup,
                    Pair("Damage") { entry ->
                        +entry.display()
                    },
                )
            }
        }
    }
}

private fun DETAILS.fieldDiceType(dieType: DieType) {
    field("Die Type", dieType)
}


// edit

fun HtmlBlockTag.editBaseDamageLookup(
    state: State,
    lookup: BaseDamageLookup,
) {
    showDetails("Base Damage Lookup", true) {
        selectValue(
            "Type",
            combine(DAMAGE, TYPE),
            BaseDamageLookupType.entries,
            lookup.getType(),
        )

        when (lookup) {
            is BaseDamageDicePool -> selectDieType(lookup.dieType)
            is SimpleBaseDamageLookup -> {
                selectDieType(lookup.dieType)
                editLookupTable(
                    DAMAGE,
                    lookup.lookup,
                    2,
                    100,
                    1,
                    listOf(
                        Pair("Dice") { entryParam, entry ->
                            selectDiceNumber(state.config.rpg.damage, entryParam, entry.dice)
                        },
                        Pair("Modifier") { entryParam, entry ->
                            selectDiceModifier(state.config.rpg.damage, entryParam, entry.modifier)
                        },
                    ),
                )
            }
        }
    }
}

private fun DETAILS.selectDieType(dieType: DieType) {
    selectValue(
        "Die Type",
        combine(DAMAGE, DIE),
        DieType.entries,
        dieType,
    )
}

// parse

fun parseBaseDamageLookup(
    parameters: Parameters,
) = when (parse(parameters, combine(DAMAGE, TYPE), BaseDamageLookupType.DicePool)) {
    BaseDamageLookupType.DicePool -> BaseDamageDicePool(
        parse(parameters, DIE, DieType.D6),
    )

    BaseDamageLookupType.SimpleLookup -> SimpleBaseDamageLookup(
        parseLookup(parameters, DAMAGE, 1) { entryParam ->
            parseStandardDice(parameters, entryParam)
        },
        parse(parameters, DIE, DieType.D6),
    )
}
