package at.orchaldir.gm.app.html.rpg.statistic

import at.orchaldir.gm.app.DAMAGE
import at.orchaldir.gm.app.DIE
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.editLookupTable
import at.orchaldir.gm.app.html.util.parseLookup
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.rpg.statistic.BaseDamageDicePool
import at.orchaldir.gm.core.model.rpg.statistic.BaseDamageLookup
import at.orchaldir.gm.core.model.rpg.statistic.BaseDamageLookupType
import at.orchaldir.gm.core.model.rpg.DieType
import at.orchaldir.gm.core.model.rpg.statistic.SimpleBaseDamageEntry
import at.orchaldir.gm.core.model.rpg.statistic.SimpleBaseDamageLookup
import io.ktor.http.*
import kotlinx.html.*

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

                table {
                    tr {
                        th { +"Until" }
                        th { +"Damage" }
                    }
                    lookup.lookup.previousEntries.forEach { entry ->
                        tr {
                            tdInt(entry.until)
                            tdString(entry.value.display())
                        }
                    }
                    tr {
                        tdString(">")
                        tdString(lookup.lookup.current.display())
                    }
                }
            }
        }
    }
}

private fun DETAILS.fieldDiceType(dieType: DieType) {
    field("Die Type", dieType)
}


// edit

fun FORM.editBaseDamageLookup(
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
                    0,
                    100,
                    listOf(
                        Pair("Dice") { entryParam, entry ->
                            selectInt(
                                entry.dice,
                                1,
                                100,
                                1,
                                combine(entryParam, DIE),
                            )
                        },
                        Pair("Modifier") { entryParam, entry ->
                            selectInt(
                                entry.modifier,
                                -10,
                                +10,
                                1,
                                combine(entryParam, NUMBER),
                            )
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
            SimpleBaseDamageEntry(
                parseInt(parameters, combine(entryParam, DIE), 1),
                parseInt(parameters, combine(entryParam, NUMBER), 0),
            )
        },
        parse(parameters, DIE, DieType.D6),
    )
}
