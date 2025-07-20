package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.FATHER
import at.orchaldir.gm.app.MOTHER
import at.orchaldir.gm.app.ORIGIN
import at.orchaldir.gm.app.REFERENCE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.Creator
import at.orchaldir.gm.core.model.util.origin.*
import at.orchaldir.gm.core.selector.character.getPossibleFathers
import at.orchaldir.gm.core.selector.character.getPossibleMothers
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
        is BornElement -> {
            val parents = listOfNotNull(origin.father, origin.mother)

            field("Origin") {
                if (parents.isEmpty()) {
                    +"Born"
                } else {
                    +"Born to "
                }
                showInlineList(parents) { parent ->
                    link(call, state, createId(parent))
                }
            }
        }
        is CombinedElement -> {
            +"Combines "

            showInlineList(origin.parents) { parent ->
                link(call, state, createId(parent))
            }
        }

        is CreatedElement -> {
            +"Created by "
            showCreator(call, state, origin.creator)
        }

        is EvolvedElement -> {
            +"Evolved from "
            link(call, state, createId(origin.parent))
        }

        is ModifiedElement -> showCreatorAndParent(call, state, origin.modifier, createId(origin.parent), "modified")
        OriginalElement -> +"Original"
        PlanarOrigin -> +"Planar"
        is TranslatedElement -> showCreatorAndParent(
            call,
            state,
            origin.translator,
            createId(origin.parent),
            "translated"
        )

        UndefinedOrigin -> if (showUndefined) {
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
    allowedTypes: Collection<OriginType>,
    createId: (Int) -> ID,
) {
    val availableParents = state.getExistingElements(state.getStorage(id), date)
        .filter { it.id() != id }

    selectValue("Origin Type", ORIGIN, allowedTypes, origin.getType()) { type ->
        when (type) {
            OriginType.Combined -> availableParents.size < 2
            OriginType.Evolved, OriginType.Modified, OriginType.Translated -> availableParents.isEmpty()
            else -> false
        }
    }

    when (origin) {
        is BornElement -> if (id is CharacterId) {
            selectOptionalElement(
                state,
                "Father",
                FATHER,
                state.getPossibleFathers(id),
                origin.father?.let { CharacterId(it) },
            )
            selectOptionalElement(
                state,
                "Mother",
                MOTHER,
                state.getPossibleMothers(id),
                origin.mother?.let { CharacterId(it) },
            )
        } else {
            error("BornElement is only supported by characters!")
        }

        is CombinedElement -> {
            selectElements(
                state,
                combine(ORIGIN, REFERENCE),
                availableParents,
                origin.parents
                    .map(createId)
                    .toSet(),
            )
            selectCreator(state, id, origin.creator, date)
        }

        is CreatedElement -> selectCreator(state, id, origin.creator, date)
        is EvolvedElement -> selectParent(state, availableParents, createId(origin.parent))
        is ModifiedElement -> selectCreatorAndParent(
            state,
            id,
            availableParents,
            origin.modifier,
            createId(origin.parent),
            date,
        )

        OriginalElement -> doNothing()
        PlanarOrigin -> doNothing()
        is TranslatedElement -> selectCreatorAndParent(
            state,
            id,
            availableParents,
            origin.translator,
            createId(origin.parent),
            date,
        )

        UndefinedOrigin -> doNothing()
    }
}

private fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.selectCreatorAndParent(
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
        OriginType.Born -> {
            val father = parseInt(parameters, FATHER)
            val mother = parseInt(parameters, MOTHER)
            BornElement(mother, father)
        }

        OriginType.Combined -> CombinedElement(
            parseElements(parameters, combine(ORIGIN, REFERENCE)) { it.toInt() },
            parseCreator(parameters),
        )

        OriginType.Created -> CreatedElement(parseCreator(parameters))
        OriginType.Evolved -> EvolvedElement(
            parseInt(parameters, combine(ORIGIN, REFERENCE)),
        )

        OriginType.Modified -> ModifiedElement(
            parseInt(parameters, combine(ORIGIN, REFERENCE)),
            parseCreator(parameters),
        )

        OriginType.Original -> OriginalElement
        OriginType.Planar -> PlanarOrigin

        OriginType.Translated -> TranslatedElement(
            parseInt(parameters, combine(ORIGIN, REFERENCE)),
            parseCreator(parameters),
        )

        OriginType.Undefined -> UndefinedOrigin
    }
