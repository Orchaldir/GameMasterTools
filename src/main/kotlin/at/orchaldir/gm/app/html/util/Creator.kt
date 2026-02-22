package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.CREATOR
import at.orchaldir.gm.app.html.fieldElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.ALLOWED_CREATORS
import at.orchaldir.gm.core.model.util.Reference
import at.orchaldir.gm.core.selector.item.getTextsPublishedBy
import at.orchaldir.gm.core.selector.util.getCreatedBy
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
    val articles = getCreatedBy(state.getArticleStorage(), id)
    val buildings = getCreatedBy(state.getBuildingStorage(), id)
    val businesses = getCreatedBy(state.getBusinessStorage(), id)
    val catastrophes = getCreatedBy(state.getCatastropheStorage(), id)
    val magicTraditions = getCreatedBy(state.getMagicTraditionStorage(), id)
    val languages = getCreatedBy(state.getLanguageStorage(), id)
    val organizations = getCreatedBy(state.getOrganizationStorage(), id)
    val planes = getCreatedBy(state.getPlaneStorage(), id)
    val quotes = getCreatedBy(state.getQuoteStorage(), id)
    val races = getCreatedBy(state.getRaceStorage(), id)
    val realms = getCreatedBy(state.getRealmStorage(), id)
    val spells = getCreatedBy(state.getSpellStorage(), id)
    val texts = getCreatedBy(state.getTextStorage(), id)
    val publishedTexts = state.getTextsPublishedBy(id)
    val settlements = getCreatedBy(state.getSettlementStorage(), id)
    val treaties = getCreatedBy(state.getTreatyStorage(), id)

    if (!alwaysShowTitle && articles.isEmpty() && buildings.isEmpty() && businesses.isEmpty() && catastrophes.isEmpty() && magicTraditions.isEmpty() &&
        languages.isEmpty() && organizations.isEmpty() && planes.isEmpty() && quotes.isEmpty() && races.isEmpty() && realms.isEmpty() && spells.isEmpty() &&
        texts.isEmpty() && publishedTexts.isEmpty() && settlements.isEmpty() && treaties.isEmpty()
    ) {
        return
    }

    h2 { +"Created" }

    fieldElements(call, state, articles)
    fieldElements(call, state, buildings)
    fieldElements(call, state, businesses)
    fieldElements(call, state, catastrophes)
    fieldElements(call, state, languages)
    fieldElements(call, state, magicTraditions)
    fieldElements(call, state, organizations)
    fieldElements(call, state, planes)
    fieldElements(call, state, quotes)
    fieldElements(call, state, races)
    fieldElements(call, state, realms)
    fieldElements(call, state, spells)
    fieldElements(call, state, texts)
    fieldElements(call, state, "Published Texts", publishedTexts)
    fieldElements(call, state, settlements)
    fieldElements(call, state, treaties)
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