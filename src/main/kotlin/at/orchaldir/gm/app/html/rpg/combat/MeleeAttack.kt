package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.EFFECT
import at.orchaldir.gm.app.PARRYING
import at.orchaldir.gm.app.REACH
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.html.tdLink
import at.orchaldir.gm.app.html.tdSkipZero
import at.orchaldir.gm.app.html.thMultiLines
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.MeleeAttack
import at.orchaldir.gm.core.selector.economy.countJobs
import at.orchaldir.gm.core.selector.economy.money.display
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.application.call
import kotlinx.html.HtmlBlockTag
import kotlinx.html.caption
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr

// show

fun HtmlBlockTag.showMeleeAttackTable(
    call: ApplicationCall,
    state: State,
    attacks: List<MeleeAttack>,
) {
    table {
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
