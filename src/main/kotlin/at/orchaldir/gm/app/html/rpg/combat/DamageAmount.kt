package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.BASE
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.rpg.parseDiceModifier
import at.orchaldir.gm.app.html.rpg.parseSimpleModifiedDice
import at.orchaldir.gm.app.html.rpg.selectDiceModifier
import at.orchaldir.gm.app.html.rpg.selectDiceNumber
import at.orchaldir.gm.app.html.rpg.statistic.parseStatisticId
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.DamageAmount
import at.orchaldir.gm.core.model.rpg.combat.DamageAmountType
import at.orchaldir.gm.core.model.rpg.combat.ModifiedBaseDamage
import at.orchaldir.gm.core.model.rpg.combat.SimpleRandomDamage
import at.orchaldir.gm.core.selector.rpg.getBaseDamageValues
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlin.math.absoluteValue

// show

fun HtmlBlockTag.displayDamageAmount(
    call: ApplicationCall,
    state: State,
    amount: DamageAmount,
) {
    when (amount) {
        is ModifiedBaseDamage -> {
            val base = state.getStatisticStorage().getOrThrow(amount.base)
            link(call, base, base.short?.text ?: base.name())

            if (amount.modifier > 0) {
                +"+${amount.modifier}"
            } else if (amount.modifier < 0) {
                +"-${amount.modifier.absoluteValue}"
            }
        }

        is SimpleRandomDamage -> +amount.amount.display()
    }
}

// edit

fun HtmlBlockTag.editDamageAmount(
    state: State,
    amount: DamageAmount,
    param: String,
) {
    showDetails("Damage Amount", true) {
        selectValue(
            "Type",
            combine(param, TYPE),
            DamageAmountType.entries,
            amount.getType(),
        )

        when (amount) {
            is ModifiedBaseDamage -> {
                selectElement(
                    state,
                    "Base Damage",
                    combine(param, BASE),
                    state.getBaseDamageValues(),
                    amount.base,
                )
                selectDiceModifier(param, amount.modifier, state.data.rpg.damageRange)
            }

            is SimpleRandomDamage -> {
                selectDiceNumber(amount.amount, param, state.data.rpg.damageRange)
                selectDiceModifier(amount.amount, param, state.data.rpg.damageRange)
            }
        }
    }
}

// parse

fun parseDamageAmount(
    parameters: Parameters,
    param: String,
) = when (parse(parameters, combine(param, TYPE), DamageAmountType.SimpleRandom)) {
    DamageAmountType.ModifiedBase -> ModifiedBaseDamage(
        parseStatisticId(parameters, combine(param, BASE)),
        parseDiceModifier(parameters, param),
    )

    DamageAmountType.SimpleRandom -> SimpleRandomDamage(
        parseSimpleModifiedDice(parameters, param),
    )
}