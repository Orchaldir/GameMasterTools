package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.DAMAGE
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.PROTECTION
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.core.selector.util.sortDamageTypes
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldProtection(
    call: ApplicationCall,
    state: State,
    protection: Protection,
) {
    field("Protection") {
        displayProtection(call, state, protection, true)
    }
}

fun HtmlBlockTag.displayProtection(
    call: ApplicationCall,
    state: State,
    protection: Protection,
    showUndefined: Boolean = false,
) {
    when (protection) {
        is DamageResistance -> +"${protection.amount} DR"
        is DamageResistances -> {
            +protection.amount.toString()
            protection.damageTypes.forEach { (id, dr) ->
                val type = state.getDamageTypeStorage().getOrThrow(id)
                +"/$dr "
                link(call, type, type.short())
            }
            +" DR"
        }
        is DefenseBonus -> +"${protection.bonus} DB"

        UndefinedProtection -> if (showUndefined) {
            +"Undefined"
        }
    }
}

// edit

fun HtmlBlockTag.editProtection(
    call: ApplicationCall,
    state: State,
    protection: Protection,
    param: String = PROTECTION,
) {
    showDetails("Protection", true) {
        selectValue(
            "Type",
            combine(param, TYPE),
            ProtectionType.entries,
            protection.getType(),
        )

        when (protection) {
            is DamageResistance -> selectDR(param, 1, protection.amount)
            is DamageResistances -> {
                var damageTypes = state.sortDamageTypes()

                selectDR(param, 0, protection.amount)
                editMap(
                    "DR for Damage Types",
                    combine(param, DAMAGE),
                    protection.damageTypes,
                    1,
                    damageTypes.size,
                ) { _, damageTypeParam, damageType, dr ->
                    selectElement(
                        state,
                        combine(damageTypeParam, TYPE),
                        damageTypes,
                        damageType,
                    )
                    selectDR(damageTypeParam, 0, dr)

                    damageTypes = damageTypes.filter { it.id != damageType }
                }
            }
            is DefenseBonus -> selectDR(param, 1, protection.bonus)

            UndefinedProtection -> doNothing()
        }
    }
}

private fun HtmlBlockTag.selectDR(
    param: String,
    min: Int,
    dr: Int,
) {
    selectInt(
        "DR",
        dr,
        min,
        100,
        1,
        combine(param, NUMBER),
    )
}

// parse

fun parseProtection(
    parameters: Parameters,
    param: String = PROTECTION,
) = when (parse(parameters, combine(param, TYPE), ProtectionType.Undefined)) {
    ProtectionType.DamageResistance -> DamageResistance(
        parseDR(parameters, param),
    )

    ProtectionType.DamageResistances -> DamageResistances(
        parseDR(parameters, param),
        parseMap(
            parameters,
            combine(param, DAMAGE),
            { _, keyParam -> parseDamageTypeId(parameters, combine(keyParam, TYPE)) },
            { _, _, valueParam -> parseDR(parameters, valueParam) },
        ),
    )

    ProtectionType.DefenseBonus -> DefenseBonus(
        parseDR(parameters, param),
    )

    ProtectionType.Undefined -> UndefinedProtection
}

private fun parseDR(parameters: Parameters, param: String) = parseInt(
    parameters,
    combine(param, NUMBER),
    1,
)
