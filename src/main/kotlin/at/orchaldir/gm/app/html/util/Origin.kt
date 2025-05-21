package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.LANGUAGES
import at.orchaldir.gm.app.ORIGIN
import at.orchaldir.gm.app.PARENT
import at.orchaldir.gm.app.RACE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.CombinedOrigin
import at.orchaldir.gm.core.model.util.CreatedOrigin
import at.orchaldir.gm.core.model.util.Creator
import at.orchaldir.gm.core.model.util.EvolvedOrigin
import at.orchaldir.gm.core.model.util.ModifiedOrigin
import at.orchaldir.gm.core.model.util.NaturalOrigin
import at.orchaldir.gm.core.model.util.Origin
import at.orchaldir.gm.core.model.util.OriginType
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun <ID : Id<ID>> HtmlBlockTag.showOrigin(
    call: ApplicationCall,
    state: State,
    origin: Origin<ID>,
) {
    field("Origin") {
        displayOrigin(call, state, origin)
    }
    optionalField(call, state, "Date", origin.date())
}

fun <ID : Id<ID>> HtmlBlockTag.displayOrigin(
    call: ApplicationCall,
    state: State,
    origin: Origin<ID>,
    displayNatural: Boolean = true,
) {
    when (origin) {
        is CombinedOrigin -> {
            +"Combines "

            showInlineList(origin.parents) { parent ->
                link(call, state, parent)
            }
        }

        is CreatedOrigin -> {
            +"Created by "
            showCreator(call, state, origin.creator)
        }

        is EvolvedOrigin -> {
            +"Evolved from "
            link(call, state, origin.parent)
        }

        is ModifiedOrigin -> {
            link(call, state, origin.parent)
            +" modified by "
            showCreator(call, state, origin.modifier)
        }

        is NaturalOrigin -> if (displayNatural) {
            +"Natural"
        }
    }
}

// edit

fun <ID : Id<ID>> FORM.editOrigin(
    state: State,
    id: ID,
    origin: Origin<ID>,
) {
    val possibleParents = state.getStorage(id)
        .getAllExcept(id)

    showDetails("Origin", true) {
        selectValue("Type", ORIGIN, OriginType.entries, origin.getType()) {
            when (it) {
                OriginType.Combined -> possibleParents.size < 2
                OriginType.Evolved, OriginType.Modified -> possibleParents.isEmpty()
                else -> false
            }
        }

        when (origin) {
            is CombinedOrigin -> {
                selectElements(state, PARENT, possibleParents, origin.parents)
                selectOriginDate(state, origin.date)
            }

            is CreatedOrigin -> {
                selectOriginCreator(state, id, origin.creator, origin.date)
                selectOriginDate(state, origin.date)
            }

            is EvolvedOrigin -> {
                selectParent(state, possibleParents, origin.parent)
                selectOriginDate(state, origin.date)
            }

            is ModifiedOrigin -> {
                selectParent(state, possibleParents, origin.parent)
                selectOriginCreator(state, id, origin.modifier, origin.date)
                selectOriginDate(state, origin.date)
            }

            is NaturalOrigin -> selectOriginDate(state, origin.date)
        }
    }
}

private fun <ID : Id<ID>> DETAILS.selectOriginCreator(
    state: State,
    id: ID,
    creator: Creator,
    date: Date?,
) {
    selectCreator(state, creator, id, date, "Creator")
}

private fun HtmlBlockTag.selectOriginDate(
    state: State,
    date: Date?,
) {
    selectOptionalDate(state, "Date", date, DATE)
}

private fun <ID : Id<ID>> DETAILS.selectParent(
    state: State,
    possibleParents: List<Element<ID>>,
    parent: ID,
) {
    selectElement(
        state,
        "Parent",
        combine(ORIGIN, PARENT),
        possibleParents,
        parent,
    )
}

// parse

fun <ID : Id<ID>> parseOrigin(
    parameters: Parameters,
    state: State,
    parseIdFromString: (String) -> ID,
) = when (parse(parameters, ORIGIN, OriginType.Natural)) {
    OriginType.Created -> CreatedOrigin(
        parseCreator(parameters),
        parseDate(parameters, state, DATE),
    )

    OriginType.Evolved -> EvolvedOrigin(
        parseId(parameters, combine(ORIGIN, RACE), parseIdFromString),
        parseDate(parameters, state, DATE),
    )

    OriginType.Combined -> {
        val parents = parseElements(parameters, LANGUAGES) { parseIdFromString(it) }
        CombinedOrigin(
            parents,
            parseDate(parameters, state, DATE),
        )
    }

    OriginType.Modified -> ModifiedOrigin(
        parseId(parameters, combine(ORIGIN, RACE), parseIdFromString),
        parseCreator(parameters),
        parseDate(parameters, state, DATE),
    )

    OriginType.Natural -> NaturalOrigin(
        parseDate(parameters, state, DATE),
    )
}

private fun <ID : Id<ID>> parseId(
    parameters: Parameters,
    param: String,
    parseIdFromString: (String) -> ID,
) = parseIdFromString(parameters[param] ?: "Error")