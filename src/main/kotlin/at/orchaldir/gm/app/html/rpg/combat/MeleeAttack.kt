package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.EFFECT
import at.orchaldir.gm.app.PARRYING
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.MeleeAttack
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showMeleeAttack(
    call: ApplicationCall,
    state: State,
    attack: MeleeAttack,
    label: String,
) {
    showDetails(label, true) {
        fieldAttackEffect(call, state, attack.effect)
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
        editAttackEffect( state, attack.effect, combine(param, EFFECT))
        editParrying(attack.parrying, combine(param, PARRYING))
    }
}

// parse

fun parseMeleeAttack(
    parameters: Parameters,
    param: String,
) = MeleeAttack(
    parseAttackEffect(parameters, combine(param, EFFECT)),
    parseParrying(parameters, combine(param, PARRYING)),
)
