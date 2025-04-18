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
import at.orchaldir.gm.core.selector.economy.getBusinessesFoundedBy
import at.orchaldir.gm.core.selector.economy.getOpenBusinesses
import at.orchaldir.gm.core.selector.getLanguagesInventedBy
import at.orchaldir.gm.core.selector.getLiving
import at.orchaldir.gm.core.selector.getRacesCreatedBy
import at.orchaldir.gm.core.selector.item.getTextsTranslatedBy
import at.orchaldir.gm.core.selector.item.getTextsWrittenBy
import at.orchaldir.gm.core.selector.magic.getSpellsCreatedBy
import at.orchaldir.gm.core.selector.organization.getExistingOrganizations
import at.orchaldir.gm.core.selector.organization.getOrganizationsFoundedBy
import at.orchaldir.gm.core.selector.util.*
import at.orchaldir.gm.core.selector.world.getBuildingsBuildBy
import at.orchaldir.gm.core.selector.world.getExistingTowns
import at.orchaldir.gm.core.selector.world.getPrisonPlanesCreatedBy
import at.orchaldir.gm.core.selector.world.getTownsFoundedBy
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
) {
    if (!state.isCreator(id)) {
        return
    }

    h2 { +"Created" }

    showList("Buildings", state.getBuildingsBuildBy(id)) { building ->
        link(call, state, building)
    }

    showList("Businesses", state.getBusinessesFoundedBy(id)) { business ->
        link(call, state, business)
    }

    showList("Languages", state.getLanguagesInventedBy(id)) { language ->
        link(call, language)
    }

    showList("Organizations", state.getOrganizationsFoundedBy(id)) { organization ->
        link(call, state, organization)
    }

    showList("Periodicals", getCreatedBy(state.getPeriodicalStorage(), id)) { periodical ->
        link(call, state, periodical)
    }

    showList("Prison Planes", state.getPrisonPlanesCreatedBy(id)) { plane ->
        link(call, state, plane)
    }

    showList("Races", state.getRacesCreatedBy(id)) { race ->
        link(call, state, race)
    }

    showList("Spells", state.getSpellsCreatedBy(id)) { spell ->
        link(call, state, spell)
    }

    showList("Texts Written", state.getTextsWrittenBy(id)) { text ->
        link(call, state, text)
    }

    showList("Texts Translated", state.getTextsTranslatedBy(id)) { text ->
        link(call, state, text)
    }

    showList("Towns", state.getTownsFoundedBy(id)) { town ->
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