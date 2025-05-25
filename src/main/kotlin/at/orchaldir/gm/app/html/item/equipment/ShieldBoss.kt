package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.BORDER
import at.orchaldir.gm.app.BOSS
import at.orchaldir.gm.app.SHAPE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.item.editColorSchemeItemPart
import at.orchaldir.gm.app.html.item.parseColorSchemeItemPart
import at.orchaldir.gm.app.html.item.showColorSchemeItemPart
import at.orchaldir.gm.app.html.math.parseCircularShape
import at.orchaldir.gm.app.html.math.selectCircularShape
import at.orchaldir.gm.app.html.math.showCircularShape
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.*
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
                showCircularShape(boss.shape)
                showColorSchemeItemPart(call, state, boss.part)
            }

            is ShieldBossWithBorder -> {
                showCircularShape(boss.shape)
                showCircularShape(boss.border, "Border Shape")
                showColorSchemeItemPart(call, state, boss.part, "Main")
                showColorSchemeItemPart(call, state, boss.borderPart, "Border")
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
                selectCircularShape(boss.shape, combine(BOSS, SHAPE))
                editColorSchemeItemPart(state, boss.part, BOSS)
            }

            is ShieldBossWithBorder -> {
                selectCircularShape(boss.shape, combine(BOSS, SHAPE))
                selectCircularShape(boss.shape, combine(BOSS, BORDER), "Border Shape")
                editColorSchemeItemPart(state, boss.part, BOSS, "Main")
                editColorSchemeItemPart(state, boss.borderPart, combine(BOSS, BORDER), "Border")
            }
        }
    }
}

// parse

fun parseShieldBoss(parameters: Parameters) = when (parse(parameters, BOSS, ShieldBossType.None)) {
    ShieldBossType.None -> NoShieldBoss
    ShieldBossType.Simple -> SimpleShieldBoss(
        parseCircularShape(parameters, combine(BOSS, SHAPE)),
        parseColorSchemeItemPart(parameters, BOSS),
    )

    ShieldBossType.Border -> ShieldBossWithBorder(
        parseCircularShape(parameters, combine(BOSS, SHAPE)),
        parseCircularShape(parameters, combine(BOSS, BORDER)),
        parseColorSchemeItemPart(parameters, BOSS),
        parseColorSchemeItemPart(parameters, combine(BOSS, BORDER)),
    )
}
