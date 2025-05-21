package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.LANGUAGES
import at.orchaldir.gm.app.ORIGIN
import at.orchaldir.gm.app.PARENT
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
import at.orchaldir.gm.core.model.util.TranslatedOrigin
import at.orchaldir.gm.core.model.util.UndefinedOrigin
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
    displayUndefined: Boolean = true,
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

        is NaturalOrigin -> if (displayUndefined) {
            +"Natural"
        }

        is TranslatedOrigin -> {
            link(call, state, origin.parent)
            +" modified by "
            showCreator(call, state, origin.translator)
        }

        is UndefinedOrigin<*> -> if (displayUndefined) {
            +"Undefined"
        }
    }
}

// edit

fun <ID : Id<ID>> FORM.editOrigin(
    state: State,
    id: ID,
    origin: Origin<ID>,
    elements: Collection<Element<ID>>,
) {
    val possibleParents = elements.filter { it.id() != id }

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

            is TranslatedOrigin -> {
                selectParent(state, possibleParents, origin.parent)
                selectOriginCreator(state, id, origin.translator, origin.date)
                selectOriginDate(state, origin.date)
            }

            is UndefinedOrigin -> doNothing()
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
        parseOptionalDate(parameters, state, DATE),
    )

    OriginType.Evolved -> EvolvedOrigin(
        parseParent(parameters, parseIdFromString),
        parseOptionalDate(parameters, state, DATE),
    )

    OriginType.Combined -> {
        val parents = parseElements(parameters, LANGUAGES) { parseIdFromString(it) }
        CombinedOrigin(
            parents,
            parseOptionalDate(parameters, state, DATE),
        )
    }

    OriginType.Modified -> ModifiedOrigin(
        parseParent(parameters, parseIdFromString),
        parseCreator(parameters),
        parseOptionalDate(parameters, state, DATE),
    )

    OriginType.Natural -> NaturalOrigin(
        parseOptionalDate(parameters, state, DATE),
    )

    OriginType.Translated -> TranslatedOrigin(
        parseParent(parameters, parseIdFromString),
        parseCreator(parameters),
        parseOptionalDate(parameters, state, DATE),
    )

    OriginType.Undefined -> UndefinedOrigin()
}

private fun <ID : Id<ID>> parseParent(parameters: Parameters, parseIdFromString: (String) -> ID) =
    parseId(parameters, combine(ORIGIN, PARENT), parseIdFromString)

private fun <ID : Id<ID>> parseId(
    parameters: Parameters,
    param: String,
    parseIdFromString: (String) -> ID,
) = parseIdFromString(parameters[param] ?: "Error")