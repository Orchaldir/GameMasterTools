package at.orchaldir.gm.app.html.character.statistic

import at.orchaldir.gm.app.COST
import at.orchaldir.gm.app.DAMAGE
import at.orchaldir.gm.app.DIE
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
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
    call: ApplicationCall,
    state: State,
    lookup: BaseDamageLookup,
) {
    showDetails("Base Damage Lookup") {
        when (lookup) {
            is BaseDamageDicePool -> fieldDiceType(lookup.dieType)
            is SimpleBaseDamageLookup -> {
                fieldDiceType(lookup.dieType)
                showLookup(call, state, lookup.lookup, "Lookup") { value ->
                    field("Die", value.dice)
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
    showDetails("Cost", true) {
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
            }
        }
    }
}

private fun DETAILS.selectDieType(
    dieType: DieType
                                  ) {
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
    BaseDamageLookupType.DicePool -> BaseDamageDicePool()
    BaseDamageLookupType.SimpleLookup -> SimpleBaseDamageLookup(
        Lookup(SimpleBaseDamageEntry(0, 0))
    )
}
