package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.DAMAGE
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.PROTECTION
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.core.selector.util.sortDamageTypes
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun HtmlBlockTag.showProtectionTable(
    call: ApplicationCall,
    state: State,
    protectionMap: Map<Equipment, Protection>,
) {
    if (protectionMap.isEmpty()) {
        return
    }

    table {
        caption { +"Protection Table" }
        tr {
            th { +"Equipment" }
            th { +"Protection" }
        }
        protectionMap.forEach { (equipment, protection) ->
            tr {
                td { link(call, state, equipment) }
                td { displayProtection(call, state, protection) }
            }
        }
    }
}

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
    val data = state.data.rpg.equipment

    showDetails("Protection", true) {
        selectValue(
            "Type",
            combine(param, TYPE),
            ProtectionType.entries,
            protection.getType(),
        )

        when (protection) {
            is DamageResistance -> selectDR(protection.amount, param, 1, data.maxDamageResistance)
            is DamageResistances -> {
                var damageTypes = state.sortDamageTypes()

                selectDR(protection.amount, param, 0, data.maxDamageResistance)
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
                    selectDR(protection.amount, damageTypeParam, 0, data.maxDamageResistance)

                    damageTypes = damageTypes.filter { it.id != damageType }
                }
            }

            is DefenseBonus -> selectDR(protection.bonus, param, 1, data.maxDefenseBonus)

            UndefinedProtection -> doNothing()
        }
    }
}

private fun HtmlBlockTag.selectDR(
    dr: Int,
    param: String,
    min: Int,
    max: Int,
) {
    selectInt(
        "DR",
        dr,
        min,
        max,
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
