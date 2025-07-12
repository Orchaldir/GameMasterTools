package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.ORIGIN
import at.orchaldir.gm.app.REFERENCE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.Creator
import at.orchaldir.gm.core.model.util.origin.*
import at.orchaldir.gm.core.selector.util.getExistingElements
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun <ID : Id<ID>> HtmlBlockTag.fieldOrigin(
    call: ApplicationCall,
    state: State,
    origin: Origin,
    createId: (Int) -> ID,
) {
    field("Origin") {
        showOrigin(call, state, origin, createId, true)
    }
}

fun <ID : Id<ID>> HtmlBlockTag.showOrigin(
    call: ApplicationCall,
    state: State,
    origin: Origin,
    createId: (Int) -> ID,
    showUndefined: Boolean = false,
) {
    when (origin) {
        is CreatedElement -> {
            +"Created by "
            showCreator(call, state, origin.creator)
        }

        is EvolvedElement -> {
            +"Evolved from "
            link(call, state, createId(origin.parent))
        }

        is ModifiedElement -> showCreatorAndParent(call, state, origin.modifier, createId(origin.parent), "modified")
        is OriginalElement -> +"Original"
        is UndefinedOrigin -> if (showUndefined) {
            +"Undefined"
        } else {
            doNothing()
        }
    }
}

private fun <ID : Id<ID>> HtmlBlockTag.showCreatorAndParent(
    call: ApplicationCall,
    state: State,
    creator: Creator,
    original: ID,
    verb: String,
) {
    link(call, state, original)
    +" $verb by "
    showCreator(call, state, creator)
}

// edit

fun <ID : Id<ID>> HtmlBlockTag.editOrigin(
    state: State,
    id: ID,
    origin: Origin,
    date: Date?,
    createId: (Int) -> ID,
) {
    val availableParents = state.getExistingElements(state.getStorage(id), date)
        .filter { it.id() != id }

    selectValue("Origin Type", ORIGIN, OriginType.entries, origin.getType()) { type ->
        when (type) {
            OriginType.Evolved, OriginType.Modified -> availableParents.isEmpty()
            else -> false
        }
    }

    when (origin) {
        is CreatedElement -> selectCreator(state, id, origin.creator, date)
        is EvolvedElement -> selectParent(state, availableParents, createId(origin.parent))
        is ModifiedElement -> selectInventorAndOriginal(
            state,
            id,
            availableParents,
            origin.modifier,
            createId(origin.parent),
            date,
        )

        is OriginalElement -> doNothing()
        is UndefinedOrigin -> doNothing()
    }
}

private fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.selectInventorAndOriginal(
    state: State,
    id: ID,
    availableParents: List<ELEMENT>,
    creator: Creator,
    parent: ID,
    date: Date?,
) {
    selectCreator(state, id, creator, date)
    selectParent(state, availableParents, parent)
}

private fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.selectParent(
    state: State,
    availableParents: List<ELEMENT>,
    parent: ID,
) {
    selectElement(
        state,
        "Parent",
        combine(ORIGIN, REFERENCE),
        availableParents,
        parent,
    )
}

private fun <ID : Id<ID>> HtmlBlockTag.selectCreator(
    state: State,
    id: ID,
    creator: Creator,
    date: Date?,
) {
    selectCreator(state, creator, id, date, "Creator")
}

// parse

fun parseOrigin(parameters: Parameters) =
    when (parse(parameters, ORIGIN, OriginType.Undefined)) {
        OriginType.Created -> CreatedElement(parseCreator(parameters))
        OriginType.Evolved -> EvolvedElement(
            parseInt(parameters, combine(ORIGIN, REFERENCE)),
        )

        OriginType.Modified -> ModifiedElement(
            parseInt(parameters, combine(ORIGIN, REFERENCE)),
            parseCreator(parameters),
        )

        OriginType.Original -> OriginalElement()
        OriginType.Undefined -> UndefinedOrigin()
    }
