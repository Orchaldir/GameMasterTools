package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.BUSINESS
import at.orchaldir.gm.app.CHARACTER
import at.orchaldir.gm.app.CREATOR
import at.orchaldir.gm.app.TOWN
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showList
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.economy.parseBusinessId
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseCharacterId
import at.orchaldir.gm.app.parse.world.parseTownId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.selector.economy.getBusinessesFoundedBy
import at.orchaldir.gm.core.selector.economy.getOpenBusinesses
import at.orchaldir.gm.core.selector.getLanguagesInventedBy
import at.orchaldir.gm.core.selector.getLiving
import at.orchaldir.gm.core.selector.item.getTextsTranslatedBy
import at.orchaldir.gm.core.selector.item.getTextsWrittenBy
import at.orchaldir.gm.core.selector.organization.getOrganizationsFoundedBy
import at.orchaldir.gm.core.selector.util.isCreator
import at.orchaldir.gm.core.selector.world.getBuildingsBuildBy
import at.orchaldir.gm.core.selector.world.getExistingTowns
import at.orchaldir.gm.core.selector.world.getTownsFoundedBy
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
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

fun <ID : Id<ID>> FORM.selectCreator(
    state: State,
    creator: Creator,
    created: ID,
    date: Date?,
    noun: String,
) {
    val businesses = state.getOpenBusinesses(date)
        .filter { it.id != created }
    val characters = state.getLiving(date)
    val towns = state.getExistingTowns(date)

    selectValue("$noun Type", CREATOR, CreatorType.entries, creator.getType(), true) { type ->
        when (type) {
            CreatorType.Undefined -> false
            CreatorType.CreatedByBusiness -> businesses.isEmpty()
            CreatorType.CreatedByCharacter -> characters.isEmpty()
            CreatorType.CreatedByTown -> towns.isEmpty()
        }
    }

    when (creator) {
        is CreatedByBusiness -> selectValue(
            noun,
            combine(CREATOR, BUSINESS),
            businesses,
            true
        ) { business ->
            label = business.name(state)
            value = business.id.value.toString()
            selected = creator.business == business.id
        }

        is CreatedByCharacter -> selectValue(
            noun,
            combine(CREATOR, CHARACTER),
            characters,
            true
        ) { character ->
            label = character.name(state)
            value = character.id.value.toString()
            selected = creator.character == character.id
        }

        is CreatedByTown -> selectValue(
            noun,
            combine(CREATOR, TOWN),
            towns,
            true
        ) { town ->
            label = town.name(state)
            value = town.id.value.toString()
            selected = creator.town == town.id
        }

        UndefinedCreator -> doNothing()
    }
}

// parse

fun parseCreator(parameters: Parameters): Creator {
    return when (parse(parameters, CREATOR, CreatorType.Undefined)) {
        CreatorType.Undefined -> UndefinedCreator
        CreatorType.CreatedByBusiness -> CreatedByBusiness(parseBusinessId(parameters, combine(CREATOR, BUSINESS)))
        CreatorType.CreatedByCharacter -> CreatedByCharacter(parseCharacterId(parameters, combine(CREATOR, CHARACTER)))
        CreatorType.CreatedByTown -> CreatedByTown(parseTownId(parameters, combine(CREATOR, TOWN)))
    }
}