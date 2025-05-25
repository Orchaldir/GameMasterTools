package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.BOSS
import at.orchaldir.gm.app.SHAPE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.item.editColorSchemeItemPart
import at.orchaldir.gm.app.html.item.parseColorSchemeItemPart
import at.orchaldir.gm.app.html.item.showColorSchemeItemPart
import at.orchaldir.gm.app.html.math.parseComplexShape
import at.orchaldir.gm.app.html.math.selectComplexShape
import at.orchaldir.gm.app.html.math.showComplexShape
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.NoShieldBoss
import at.orchaldir.gm.core.model.item.equipment.style.ShieldBoss
import at.orchaldir.gm.core.model.item.equipment.style.ShieldBossType
import at.orchaldir.gm.core.model.item.equipment.style.SimpleShieldBoss
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showShieldBoss(
    call: ApplicationCall,
    state: State,
    boss: ShieldBoss,
) {
    showDetails("Shield Boss") {
        field("Type", boss.getType())

        when (boss) {
            NoShieldBoss -> doNothing()
            is SimpleShieldBoss -> {
                showComplexShape(boss.shape)
                showColorSchemeItemPart(call, state, boss.main)
            }
        }
    }
}

// edit

fun FORM.editShieldBoss(state: State, boss: ShieldBoss) {
    showDetails("Opening Style", true) {
        selectValue("Type", BOSS, ShieldBossType.entries, boss.getType())

        when (boss) {
            NoShieldBoss -> doNothing()
            is SimpleShieldBoss -> {
                selectComplexShape(boss.shape, combine(BOSS, SHAPE))
                editColorSchemeItemPart(state, boss.main, BOSS)
            }
        }
    }
}

// parse

fun parseShieldBoss(parameters: Parameters) = when (parse(parameters, BOSS, ShieldBossType.None)) {
    ShieldBossType.None -> NoShieldBoss
    ShieldBossType.Simple -> SimpleShieldBoss(
        parseComplexShape(parameters, combine(BOSS, SHAPE)),
        parseColorSchemeItemPart(parameters, BOSS),
    )
}
