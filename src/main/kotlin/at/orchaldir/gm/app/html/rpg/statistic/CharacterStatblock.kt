package at.orchaldir.gm.app.html.rpg.statistic

import at.orchaldir.gm.app.REFERENCE
import at.orchaldir.gm.app.STATBLOCK
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.parseCharacterTemplateId
import at.orchaldir.gm.app.html.rpg.editStatblock
import at.orchaldir.gm.app.html.rpg.parseStatblock
import at.orchaldir.gm.app.html.rpg.showStatblock
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.statistic.CharacterStatblock
import at.orchaldir.gm.core.model.rpg.statistic.CharacterStatblockType
import at.orchaldir.gm.core.model.rpg.statistic.UndefinedCharacterStatblock
import at.orchaldir.gm.core.model.rpg.statistic.UniqueCharacterStatblock
import at.orchaldir.gm.core.model.rpg.statistic.UseStatblockOfTemplate
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showCharacterStatblock(
    call: ApplicationCall,
    state: State,
    statblock: CharacterStatblock,
) {
    showDetails("Statblock", true) {
        field("Type", statblock.getType())

        when (statblock) {
            UndefinedCharacterStatblock -> doNothing()
            is UniqueCharacterStatblock -> showStatblock(call, state, statblock.statblock)
            is UseStatblockOfTemplate -> fieldLink(call, state, statblock.template)
        }
    }
}

// edit

fun FORM.editCharacterStatblock(
    call: ApplicationCall,
    state: State,
    statblock: CharacterStatblock,
) {
    showDetails("Statblock", true) {
        selectValue(
            "Type",
            STATBLOCK,
            CharacterStatblockType.entries,
            statblock.getType(),
        )

        when (statblock) {
            UndefinedCharacterStatblock -> doNothing()
            is UniqueCharacterStatblock -> editStatblock(call, state, statblock.statblock)
            is UseStatblockOfTemplate -> selectElement(
                state,
                combine(STATBLOCK, REFERENCE),
                state.getCharacterTemplateStorage().getAll(),
                statblock.template,
            )
        }
    }
}

// parse

fun parseCharacterStatblock(
    state: State,
    parameters: Parameters,
) = when (parse(parameters, STATBLOCK, CharacterStatblockType.Undefined)) {
    CharacterStatblockType.Statblock -> UniqueCharacterStatblock(
        parseStatblock(state, parameters),
    )

    CharacterStatblockType.Template -> UseStatblockOfTemplate(
        parseCharacterTemplateId(parameters, combine(STATBLOCK, REFERENCE)),
    )

    CharacterStatblockType.Undefined -> UndefinedCharacterStatblock
}
