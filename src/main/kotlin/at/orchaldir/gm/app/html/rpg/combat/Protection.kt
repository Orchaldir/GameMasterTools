package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.PROTECTION
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.rpg.combat.DamageResistance
import at.orchaldir.gm.core.model.rpg.combat.Protection
import at.orchaldir.gm.core.model.rpg.combat.ProtectionType
import at.orchaldir.gm.core.model.rpg.combat.UndefinedProtection
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldProtection(
    protection: Protection,
) {
    field("Protection") {
        displayProtection(protection, true)
    }
}

fun HtmlBlockTag.displayProtection(
    protection: Protection,
    showUndefined: Boolean = false,
) {
    when (protection) {
        is DamageResistance -> +"${protection.amount} DR"
        UndefinedProtection -> if (showUndefined) {
            +"Undefined"
        }
    }
}

// edit

fun HtmlBlockTag.editProtection(
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
            is DamageResistance -> selectInt(
                "DR",
                protection.amount,
                1,
                100,
                1,
                combine(param, NUMBER),
            )
            UndefinedProtection -> doNothing()
        }
    }
}

// parse

fun parseProtection(
    parameters: Parameters,
    param: String = PROTECTION,
) = when (parse(parameters, combine(param, TYPE), ProtectionType.Undefined)) {
    ProtectionType.DamageResistance -> DamageResistance(
        parseInt(parameters, combine(param, NUMBER), 0)
    )
    ProtectionType.Undefined -> UndefinedProtection
}
