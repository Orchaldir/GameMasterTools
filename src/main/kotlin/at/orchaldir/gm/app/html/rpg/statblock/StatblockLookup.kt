package at.orchaldir.gm.app.html.rpg.statblock

import at.orchaldir.gm.app.REFERENCE
import at.orchaldir.gm.app.STATBLOCK
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.parseCharacterTemplateId
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterTemplateId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.rpg.statblock.*
import at.orchaldir.gm.core.selector.rpg.statblock.getStatblock
import at.orchaldir.gm.core.selector.util.sortCharacterTemplates
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showStatblockLookup(
    call: ApplicationCall,
    state: State,
    lookup: StatblockLookup,
    showUndefined: Boolean = false,
) {
    when (lookup) {
        is UniqueStatblock -> +"Unique"
        is UseStatblockOfTemplate -> {
            +"Use "
            optionalLink(call, state, lookup.template())
        }

        is ModifyStatblockOfTemplate -> {
            +"Modify "
            optionalLink(call, state, lookup.template())
        }

        UndefinedStatblockLookup -> if (showUndefined) {
            +"Undefined"
        }
    }
}

fun HtmlBlockTag.showStatblockLookupDetails(
    call: ApplicationCall,
    state: State,
    race: RaceId,
    lookup: StatblockLookup,
) = showStatblockLookupDetails(
    call,
    state,
    state.getRaceStorage().getOrThrow(race).lifeStages.statblock(),
    lookup,
)

fun HtmlBlockTag.showStatblockLookupDetails(
    call: ApplicationCall,
    state: State,
    base: Statblock,
    lookup: StatblockLookup,
) {
    showDetails("Statblock Lookup", true) {
        field("Type", lookup.getType())

        when (lookup) {
            UndefinedStatblockLookup -> doNothing()
            is UniqueStatblock -> {
                val resolved = lookup.statblock.applyTo(base)
                showStatblockUpdate(call, state, base, lookup.statblock, resolved)
                showStatblock(call, state, resolved)
            }

            is UseStatblockOfTemplate -> {
                val statblock = state.getStatblock(base, lookup.template)

                fieldLink(call, state, lookup.template)
                field("Cost", statblock.calculateCost(state))
            }

            is ModifyStatblockOfTemplate -> {
                val statblock = state.getStatblock(base, lookup.template)
                val resolved = lookup.update.applyTo(statblock)

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
    race: RaceId,
    lookup: StatblockLookup,
    ignoredTemplates: Set<CharacterTemplateId> = emptySet(),
) = editStatblockLookup(
    call,
    state,
    state.getRaceStorage().getOrThrow(race).lifeStages.statblock(),
    lookup,
    ignoredTemplates,
)


fun HtmlBlockTag.editStatblockLookup(
    call: ApplicationCall,
    state: State,
    statblock: Statblock,
    lookup: StatblockLookup,
    ignoredTemplates: Set<CharacterTemplateId> = emptySet(),
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
            is UniqueStatblock -> {
                val resolved = lookup.statblock.applyTo(statblock)

                editStatblockUpdate(call, state, statblock, lookup.statblock, resolved)
            }

            is UseStatblockOfTemplate -> selectCharacterTemplate(state, ignoredTemplates, lookup.template)
            is ModifyStatblockOfTemplate -> {
                val statblock = state.getStatblock(statblock, lookup.template)
                val resolved = lookup.update.applyTo(statblock)

                selectCharacterTemplate(state, ignoredTemplates, lookup.template)
                field("Template Cost", statblock.calculateCost(state))
                editStatblockUpdate(call, state, statblock, lookup.update, resolved)
                field("Cost", resolved.calculateCost(state))
            }
        }
    }
}

private fun DETAILS.selectCharacterTemplate(
    state: State,
    ignoredTemplates: Set<CharacterTemplateId>,
    templateId: CharacterTemplateId,
) {
    val templates = state.getCharacterTemplateStorage()
        .getAll()
        .filter { !ignoredTemplates.contains(it.id) }
    selectElement(
        state,
        combine(STATBLOCK, REFERENCE),
        state.sortCharacterTemplates(templates),
        templateId,
    )
}

// parse

fun parseStatblockLookup(
    state: State,
    parameters: Parameters,
) = when (parse(parameters, STATBLOCK, StatblockLookupType.Undefined)) {
    StatblockLookupType.Unique -> UniqueStatblock(
        parseStatblockUpdate(state, parameters),
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
