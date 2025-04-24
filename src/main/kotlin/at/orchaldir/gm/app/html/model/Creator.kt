package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.character.parseCharacterId
import at.orchaldir.gm.app.html.model.organization.parseOrganizationId
import at.orchaldir.gm.app.html.model.religion.parseGodId
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.economy.parseBusinessId
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.world.parseTownId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.selector.economy.getOpenBusinesses
import at.orchaldir.gm.core.selector.getLiving
import at.orchaldir.gm.core.selector.organization.getExistingOrganizations
import at.orchaldir.gm.core.selector.util.*
import at.orchaldir.gm.core.selector.world.getExistingTowns
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
        is CreatedByGod -> link(call, state, creator.god)
        is CreatedByOrganization -> link(call, state, creator.organization)
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

    showList("Articles", getCreatedBy(state.getArticleStorage(), id)) { article ->
        link(call, state, article)
    }

    showList("Buildings", getCreatedBy(state.getBuildingStorage(), id)) { building ->
        link(call, state, building)
    }

    showList("Businesses", getCreatedBy(state.getBusinessStorage(), id)) { business ->
        link(call, state, business)
    }

    showList("Languages", getCreatedBy(state.getLanguageStorage(), id)) { language ->
        link(call, language)
    }

    showList("Organizations", getCreatedBy(state.getOrganizationStorage(), id)) { organization ->
        link(call, state, organization)
    }

    showList("Planes", getCreatedBy(state.getPlaneStorage(), id)) { plane ->
        link(call, state, plane)
    }

    showList("Races", getCreatedBy(state.getRaceStorage(), id)) { race ->
        link(call, state, race)
    }

    showList("Spells", getCreatedBy(state.getSpellStorage(), id)) { spell ->
        link(call, state, spell)
    }

    showList("Texts", getCreatedBy(state.getTextStorage(), id)) { text ->
        link(call, state, text)
    }

    showList("Towns", getCreatedBy(state.getTownStorage(), id)) { town ->
        link(call, state, town)
    }
}

// select

fun <ID : Id<ID>> HtmlBlockTag.selectCreator(
    state: State,
    creator: Creator,
    created: ID,
    date: Date?,
    noun: String,
) {
    val businesses = state.getOpenBusinesses(date)
        .filter { it.id != created }
    val characters = state.getLiving(date)
    val gods = state.getGodStorage().getAll()
    val towns = state.getExistingTowns(date)
        .filter { it.id != created }
    val organizations = state.getExistingOrganizations(date)
        .filter { it.id != created }

    selectValue("$noun Type", CREATOR, CreatorType.entries, creator.getType(), true) { type ->
        when (type) {
            CreatorType.Undefined -> false
            CreatorType.CreatedByBusiness -> businesses.isEmpty()
            CreatorType.CreatedByCharacter -> characters.isEmpty()
            CreatorType.CreatedByGod -> gods.isEmpty()
            CreatorType.CreatedByOrganization -> organizations.isEmpty()
            CreatorType.CreatedByTown -> towns.isEmpty()
        }
    }

    when (creator) {
        is CreatedByBusiness -> selectElement(
            state,
            noun,
            combine(CREATOR, BUSINESS),
            state.sortBusinesses(businesses),
            creator.business,
            true
        )

        is CreatedByCharacter -> selectElement(
            state,
            noun,
            combine(CREATOR, CHARACTER),
            characters,
            creator.character,
            true
        )

        is CreatedByGod -> selectElement(
            state,
            noun,
            combine(CREATOR, GOD),
            state.sortGods(gods),
            creator.god,
            true
        )

        is CreatedByOrganization -> selectElement(
            state,
            noun,
            combine(CREATOR, ORGANIZATION),
            state.sortOrganizations(organizations),
            creator.organization,
            true
        )

        is CreatedByTown -> selectElement(
            state,
            noun,
            combine(CREATOR, TOWN),
            towns,
            creator.town,
            true
        )

        UndefinedCreator -> doNothing()
    }
}

// parse

fun parseCreator(parameters: Parameters): Creator {
    return when (parse(parameters, CREATOR, CreatorType.Undefined)) {
        CreatorType.Undefined -> UndefinedCreator
        CreatorType.CreatedByBusiness -> CreatedByBusiness(parseBusinessId(parameters, combine(CREATOR, BUSINESS)))
        CreatorType.CreatedByCharacter -> CreatedByCharacter(parseCharacterId(parameters, combine(CREATOR, CHARACTER)))
        CreatorType.CreatedByGod -> CreatedByGod(parseGodId(parameters, combine(CREATOR, GOD)))
        CreatorType.CreatedByOrganization -> CreatedByOrganization(
            parseOrganizationId(
                parameters,
                combine(CREATOR, ORGANIZATION)
            )
        )

        CreatorType.CreatedByTown -> CreatedByTown(parseTownId(parameters, combine(CREATOR, TOWN)))
    }
}