package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.BORDER
import at.orchaldir.gm.app.BOSS
import at.orchaldir.gm.app.SHAPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.math.parseCircularShape
import at.orchaldir.gm.app.html.math.selectCircularShape
import at.orchaldir.gm.app.html.math.showCircularShape
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.SHIELD_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.part.SOLID_MATERIALS
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
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
                showItemPart(call, state, boss.main)
            }

            is ShieldBossWithBorder -> {
                showCircularShape(boss.bossShape, "Boss Shape")
                showCircularShape(boss.borderShape, "Border Shape")
                showItemPart(call, state, boss.boss, "Boss")
                showItemPart(call, state, boss.border, "Border")
            }
        }
    }
}

// edit

fun HtmlBlockTag.editShieldBoss(state: State, boss: ShieldBoss) {
    showDetails("Shield Boss", true) {
        selectValue("Type", BOSS, ShieldBossType.entries, boss.getType())

        when (boss) {
            NoShieldBoss -> doNothing()
            is SimpleShieldBoss -> {
                selectCircularShape(boss.shape, combine(BOSS, SHAPE))
                editItemPart(state, boss.main, BOSS, allowedTypes = SHIELD_MATERIALS)
            }

            is ShieldBossWithBorder -> {
                selectCircularShape(boss.bossShape, combine(BOSS, SHAPE), "Boss Shape")
                selectCircularShape(boss.bossShape, combine(BOSS, BORDER), "Border Shape")
                editItemPart(state, boss.boss, BOSS, "Boss", SHIELD_MATERIALS)
                editItemPart(
                    state,
                    boss.border,
                    combine(BOSS, BORDER),
                    "Border",
                    SHIELD_MATERIALS,
                )
            }
        }
    }
}

// parse

fun parseShieldBoss(
    state: State,
    parameters: Parameters,
) = when (parse(parameters, BOSS, ShieldBossType.None)) {
    ShieldBossType.None -> NoShieldBoss
    ShieldBossType.Simple -> SimpleShieldBoss(
        parseCircularShape(parameters, combine(BOSS, SHAPE)),
        parseItemPart(state, parameters, BOSS, SHIELD_MATERIALS),
    )

    ShieldBossType.Border -> ShieldBossWithBorder(
        parseCircularShape(parameters, combine(BOSS, SHAPE)),
        parseCircularShape(parameters, combine(BOSS, BORDER)),
        parseItemPart(state, parameters, BOSS, SHIELD_MATERIALS),
        parseItemPart(state, parameters, combine(BOSS, BORDER), SHIELD_MATERIALS),
    )
}
