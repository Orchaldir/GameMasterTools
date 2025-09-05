package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.character.parseCharacterId
import at.orchaldir.gm.app.html.culture.parseCultureId
import at.orchaldir.gm.app.html.economy.parseBusinessId
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.organization.parseOrganizationId
import at.orchaldir.gm.app.html.realm.parseRealmId
import at.orchaldir.gm.app.html.realm.parseTownId
import at.orchaldir.gm.app.html.religion.parseGodId
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.team.parseTeamId
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.organization.Team
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.selector.character.getLiving
import at.orchaldir.gm.core.selector.economy.getOpenBusinesses
import at.orchaldir.gm.core.selector.organization.getExistingOrganizations
import at.orchaldir.gm.core.selector.organization.getExistingTeams
import at.orchaldir.gm.core.selector.realm.getExistingRealms
import at.orchaldir.gm.core.selector.realm.getExistingTowns
import at.orchaldir.gm.core.selector.util.sortBusinesses
import at.orchaldir.gm.core.selector.util.sortGods
import at.orchaldir.gm.core.selector.util.sortOrganizations
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldReference(
    call: ApplicationCall,
    state: State,
    reference: Reference,
    label: String,
) {
    field(label) {
        showReference(call, state, reference)
    }
}

fun HtmlBlockTag.showReference(
    call: ApplicationCall,
    state: State,
    reference: Reference,
    showUndefined: Boolean = true,
) {
    when (reference) {
        is BusinessReference -> link(call, state, reference.business)
        is CharacterReference -> link(call, state, reference.character)
        is CultureReference -> link(call, state, reference.culture)
        is GodReference -> link(call, state, reference.god)
        is OrganizationReference -> link(call, state, reference.organization)
        is RealmReference -> link(call, state, reference.realm)
        is TeamReference -> link(call, state, reference.team)
        is TownReference -> link(call, state, reference.town)
        NoReference -> +"None"
        UndefinedReference -> if (showUndefined) {
            +"Undefined"
        }

    }
}

// select

fun HtmlBlockTag.selectReference(
    state: State,
    label: String,
    reference: Reference,
    date: Date?,
    param: String,
    allowedTypes: Collection<ReferenceType>,
    filter: (Element<*>) -> Boolean = { true },
) {
    val businesses = state.getOpenBusinesses(date)
        .filter { filter(it) }
    val characters = state.getLiving(date)
        .filter { filter(it) }
    val cultures = state.getCultureStorage()
        .getAll()
        .filter { filter(it) }
    val gods = state.getGodStorage()
        .getAll()
        .filter { filter(it) }
    val organizations = state.getExistingOrganizations(date)
        .filter { filter(it) }
    val realms = state.getExistingRealms(date)
        .filter { filter(it) }
    val teams = state.getExistingTeams(date)
        .filter { filter(it) }
    val towns = state.getExistingTowns(date)
        .filter { filter(it) }

    selectValue("$label Type", param, allowedTypes, reference.getType()) { type ->
        when (type) {
            ReferenceType.None, ReferenceType.Undefined -> false
            ReferenceType.Business -> businesses.isEmpty()
            ReferenceType.Character -> characters.isEmpty()
            ReferenceType.Culture -> cultures.isEmpty()
            ReferenceType.God -> gods.isEmpty()
            ReferenceType.Organization -> organizations.isEmpty()
            ReferenceType.Realm -> realms.isEmpty()
            ReferenceType.Team -> teams.isEmpty()
            ReferenceType.Town -> towns.isEmpty()
        }
    }

    when (reference) {
        is BusinessReference -> selectElement(
            state,
            label,
            combine(param, BUSINESS),
            state.sortBusinesses(businesses),
            reference.business,
        )

        is CharacterReference -> selectElement(
            state,
            label,
            combine(param, CHARACTER),
            characters,
            reference.character,
        )

        is CultureReference -> selectElement(
            state,
            label,
            combine(param, CULTURE),
            cultures,
            reference.culture,
        )

        is GodReference -> selectElement(
            state,
            label,
            combine(param, GOD),
            state.sortGods(gods),
            reference.god,
        )

        is OrganizationReference -> selectElement(
            state,
            label,
            combine(param, ORGANIZATION),
            state.sortOrganizations(organizations),
            reference.organization,
        )

        is RealmReference -> selectElement(
            state,
            label,
            combine(param, REALM),
            realms,
            reference.realm,
        )

        is TeamReference -> selectElement(
            state,
            label,
            combine(param, TEAM),
            teams,
            reference.team,
        )

        is TownReference -> selectElement(
            state,
            label,
            combine(param, TOWN),
            towns,
            reference.town,
        )

        NoReference, UndefinedReference -> doNothing()
    }
}

// parse

fun parseReference(
    parameters: Parameters,
    param: String,
): Reference {
    return when (parse(parameters, param, ReferenceType.Undefined)) {
        ReferenceType.None -> NoReference
        ReferenceType.Undefined -> UndefinedReference
        ReferenceType.Business -> BusinessReference(
            parseBusinessId(parameters, combine(param, BUSINESS)),
        )

        ReferenceType.Character -> CharacterReference(
            parseCharacterId(parameters, combine(param, CHARACTER)),
        )

        ReferenceType.Culture -> CultureReference(
            parseCultureId(parameters, combine(param, CULTURE)),
        )

        ReferenceType.God -> GodReference(
            parseGodId(parameters, combine(param, GOD)),
        )

        ReferenceType.Organization -> OrganizationReference(
            parseOrganizationId(parameters, combine(param, ORGANIZATION))
        )

        ReferenceType.Realm -> RealmReference(
            parseRealmId(parameters, combine(param, REALM)),
        )

        ReferenceType.Team -> TeamReference(
            parseTeamId(parameters, combine(param, TEAM)),
        )

        ReferenceType.Town -> TownReference(
            parseTownId(parameters, combine(param, TOWN)),
        )
    }
}