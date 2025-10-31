package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.ORNAMENT
import at.orchaldir.gm.app.POMMEL
import at.orchaldir.gm.app.SIZE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.NoPommel
import at.orchaldir.gm.core.model.item.equipment.style.Pommel
import at.orchaldir.gm.core.model.item.equipment.style.PommelType
import at.orchaldir.gm.core.model.item.equipment.style.PommelWithOrnament
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showPommel(
    call: ApplicationCall,
    state: State,
    pommel: Pommel,
) {
    showDetails("Pommel") {
        field("Type", pommel.getType())

        when (pommel) {
            NoPommel -> doNothing()
            is PommelWithOrnament -> {
                showOrnament(call, state, pommel.ornament)
                field("Size", pommel.size)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editPommel(
    state: State,
    pommel: Pommel,
    param: String = POMMEL,
) {
    showDetails("Pommel", true) {
        selectValue("Type", param, PommelType.entries, pommel.getType())

        when (pommel) {
            NoPommel -> doNothing()
            is PommelWithOrnament -> {
                editOrnament(state, pommel.ornament, combine(param, ORNAMENT))
                selectValue(
                    "Size",
                    combine(param, SIZE),
                    Size.entries,
                    pommel.size,
                )
            }
        }
    }
}


// parse

fun parsePommel(
    parameters: Parameters,
    param: String = POMMEL,
) = when (parse(parameters, param, PommelType.Ornament)) {
    PommelType.None -> NoPommel
    PommelType.Ornament -> PommelWithOrnament(
        parseOrnament(parameters, combine(param, ORNAMENT)),
        parse(parameters, combine(param, SIZE), Size.Medium),
    )
}
