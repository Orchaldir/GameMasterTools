package at.orchaldir.gm.app.html.model.util

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.character.parseCharacterId
import at.orchaldir.gm.app.html.model.culture.parseCultureId
import at.orchaldir.gm.app.html.model.economy.parseBusinessId
import at.orchaldir.gm.app.html.model.organization.parseOrganizationId
import at.orchaldir.gm.app.html.model.realm.parseRealmId
import at.orchaldir.gm.app.html.model.realm.parseTownId
import at.orchaldir.gm.app.html.model.religion.parseGodId
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.selector.character.getLiving
import at.orchaldir.gm.core.selector.economy.getOpenBusinesses
import at.orchaldir.gm.core.selector.organization.getExistingOrganizations
import at.orchaldir.gm.core.selector.realm.getExistingRealms
import at.orchaldir.gm.core.selector.realm.getExistingTowns
import at.orchaldir.gm.core.selector.util.*
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.fieldCreator(
    call: ApplicationCall,
    state: State,
    creator: Creator,
    label: String,
) {
    field(label) {
        showCreator(call, state, creator)
    }
}

fun HtmlBlockTag.showCreator(
    call: ApplicationCall,
    state: State,
    creator: Creator,
    showUndefined: Boolean = true,
) {
    when (creator) {
        is CreatedByBusiness -> link(call, state, creator.business)
        is CreatedByCharacter -> link(call, state, creator.character)
        is CreatedByCulture -> link(call, state, creator.culture)
        is CreatedByGod -> link(call, state, creator.god)
        is CreatedByOrganization -> link(call, state, creator.organization)
        is CreatedByRealm -> link(call, state, creator.realm)
        is CreatedByTown -> link(call, state, creator.town)
        UndefinedCreator -> if (showUndefined) {
            +"Undefined"
        }

    }
}

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

    fieldList(call, state, getCreatedBy(state.getArticleStorage(), id))
    fieldList(call, state, getCreatedBy(state.getBuildingStorage(), id))
    fieldList(call, state, getCreatedBy(state.getBusinessStorage(), id))
    fieldList(call, state, getCreatedBy(state.getCatastropheStorage(), id))
    fieldList(call, state, getCreatedBy(state.getLanguageStorage(), id))
    fieldList(call, state, getCreatedBy(state.getMagicTraditionStorage(), id))
    fieldList(call, state, getCreatedBy(state.getOrganizationStorage(), id))
    fieldList(call, state, getCreatedBy(state.getPlaneStorage(), id))
    fieldList(call, state, getCreatedBy(state.getQuoteStorage(), id))
    fieldList(call, state, getCreatedBy(state.getRaceStorage(), id))
    fieldList(call, state, getCreatedBy(state.getRealmStorage(), id))
    fieldList(call, state, getCreatedBy(state.getSpellStorage(), id))
    fieldList(call, state, getCreatedBy(state.getTextStorage(), id))
    fieldList(call, state, getCreatedBy(state.getTownStorage(), id))
    fieldList(call, state, getCreatedBy(state.getTreatyStorage(), id))
}

// select

fun <ID : Id<ID>> HtmlBlockTag.selectCreator(
    state: State,
    creator: Creator,
    created: ID,
    date: Date?,
    noun: String,
    param: String = CREATOR,
) {
    val businesses = state.getOpenBusinesses(date)
        .filter { it.id != created }
    val characters = state.getLiving(date)
    val cultures = state.getCultureStorage().getAll()
    val gods = state.getGodStorage().getAll()
    val organizations = state.getExistingOrganizations(date)
        .filter { it.id != created }
    val realms = state.getExistingRealms(date)
        .filter { it.id != created }
    val towns = state.getExistingTowns(date)
        .filter { it.id != created }

    selectValue("$noun Type", param, CreatorType.entries, creator.getType()) { type ->
        when (type) {
            CreatorType.Undefined -> false
            CreatorType.CreatedByBusiness -> businesses.isEmpty()
            CreatorType.CreatedByCharacter -> characters.isEmpty()
            CreatorType.CreatedByCulture -> cultures.isEmpty()
            CreatorType.CreatedByGod -> gods.isEmpty()
            CreatorType.CreatedByOrganization -> organizations.isEmpty()
            CreatorType.CreatedByRealm -> realms.isEmpty()
            CreatorType.CreatedByTown -> towns.isEmpty()
        }
    }

    when (creator) {
        is CreatedByBusiness -> selectElement(
            state,
            noun,
            combine(param, BUSINESS),
            state.sortBusinesses(businesses),
            creator.business,
        )

        is CreatedByCharacter -> selectElement(
            state,
            noun,
            combine(param, CHARACTER),
            characters,
            creator.character,
        )

        is CreatedByCulture -> selectElement(
            state,
            noun,
            combine(param, CULTURE),
            cultures,
            creator.culture,
        )

        is CreatedByGod -> selectElement(
            state,
            noun,
            combine(param, GOD),
            state.sortGods(gods),
            creator.god,
        )

        is CreatedByOrganization -> selectElement(
            state,
            noun,
            combine(param, ORGANIZATION),
            state.sortOrganizations(organizations),
            creator.organization,
        )

        is CreatedByRealm -> selectElement(
            state,
            noun,
            combine(param, REALM),
            realms,
            creator.realm,
        )

        is CreatedByTown -> selectElement(
            state,
            noun,
            combine(param, TOWN),
            towns,
            creator.town,
        )

        UndefinedCreator -> doNothing()
    }
}

// parse

fun parseCreator(
    parameters: Parameters,
    param: String = CREATOR,
): Creator {
    return when (parse(parameters, param, CreatorType.Undefined)) {
        CreatorType.Undefined -> UndefinedCreator
        CreatorType.CreatedByBusiness -> CreatedByBusiness(
            parseBusinessId(parameters, combine(param, BUSINESS)),
        )

        CreatorType.CreatedByCharacter -> CreatedByCharacter(
            parseCharacterId(parameters, combine(param, CHARACTER)),
        )

        CreatorType.CreatedByCulture -> CreatedByCulture(
            parseCultureId(parameters, combine(param, CULTURE)),
        )

        CreatorType.CreatedByGod -> CreatedByGod(
            parseGodId(parameters, combine(param, GOD)),
        )

        CreatorType.CreatedByOrganization -> CreatedByOrganization(
            parseOrganizationId(parameters, combine(param, ORGANIZATION))
        )

        CreatorType.CreatedByRealm -> CreatedByRealm(
            parseRealmId(parameters, combine(param, REALM)),
        )

        CreatorType.CreatedByTown -> CreatedByTown(
            parseTownId(parameters, combine(param, TOWN)),
        )
    }
}