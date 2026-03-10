package at.orchaldir.gm.app.html.item.text

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.part.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.BOOK_PROTECTION_MATERIALS
import at.orchaldir.gm.core.model.item.text.book.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showBossesPattern(
    call: ApplicationCall,
    state: State,
    pattern: BossesPattern,
) {
    showDetails("Bosses Pattern") {
        field("Type", pattern.getType())

        when (pattern) {
            NoBosses -> doNothing()
            is SimpleBossesPattern -> {
                field("Shape", pattern.shape)
                field("Size", pattern.size)
                showItemPart(call, state, pattern.boss)
                field("Pattern", pattern.pattern.toString())
            }
        }
    }
}

// edit

fun HtmlBlockTag.editBossesPattern(
    state: State,
    bosses: BossesPattern,
) {
    showDetails("Bosses", true) {
        selectValue("Pattern", BOSSES, BossesPatternType.entries, bosses.getType())

        when (bosses) {
            is NoBosses -> doNothing()
            is SimpleBossesPattern -> {
                selectValue("Bosses Shape", combine(BOSSES, SHAPE), BossesShape.entries, bosses.shape)
                selectValue("Bosses Size", combine(BOSSES, SIZE), Size.entries, bosses.size)
                editItemPart(state, bosses.boss, BOSSES, allowedTypes = BOOK_PROTECTION_MATERIALS)
                selectInt("Bosses Pattern Size", bosses.pattern.size, 1, 20, 1, combine(BOSSES, NUMBER))

                showListWithIndex(bosses.pattern) { index, count ->
                    val countParam = combine(BOSSES, index)
                    selectInt("Count", count, 1, 20, 1, countParam)
                }
            }
        }
    }
}

// parse

fun parseBosses(
    state: State,
    parameters: Parameters,
) = when (parse(parameters, BOSSES, BossesPatternType.None)) {
    BossesPatternType.Simple -> SimpleBossesPattern(
        parseBossesPattern(parameters),
        parse(parameters, combine(BOSSES, SHAPE), BossesShape.Circle),
        parse(parameters, combine(BOSSES, SIZE), Size.Medium),
        parseItemPart(state, parameters, BOSSES, BOOK_PROTECTION_MATERIALS),
    )

    BossesPatternType.None -> NoBosses
}

private fun parseBossesPattern(parameters: Parameters): List<Int> {
    val count = parseInt(parameters, combine(BOSSES, NUMBER), 1)

    return (0..<count)
        .map { index ->
            parseInt(parameters, combine(BOSSES, index), 1)
        }
}
