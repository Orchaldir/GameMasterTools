package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.EFFECT
import at.orchaldir.gm.app.PARRYING
import at.orchaldir.gm.app.REACH
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.showDetails
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
        caption { +"Melee Attacks" }
        tr {
            sharedHeaders()
        }
        attacks.forEach { attack ->
            tr {
                sharedColumns(call, state, attack)
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
        caption { +"Melee Attacks" }
        tr {
            th { +"Weapon" }
            sharedHeaders()
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
                    sharedColumns(call, state, attack)
                }
            }

        }
    }
}

private fun TR.sharedHeaders() {
    th { +"Damage" }
    th { +"Reach" }
    th { +"Parrying" }
}

private fun TR.sharedColumns(
    call: ApplicationCall,
    state: State,
    attack: MeleeAttack,
) {
    td { displayAttackEffect(call, state, attack.effect) }
    td { displayReach(attack.reach) }
    td { displayParrying(attack.parrying) }
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
        editAttackEffect(state, attack.effect, param)
        editReach(attack.reach, param)
        editParrying(attack.parrying, param)
    }
}

// parse

fun parseMeleeAttack(
    parameters: Parameters,
    param: String,
) = MeleeAttack(
    parseAttackEffect(parameters, param),
    parseReach(parameters, param),
    parseParrying(parameters, param),
)
