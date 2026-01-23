package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.rpg.combat.RangedAttack
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun HtmlBlockTag.showRangedAttackTable(
    call: ApplicationCall,
    state: State,
    attacks: List<RangedAttack>,
) {
    table {
        caption { +"Ranged Attacks" }
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

fun HtmlBlockTag.showRangedAttackTable(
    call: ApplicationCall,
    state: State,
    attackMap: Map<Equipment, List<RangedAttack>>,
) {
    if (attackMap.isEmpty()) {
        return
    }

    table {
        caption { +"Ranged Attacks" }
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
    th { +"Accuracy" }
    th { +"Range" }
    th { +"Shots" }
}

private fun TR.sharedColumns(
    call: ApplicationCall,
    state: State,
    attack: RangedAttack,
) {
    td { displayAttackEffect(call, state, attack.effect) }
    td { displayAccuracy(attack.accuracy) }
    td { displayRange(call, state, attack.range) }
    td { displayShots(attack.shots) }
}

fun HtmlBlockTag.showRangedAttacks(
    call: ApplicationCall,
    state: State,
    attacks: List<RangedAttack>,
) {
    showDetails("Attacks", true) {
        attacks.withIndex().forEach { (index, attack) ->
            showRangedAttack(call, state, attack, "${index + 1}.Attack")
        }
    }
}

fun HtmlBlockTag.showRangedAttack(
    call: ApplicationCall,
    state: State,
    attack: RangedAttack,
    label: String,
) {
    showDetails(label, true) {
        fieldAttackEffect(call, state, attack.effect)
        fieldAccuracy(attack.accuracy)
        fieldRange(call, state, attack.range)
        showShotsDetails(call, state, attack.shots)
    }
}

// edit

fun HtmlBlockTag.editRangedAttack(
    state: State,
    attack: RangedAttack,
    label: String,
    param: String,
) {
    showDetails(label, true) {
        editAttackEffect(state, attack.effect, param)
        editAccuracy(attack.accuracy, param)
        editRange(state, attack.range, param)
        editShots(state, attack.shots, param)
    }
}

// parse

fun parseRangedAttack(
    parameters: Parameters,
    param: String,
) = RangedAttack(
    parseAccuracy(parameters, param),
    parseAttackEffect(parameters, param),
    parseRange(parameters, param),
    parseShots(parameters, param),
)
