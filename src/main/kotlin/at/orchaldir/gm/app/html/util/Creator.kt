package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.CREATOR
import at.orchaldir.gm.app.html.fieldElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.ALLOWED_CREATORS
import at.orchaldir.gm.core.model.util.Reference
import at.orchaldir.gm.core.selector.util.getCreatedBy
import at.orchaldir.gm.core.selector.util.isCreator
import at.orchaldir.gm.utils.Id
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun <ID : Id<ID>> HtmlBlockTag.showCreated(
    call: ApplicationCall,
    state: State,
    id: ID,
    alwaysShowTitle: Boolean = false,
) {
    if (!alwaysShowTitle && !state.isCreator(id)) {
        return
    }

    h2 { +"Created" }

    fieldElements(call, state, getCreatedBy(state.getArticleStorage(), id))
    fieldElements(call, state, getCreatedBy(state.getBuildingStorage(), id))
    fieldElements(call, state, getCreatedBy(state.getBusinessStorage(), id))
    fieldElements(call, state, getCreatedBy(state.getCatastropheStorage(), id))
    fieldElements(call, state, getCreatedBy(state.getLanguageStorage(), id))
    fieldElements(call, state, getCreatedBy(state.getMagicTraditionStorage(), id))
    fieldElements(call, state, getCreatedBy(state.getOrganizationStorage(), id))
    fieldElements(call, state, getCreatedBy(state.getPlaneStorage(), id))
    fieldElements(call, state, getCreatedBy(state.getQuoteStorage(), id))
    fieldElements(call, state, getCreatedBy(state.getRaceStorage(), id))
    fieldElements(call, state, getCreatedBy(state.getRealmStorage(), id))
    fieldElements(call, state, getCreatedBy(state.getSpellStorage(), id))
    fieldElements(call, state, getCreatedBy(state.getTextStorage(), id))
    fieldElements(call, state, getCreatedBy(state.getTownStorage(), id))
    fieldElements(call, state, getCreatedBy(state.getTreatyStorage(), id))
}

// select

fun <ID : Id<ID>> HtmlBlockTag.selectCreator(
    state: State,
    creator: Reference,
    created: ID,
    date: Date?,
    noun: String = "Creator",
) = selectReference(state, noun, creator, date, CREATOR, ALLOWED_CREATORS) {
    it.id() != created
}

// parse

fun parseCreator(
    parameters: Parameters,
    param: String = CREATOR,
) = parseReference(parameters, param)