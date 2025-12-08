package at.orchaldir.gm.app.html.rpg.statblock

import at.orchaldir.gm.app.REFERENCE
import at.orchaldir.gm.app.STATBLOCK
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.parseCharacterTemplateId
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.statblock.CharacterStatblock
import at.orchaldir.gm.core.model.rpg.statblock.CharacterStatblockType
import at.orchaldir.gm.core.model.rpg.statblock.ModifyStatblockOfTemplate
import at.orchaldir.gm.core.model.rpg.statblock.StatblockUpdate
import at.orchaldir.gm.core.model.rpg.statblock.UndefinedCharacterStatblock
import at.orchaldir.gm.core.model.rpg.statblock.UniqueCharacterStatblock
import at.orchaldir.gm.core.model.rpg.statblock.UseStatblockOfTemplate
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
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
            is UseStatblockOfTemplate -> {
                val template = state.getCharacterTemplateStorage().getOrThrow(statblock.template)

                fieldLink(call, state, statblock.template)
                field("Cost", template.statblock.calculateCost(state))
            }
            is ModifyStatblockOfTemplate -> {
                val template = state.getCharacterTemplateStorage().getOrThrow(statblock.template)
                val resolved = statblock.update.resolve(template.statblock)

                fieldLink(call, state, statblock.template)
                field("Template Cost", template.statblock.calculateCost(state))
                showStatblockUpdate(call, state, template.statblock, statblock.update, resolved)
                field("Cost", resolved.calculateCost(state))
            }
        }
    }
}

// edit

fun HtmlBlockTag.editCharacterStatblock(
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

            is ModifyStatblockOfTemplate -> {
                val template = state.getCharacterTemplateStorage().getOrThrow(statblock.template)
                val resolved = statblock.update.resolve(template.statblock)

                selectElement(
                    state,
                    combine(STATBLOCK, REFERENCE),
                    state.getCharacterTemplateStorage().getAll(),
                    statblock.template,
                )
                field("Template Cost", template.statblock.calculateCost(state))
                editStatblockUpdate(call, state, template.statblock, statblock.update, resolved)
                field("Cost", resolved.calculateCost(state))
            }
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
    CharacterStatblockType.ModifiedTemplate -> ModifyStatblockOfTemplate(
        parseCharacterTemplateId(parameters, combine(STATBLOCK, REFERENCE)),
        parseStatblockUpdate(state, parameters),
    )

    CharacterStatblockType.Undefined -> UndefinedCharacterStatblock
}
