package at.orchaldir.gm.app.html.rpg.statblock

import at.orchaldir.gm.app.REFERENCE
import at.orchaldir.gm.app.STATBLOCK
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.parseCharacterTemplateId
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.statblock.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showStatblockLookup(
    call: ApplicationCall,
    state: State,
    statblock: StatblockLookup,
) {
    showDetails("Statblock", true) {
        field("Type", statblock.getType())

        when (statblock) {
            UndefinedStatblockLookup -> doNothing()
            is UniqueStatblock -> showStatblock(call, state, statblock.statblock)
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
                showStatblock(call, state, resolved)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editStatblockLookup(
    call: ApplicationCall,
    state: State,
    statblock: StatblockLookup,
) {
    showDetails("Statblock", true) {
        selectValue(
            "Type",
            STATBLOCK,
            StatblockLookupType.entries,
            statblock.getType(),
        )

        when (statblock) {
            UndefinedStatblockLookup -> doNothing()
            is UniqueStatblock -> editStatblock(call, state, statblock.statblock)
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

fun parseStatblockLookup(
    state: State,
    parameters: Parameters,
) = when (parse(parameters, STATBLOCK, StatblockLookupType.Undefined)) {
    StatblockLookupType.Unique -> UniqueStatblock(
        parseStatblock(state, parameters),
    )

    StatblockLookupType.UseTemplate -> UseStatblockOfTemplate(
        parseCharacterTemplateId(parameters, combine(STATBLOCK, REFERENCE)),
    )

    StatblockLookupType.ModifyTemplate -> ModifyStatblockOfTemplate(
        parseCharacterTemplateId(parameters, combine(STATBLOCK, REFERENCE)),
        parseStatblockUpdate(state, parameters),
    )

    StatblockLookupType.Undefined -> UndefinedStatblockLookup
}
