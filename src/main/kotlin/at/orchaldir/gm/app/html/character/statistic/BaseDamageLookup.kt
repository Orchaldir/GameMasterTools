package at.orchaldir.gm.app.html.character.statistic

import at.orchaldir.gm.app.COST
import at.orchaldir.gm.app.DAMAGE
import at.orchaldir.gm.app.DIE
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.parseLookup
import at.orchaldir.gm.app.html.util.selectLookup
import at.orchaldir.gm.app.html.util.showLookup
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.statistic.*
import at.orchaldir.gm.core.model.util.Lookup
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.ApplicationCall
import kotlinx.html.DETAILS
import kotlinx.html.FORM
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
                showLookup(lookup.lookup, "Lookup") { value ->
                    field("Dice", value.dice)
                    field("Modifier", value.modifier)
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
                selectLookup(
                    DAMAGE,
                    lookup.lookup,
                    "Lookup",
                    0,
                    100,
                ) { entryParam, entry ->
                    selectInt(
                        "Dice",
                        entry.dice,
                        1,
                        100,
                        1,
                        combine(entryParam, DIE),
                    )
                    selectInt(
                        "Modifier",
                        entry.modifier,
                        -10,
                        +10,
                        1,
                        combine(entryParam, NUMBER),
                    )
                }
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
