package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.EFFECT
import at.orchaldir.gm.app.PARRYING
import at.orchaldir.gm.app.REACH
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.rpg.combat.MeleeAttack
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun HtmlBlockTag.showMeleeAttackTable(
    call: ApplicationCall,
    state: State,
    attacks: List<MeleeAttack>,
) {
    table {
        caption { +"Attacks" }
        tr {
            th { +"Damage" }
            th { +"Reach" }
            th { +"Parrying" }
        }
        attacks.forEach { attack ->
            tr {
                td { displayAttackEffect(call, state, attack.effect) }
                td { displayReach(attack.reach) }
                td { displayParrying(attack.parrying) }
            }
        }
    }
}

fun HtmlBlockTag.showMeleeAttackTable(
    call: ApplicationCall,
    state: State,
    attackMap: Map<Equipment, List<MeleeAttack>>,
) {
    if (attackMap.isEmpty()) {
        return
    }

    table {
        caption { +"Attacks" }
        tr {
            th { +"Weapon" }
            th { +"Damage" }
            th { +"Reach" }
            th { +"Parrying" }
        }
        attackMap.forEach { (weapon, attacks) ->
            var isFirst = true

            attacks.forEach { attack ->
                tr {
                    if (isFirst) {
                        td {
                            rowSpan = "2"
                            link(call, state, weapon)
                        }
                        isFirst = false
                    }
                    td { displayAttackEffect(call, state, attack.effect) }
                    td { displayReach(attack.reach) }
                    td { displayParrying(attack.parrying) }
                }
            }

        }
    }
}

fun HtmlBlockTag.showMeleeAttacks(
    call: ApplicationCall,
    state: State,
    attacks: List<MeleeAttack>,
) {
    showDetails("Attacks", true) {
        attacks.withIndex().forEach { (index, attack) ->
            showMeleeAttack(call, state, attack, "${index + 1}.Attack")
        }
    }
}

fun HtmlBlockTag.showMeleeAttack(
    call: ApplicationCall,
    state: State,
    attack: MeleeAttack,
    label: String,
) {
    showDetails(label, true) {
        fieldAttackEffect(call, state, attack.effect)
        fieldReach(attack.reach)
        fieldParrying(attack.parrying)
    }
}

// edit

fun HtmlBlockTag.editMeleeAttack(
    state: State,
    attack: MeleeAttack,
    label: String,
    param: String,
) {
    showDetails(label, true) {
        editAttackEffect(state, attack.effect, combine(param, EFFECT))
        editReach(attack.reach, combine(param, REACH))
        editParrying(attack.parrying, combine(param, PARRYING))
    }
}

// parse

fun parseMeleeAttack(
    parameters: Parameters,
    param: String,
) = MeleeAttack(
    parseAttackEffect(parameters, combine(param, EFFECT)),
    parseReach(parameters, combine(param, REACH)),
    parseParrying(parameters, combine(param, PARRYING)),
)
