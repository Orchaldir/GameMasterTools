package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.parseCharacterId
import at.orchaldir.gm.app.html.culture.parseCultureId
import at.orchaldir.gm.app.html.economy.parseBusinessId
import at.orchaldir.gm.app.html.organization.parseOrganizationId
import at.orchaldir.gm.app.html.realm.parseRealmId
import at.orchaldir.gm.app.html.realm.parseTownId
import at.orchaldir.gm.app.html.religion.parseGodId
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
        is TownReference -> link(call, state, reference.town)
        UndefinedReference -> if (showUndefined) {
            +"Undefined"
        }

    }
}

// select

fun <ID : Id<ID>> HtmlBlockTag.selectReference(
    state: State,
    reference: Reference,
    created: ID,
    date: Date?,
    param: String,
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

    selectValue("Type", param, ReferenceType.entries, reference.getType()) { type ->
        when (type) {
            ReferenceType.Undefined -> false
            ReferenceType.Business -> businesses.isEmpty()
            ReferenceType.Character -> characters.isEmpty()
            ReferenceType.Culture -> cultures.isEmpty()
            ReferenceType.God -> gods.isEmpty()
            ReferenceType.Organization -> organizations.isEmpty()
            ReferenceType.Realm -> realms.isEmpty()
            ReferenceType.Town -> towns.isEmpty()
        }
    }

    when (reference) {
        is BusinessReference -> selectElement(
            state,
            combine(param, BUSINESS),
            state.sortBusinesses(businesses),
            reference.business,
        )

        is CharacterReference -> selectElement(
            state,
            combine(param, CHARACTER),
            characters,
            reference.character,
        )

        is CultureReference -> selectElement(
            state,
            combine(param, CULTURE),
            cultures,
            reference.culture,
        )

        is GodReference -> selectElement(
            state,
            combine(param, GOD),
            state.sortGods(gods),
            reference.god,
        )

        is OrganizationReference -> selectElement(
            state,
            combine(param, ORGANIZATION),
            state.sortOrganizations(organizations),
            reference.organization,
        )

        is RealmReference -> selectElement(
            state,
            combine(param, REALM),
            realms,
            reference.realm,
        )

        is TownReference -> selectElement(
            state,
            combine(param, TOWN),
            towns,
            reference.town,
        )

        UndefinedReference -> doNothing()
    }
}

// parse

fun parseReference(
    parameters: Parameters,
    param: String = CREATOR,
): Reference {
    return when (parse(parameters, param, ReferenceType.Undefined)) {
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

        ReferenceType.Town -> TownReference(
            parseTownId(parameters, combine(param, TOWN)),
        )
    }
}