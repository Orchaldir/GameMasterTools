package at.orchaldir.gm.app.html.rpg.statblock

import at.orchaldir.gm.app.REFERENCE
import at.orchaldir.gm.app.STATBLOCK
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.parseCharacterTemplateId
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.statblock.*
import at.orchaldir.gm.core.selector.rpg.statblock.getStatblock
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showStatblockLookup(
    call: ApplicationCall,
    state: State,
    lookup: StatblockLookup,
) {
    showDetails("Statblock Lookup", true) {
        field("Type", lookup.getType())

        when (lookup) {
            UndefinedStatblockLookup -> doNothing()
            is UniqueStatblock -> showStatblock(call, state, lookup.statblock)
            is UseStatblockOfTemplate -> {
                val statblock = state.getStatblock(lookup.template)

                fieldLink(call, state, lookup.template)
                field("Cost", statblock.calculateCost(state))
            }

            is ModifyStatblockOfTemplate -> {
                val statblock = state.getStatblock(lookup.template)
                val resolved = lookup.update.resolve(statblock)

                fieldLink(call, state, lookup.template)
                field("Template Cost", statblock.calculateCost(state))
                showStatblockUpdate(call, state, statblock, lookup.update, resolved)
                showStatblock(call, state, resolved)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editStatblockLookup(
    call: ApplicationCall,
    state: State,
    lookup: StatblockLookup,
) {
    showDetails("Statblock Lookup", true) {
        selectValue(
            "Type",
            STATBLOCK,
            StatblockLookupType.entries,
            lookup.getType(),
        )

        when (lookup) {
            UndefinedStatblockLookup -> doNothing()
            is UniqueStatblock -> editStatblock(call, state, lookup.statblock)
            is UseStatblockOfTemplate -> selectElement(
                state,
                combine(STATBLOCK, REFERENCE),
                state.getCharacterTemplateStorage().getAll(),
                lookup.template,
            )

            is ModifyStatblockOfTemplate -> {
                val statblock = state.getStatblock(lookup.template)
                val resolved = lookup.update.resolve(statblock)

                selectElement(
                    state,
                    combine(STATBLOCK, REFERENCE),
                    state.getCharacterTemplateStorage().getAll(),
                    lookup.template,
                )
                field("Template Cost", statblock.calculateCost(state))
                editStatblockUpdate(call, state, statblock, lookup.update, resolved)
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
